package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.junit.TestStarter;
import com.mplify.msgserver.enums.Rfc3066LangTag;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Simple test for the "Rfc3066LangTag" type
 * 
 * 2007.02.02 - Written
 * 2007.03.15 - The sequence of values to obtain() has been unified with 
 *              other obtain statements.
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 ******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseRfc3066LangTag extends TestStarter {

    private final static String CLASS = TestCaseRfc3066LangTag.class.getName();

    /**
     * Just check whether RFC3066LangTag -> String -> RFC3066LangTag mapping works.
     */

    @Test
    public void testAutoMatch() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testAutoMatch");
        for (Rfc3066LangTag lt : Rfc3066LangTag.SET_OF_ALL_PATHS) {
            String tag = lt.toString();
            Rfc3066LangTag back1 = Rfc3066LangTag.obtain(tag, null, true);
            Rfc3066LangTag back2 = Rfc3066LangTag.obtain(tag.toUpperCase(), null, true);
            Rfc3066LangTag back3 = Rfc3066LangTag.obtain(tag.toLowerCase(), null, true);
            assertEquals(back1, lt);
            assertEquals(back2, lt);
            assertEquals(back3, lt);
            logger.info("Tag '" + tag + "' can correctly be used to find itself");
        }
    }

    /**
     * Helper used in collecting all the RFC3066LangTag from the sets of children
     */

    private static void recursivelyAddTree(Set<Rfc3066LangTag> rebuildSet, Set<Rfc3066LangTag> treeLevel, Rfc3066LangTag parent) {
        for (Rfc3066LangTag lt : treeLevel) {
            boolean added = rebuildSet.add(lt);
            assertTrue(added);
            if (parent == null) {
                assertNull(lt.getParent());
            } else {
                assertEquals(lt.getParent(), parent);
            }
            // recursively add children
            Set<Rfc3066LangTag> children = new HashSet<Rfc3066LangTag>();
            children.addAll(lt.getChildren().values());
            recursivelyAddTree(rebuildSet, children, lt);
        }
    }

    /**
     * Test that collecting all tags from the TOPMOST ones down their tree reaches them all
     */
    
    @Test
    public void testTree() {
//        Logger logger = LoggerFactory.getLogger(CLASS + ".testTree");
        Set<Rfc3066LangTag> rebuildSet = new HashSet<Rfc3066LangTag>();
        recursivelyAddTree(rebuildSet, Rfc3066LangTag.SET_OF_ROOT_TAGS, null);
        assertEquals(rebuildSet.size(), Rfc3066LangTag.LIST_OF_ALL_PATHS.size());
    }

    /**
     * Match strings to get tags; comparison is done on strings 
     */
    
    @Test
    public void testObtainingLangTagsByMatchingString() {
        assertNull("test 1", Rfc3066LangTag.obtain("xx", null, false));
        // match on 'de' or 'DE' (case insensitivity) and the result, stringified, yield 'de'
        assertEquals("test 2", Rfc3066LangTag.obtain("de", null, false), Rfc3066LangTag.obtain("DE", null, false));
        assertEquals("test 3", "de", Rfc3066LangTag.obtain("de", null, false).toString());
        assertEquals("test 4", "de", Rfc3066LangTag.obtain("DE", null, false).toString());
        // match on 'de-DE' or 'DE-DE' (case insensitivity) and the result, stringified, yield 'de-DE'
        assertEquals("test 5", Rfc3066LangTag.obtain("de-DE", null, false), Rfc3066LangTag.obtain("DE-DE", null, false));
        assertEquals("test 6", "de-DE", Rfc3066LangTag.obtain("de-DE", null, false).toString());
        assertEquals("test 7", "de-DE", Rfc3066LangTag.obtain("DE-DE", null, false).toString());
        // match on 'DE-DE-HELGA' and the result, stringified, yields "de-DE-Helga"
        assertEquals("test 8", "de-DE-helga", Rfc3066LangTag.obtain("DE-DE-HELGA", null, false).toString());
    }

    /**
     * Match strings to get tags; comparison is done on strings 
     */
    
    @Test
    public void testObtainingLangTagsByMatchingBestOnTags() {
        assertNull("No match on xx-yy", Rfc3066LangTag.obtainBest("xx", "yy"));
        assertEquals(Rfc3066LangTag.obtainBest("de"), Rfc3066LangTag.obtain("de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("de", "xx"), Rfc3066LangTag.obtain("de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("de", "de", "xx"), Rfc3066LangTag.obtain("de-de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("de", "de", "voxpilot", "xx"), Rfc3066LangTag.obtain("de-de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("de", "de", "voxpilot", "marlene"), Rfc3066LangTag.obtain("de-de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("de", "de", "helga"), Rfc3066LangTag.obtain("de-de-helga", null, false));
    }

    /**
     * Match strings to get tags; comparison is done on strings 
     */
    
    @Test
    public void testObtainingLangTagsByMatchingBestOnString() {
        // Check that de-de actually resolves correctly
        assertEquals(Rfc3066LangTag.obtainBest("de-de").toString(), "de-DE");
        // more tests
        assertNull("No match on xx", Rfc3066LangTag.obtainBest("xx"));
        assertEquals(Rfc3066LangTag.obtainBest("de"), Rfc3066LangTag.obtain("de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("de-xx"), Rfc3066LangTag.obtain("de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("de-de"), Rfc3066LangTag.obtain("de-de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("de-DE"), Rfc3066LangTag.obtain("de-de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("DE-DE"), Rfc3066LangTag.obtain("de-de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("DE-de"), Rfc3066LangTag.obtain("de-de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("de-de-xx"), Rfc3066LangTag.obtain("de-de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("de-de-voxpilot-xx"), Rfc3066LangTag.obtain("de-de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("de-de-voxpilot-marlene"), Rfc3066LangTag.obtain("de-de", null, false));
        assertEquals(Rfc3066LangTag.obtainBest("de-de-helga"), Rfc3066LangTag.obtain("de-de-helga", null, false));
    }
}
