package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.junit.HelperForTestCases;
import com.mplify.junit.TestStarter;
import com.mplify.junit.HelperForTestCases.Allowed;
import com.mplify.payload.PropertyMarshaller;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * TestCase for Marshal/Unmarshal procedures for Properties.
 *
 * 2004.10.08 - Reviewed
 * 2010.10.09 - Separation between testing of marshal/unmarshal of Properties
 *              and marshal/unmarshal of Payload
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 ******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseMarshalUnmarshalProperties extends TestStarter {

    private final static String CLASS = TestCaseMarshalUnmarshalPayload.class.getName();

    @Test
    public void testMarshalUnmarshalPropertiesThatAreEmpty() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testMarshalUnmarshalPropertiesThatAreEmpty");
        String m = PropertyMarshaller.marshalProperties(new Properties());
        assertEquals("",m); // the result is the empty string!
        Properties p = PropertyMarshaller.unmarshalProperties(m);
        assertNotNull(p);
        assertTrue(p.isEmpty());
        logger.info("Ok");
    }

    @Test
    public void testMarshalUnmarshalPropertiesWithAnEmptyValue() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testMarshalUnmarshalPropertiesWithAnEmptyValue");
        Properties pin = new Properties();
        pin.setProperty("key", "");
        String m = PropertyMarshaller.marshalProperties(pin);
        Properties pout = PropertyMarshaller.unmarshalProperties(m);
        assertNotNull(pout);
        assertEquals(1, pout.size());
        assertEquals("", pout.getProperty("key"));
        logger.info("Ok");
    }

    @Test
    public void testMarshalUnmarshalPropertiesWithKeyThatHasTrimmableSpace() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testMarshalUnmarshalPropertiesWithKeyThatHasTrimmableSpace");
        Properties pin = new Properties();
        // these things are not the same!
        pin.setProperty(" key", "1");
        pin.setProperty("key ", "2");
        pin.setProperty(" key ", "3");
        pin.setProperty("key", "4");
        String m = PropertyMarshaller.marshalProperties(pin);
        Properties pout = PropertyMarshaller.unmarshalProperties(m);
        assertNotNull(pout);
        assertEquals(4, pout.size());
        assertEquals("1", pout.getProperty(" key"));
        assertEquals("2", pout.getProperty("key "));
        assertEquals("3", pout.getProperty(" key "));
        assertEquals("4", pout.getProperty("key"));
        logger.info("Ok");
    }

    @Test
    public void testMarshalUnmarshalPropertiesWithValuesThatHaveLotsOfWhitespace() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testMarshalUnmarshalPropertiesWithValuesThatHaveLotsOfWhitespace");
        String value = "         ... .        ...   .........    .  . . .. . . . . ";
        Properties pin = new Properties();
        pin.setProperty("key", value);
        String m = PropertyMarshaller.marshalProperties(pin);
        Properties pout = PropertyMarshaller.unmarshalProperties(m);
        assertNotNull(pout);
        assertEquals(1, pout.size());
        assertTrue(value.equals(pout.getProperty("key")));
        logger.info("Ok");
    }

    @Test
    public void testMarshalUnmarshalRandomProperties() {
        boolean trimmedNonemptyKeys,alphanumericsOnly;
        Logger logger = LoggerFactory.getLogger(CLASS + ".testMarshalUnmarshalRandomProperties");
        for (int i = 0; i < 300; i++) {
            boolean onlyTrimmedNonemptyKeys;
            Properties p = HelperForTestCases.createRandomProperties(20, onlyTrimmedNonemptyKeys = false, Allowed.PRINTABLES, Allowed.PRINTABLES);
            String m = PropertyMarshaller.marshalProperties(p);
            Properties pout = PropertyMarshaller.unmarshalProperties(m);
            assertTrue(HelperForTestCases.equalsProperties(p, pout));
            logger.debug("Passed successfully with '" + m + "'");
        }
        logger.info("Ok");
    }

}
