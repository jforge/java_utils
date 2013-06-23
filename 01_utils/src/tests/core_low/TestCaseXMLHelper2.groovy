package tests.core_low

import com.mplify.logging.LogFacilities;

import static org.junit.Assert.*

import org.jdom.Comment
import org.jdom.Element
import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.mplify.junit.TestStarter
import com.mplify.xml.JDomHelper
import com.mplify.xml.XMLParsing

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2013, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * 
 *
 * 2013.02.18 - Test handling of 
 ******************************************************************************/

class TestCaseXMLHelper2 extends TestStarter {

    private final static String CLASS = TestCaseXMLHelper2.class.getName()

    /**
     * Does stringification-for-database correctly throw away irrelevant details? 
     */

    @Test
    void testStringificationForDatabase() {
        Logger logger = LoggerFactory.getLogger("${CLASS}.testStringificationForDatabase")
        //
        // a map listing as
        // key: the XML input
        // value: the XML expected after "serialization for database"
        //
        def map = [
            '<routing/>' : '<routing />' , // adds a space before the end
            '<routing />' : '<routing />' , // element is left as is "as is"
            '<!-- A --><routing /><!-- B -->' : '<routing />' , // element is left as is "as is", but commentary around is lost as it belongs to the DOCUMENT
            '<routing> <alpha> </alpha> </routing>' : '<routing> <alpha> </alpha> </routing>', // element is left as is "as is" because of whitespace
            '<routing><alpha></alpha></routing>' : '<routing><alpha /></routing>', // inner element collapses
            '<routing></routing>' : '<routing />' , // collapses
            '<routing>\n</routing>' : '<routing>\r\n</routing>', // LF replaced by CRLF
            '<routing><!-- some commentary --></routing>' : '<routing><!-- some commentary --></routing>' ] // commentary left "as is"
        map.each {
            Element r = XMLParsing.extractToplevelElement(it.key, "routing")
            String dbString = JDomHelper.stringifyForDatabase(r);
            logger.info("Stringified " + LogFacilities.mangleString(it.key) + " for database, which gives " + LogFacilities.mangleString(dbString))
            assertEquals(it.value, dbString)
        }
    }

    /**
     * Does stringification-for-canonicization correctly throw away irrelevant details and the comments
     */

    @Test
    void testStringificationForCanonicization() {
        Logger logger = LoggerFactory.getLogger("${CLASS}.testStringificationForCanonicization")
        //
        // a map listing as
        // key: the XML input
        // value: the XML expected after "serialization for database"
        //
        def map = [
            '<routing/>' : '<routing />' , // adds a space before the end
            '<routing />' : '<routing />' , // element is left as is "as is"
            '<!-- A --><routing /><!-- B -->' : '<routing />' , // element is left as is "as is", but commentary around is lost as it belongs to the DOCUMENT
            '<routing> <alpha> </alpha> </routing>' : '<routing><alpha /></routing>', // inner element collapses
            '<routing><alpha></alpha></routing>' : '<routing><alpha /></routing>', // inner element collapses
            '<routing></routing>' : '<routing />' , // collapses
            '<routing></routing>' : '<routing />', // collapses
            '<routing><!-- some commentary --></routing>' : '<routing />', // commentary has been removed
            '<!-- GAMMA --><routing><!-- ALPHA --></routing><!-- BETA -->' : '<routing />', // commentary has been entireldremoved
            '<routing attr1="a"><sub1 attr2="b">\n\r<!-- HALLO --><sub2 attr3="c"> \n   </sub2>\r\r</sub1> <!-- BIG AL --> </routing>' : '<routing attr1="a"><sub1 attr2="b"><sub2 attr3="c" /></sub1></routing>' ] // collapses
        map.each {
            Element r = XMLParsing.extractToplevelElement(it.key, "routing")
            StringBuilder buf = new StringBuilder()
            String hash = JDomHelper.hashElement(r, buf)
            logger.info("Hashed " + LogFacilities.mangleString(it.key) + " , which gives ${hash} and the linear representation " + LogFacilities.mangleString(buf as String))
        }
    }

    /**
     * Does hashing correctly work
     */

    @Test
    void testHashing() {
        Logger logger = LoggerFactory.getLogger("${CLASS}.testHashing")
        //
        // a map listing as
        // key: the XML input
        // value: the XML expected after "serialization for database"
        //
        def map = [
            '<routing/>' : '91C1F648A70665233B94AB865BE4AA67691FC62C' ,
            '<routing />' : '91C1F648A70665233B94AB865BE4AA67691FC62C' ,
            '<!-- A --><routing /><!-- B -->' : '91C1F648A70665233B94AB865BE4AA67691FC62C' ,
            '<routing> <alpha> </alpha> </routing>' : 'EF679C3AAE05EC24EAEF2B05B8B00857C05828EA',
            '<routing><alpha></alpha></routing>' : 'EF679C3AAE05EC24EAEF2B05B8B00857C05828EA',
            '<routing><!-- some commentary --></routing>' : '91C1F648A70665233B94AB865BE4AA67691FC62C', // commentary has been removed
            '<!-- GAMMA --><routing><!-- ALPHA --></routing><!-- BETA -->' : '91C1F648A70665233B94AB865BE4AA67691FC62C', // commentary has been entireldremoved
            '<routing attr1="a"><sub1 attr2="b">\n\r<!-- HALLO --><sub2 attr3="c"> \n   </sub2>\r\r</sub1> <!-- BIG AL --> </routing>' : 'B1E494CC8E8B96FFACDACD969E2D8F933ADCD90C' ] // collapses
        map.each {
            //
            // Simply hash, see whether it's the expected value
            //
            Element r
            String hash
            A: {
                r = XMLParsing.extractToplevelElement(it.key, "routing")
                StringBuilder buf = new StringBuilder()
                hash = JDomHelper.hashElement(r, buf)
                logger.info("Hashed " + LogFacilities.mangleString(it.key) + " , which gives ${hash} and the linear representation " + LogFacilities.mangleString(buf as String))
                assertEquals(it.value, hash)
            }
            //
            // Reform the XML, re-read it, re-hash it: values must be identical
            // Here, use null "buf"
            //
            B: {
                String dbString = JDomHelper.stringifyForDatabase(r);
                Element r2 = XMLParsing.extractToplevelElement(dbString, "routing")
                String hash2 = JDomHelper.hashElement(r2, null)
                assertEquals(hash, hash2)
            }
            C: {
                String canonString = JDomHelper.stringifyForCanonicization(r);
                Element r3 = XMLParsing.extractToplevelElement(canonString, "routing")
                String hash3 = JDomHelper.hashElement(r3, null)
                assertEquals(hash, hash3)
            }

        }
    }

}
