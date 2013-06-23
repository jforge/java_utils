package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import org.jdom.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.junit.HelperForTestCases;
import com.mplify.junit.TestStarter;
import com.mplify.junit.HelperForTestCases.Allowed;
import com.mplify.payload.Payload;
import com.mplify.payload.PayloadKey;
/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * TestCase for Marshal/Unmarshal procedures of Payload
 *
 * 2004.10.08 - Reviewed
 * 2010.10.09 - Separation between testing of marshal/unmarshal of Properties
 *              and marshal/unmarshal of Payload
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 ******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseMarshalUnmarshalPayload extends TestStarter {

    private final static String CLASS = TestCaseMarshalUnmarshalPayload.class.getName();

    @Test
    public void testMarshalUnmarshalPayloadThatIsEmpty() {
//        Logger logger = LoggerFactory.getLogger(CLASS + ".testMarshalUnmarshalPayloadThatIsEmpty");
        Payload<PayloadKey> payload = new Payload<PayloadKey>();
        String serialized = payload.marshal("UTF-8");
        assertEquals("", serialized); // the result is the empty string!
        Payload<PayloadKey> payloadBack = new Payload<PayloadKey>(serialized, Payload.factory,"UTF-8");
        assertNotNull(payloadBack);
        assertEquals(0, payloadBack.size());
    }

    @Test
    public void testMarshalUnmarshalPayloadWithAnEmptyValue() {
//        Logger logger = LoggerFactory.getLogger(CLASS + ".testMarshalUnmarshalPayloadWithAnEmptyValue");
        Payload<PayloadKey> payload = new Payload<PayloadKey>();
        payload.set(new PayloadKey("key"), "");
        String serialized = payload.marshal("UTF-8");
        Payload<PayloadKey> payloadBack = new Payload<PayloadKey>(serialized, Payload.factory,"UTF-8");
        assertNotNull(payloadBack);
        assertEquals(1, payloadBack.size());
        assertEquals("", payloadBack.get(new PayloadKey("key")));
    }

    @Test
    public void testMarshalUnmarshalPayloadWithValuesThatHaveLotsOfWhitespace() {
//        Logger logger = LoggerFactory.getLogger(CLASS + ".testMarshalUnmarshalPayloadWithValuesThatHaveLotsOfWhitespace");
        String value = "         ... .        ...   .........    .  . . .. . . . . ";
        Payload<PayloadKey> payload = new Payload<PayloadKey>();
        payload.set(new PayloadKey("key"), value);
        assertEquals(value, payload.get(new PayloadKey("key")));
        String serialized = payload.marshal("UTF-8");
        Payload<PayloadKey> payloadBack = new Payload<PayloadKey>(serialized, Payload.factory,"UTF-8");
        assertNotNull(payloadBack);
        assertEquals(1, payloadBack.size());
        assertEquals(value, payloadBack.get(new PayloadKey("key")));
    }

    @Test
    public void testMarshalUnmarshalRandomPayloadsToString() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testMarshalUnmarshalRandomPayloadsToString");
        for (int i = 0; i < 300; i++) {
            boolean onlyTrimmedNonemptyKeys;
            Properties p = HelperForTestCases.createRandomProperties(20, onlyTrimmedNonemptyKeys = true, Allowed.PRINTABLES, Allowed.PRINTABLES);
            Payload<PayloadKey> payload = new Payload<PayloadKey>(p, Payload.factory);
            String serialized = payload.marshal("UTF-8");
            Payload<PayloadKey> payloadBack = new Payload<PayloadKey>(serialized, Payload.factory,"UTF-8");
            assertTrue(payload.deepEquals(payloadBack));
            assertTrue(payloadBack.deepEquals(payload));
            // additionally, one can compare the serializations
            String serializedBack = payloadBack.marshal("UTF-8");
            HelperForTestCases.compareString(serialized, serializedBack, logger);
        }
    }

    @Test
    public void testMarshalUnmarshalRandomPayloadsToJdom() {
//        Logger logger = LoggerFactory.getLogger(CLASS + ".testMarshalUnmarshalRandomPayloadsToJdom");
        for (int i = 0; i < 300; i++) {
            boolean onlyTrimmedNonemptyKeys;
            Properties p = HelperForTestCases.createRandomProperties(20, onlyTrimmedNonemptyKeys = true, Allowed.LETTERS, Allowed.PRINTABLES);
            Payload<PayloadKey> payload = new Payload<PayloadKey>(p, Payload.factory);
            Element element = new Element("TOPLEVEL");
            payload.injectJdom(element);
            Payload<PayloadKey> payloadBack = new Payload<PayloadKey>(element, Payload.factory);
            assertTrue(payload.deepEquals(payloadBack));
            assertTrue(payloadBack.deepEquals(payload));
        }
    }
}
