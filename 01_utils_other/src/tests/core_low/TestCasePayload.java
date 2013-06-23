package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.junit.TestStarter;
import com.mplify.properties.Payload;
import com.mplify.properties.PayloadKey;

//@SuppressWarnings("static-method")
public class TestCasePayload extends TestStarter {

    private final static String CLASS = TestCasePayload.class.getName();

    @Test
    public void testPayloadKeyCleanliness() {
        boolean keyIsClean;
        Payload<PayloadKey> payload = new Payload<PayloadKey>();
        payload.set(new PayloadKey("some key"), "whatever");
        assertEquals("whatever", payload.get(new PayloadKey("some key")));
        assertEquals("whatever", payload.get(new PayloadKey("   some key")));
        assertEquals("whatever", payload.get(new PayloadKey("some key   ")));
        assertEquals("whatever", payload.get(new PayloadKey("   some key   ")));
    }

    @Test
    public void testNullNotAllowedByPayloadConstructor() {
        Payload<PayloadKey> payload = new Payload<PayloadKey>();
        try {
            payload.set((PayloadKey) null, "whatever");
            fail("There should have been an exception");
        } catch (Exception exe) {
            // ok: exception expected
        }
    }

    @Test
    public void testEmptyStringNotAllowedByPayloadConstructor() {
        Payload<PayloadKey> payload = new Payload<PayloadKey>();
        try {
            payload.set(new PayloadKey("  "), "whatever");
            fail("There should have been an exception");
        } catch (Exception exe) {
            // ok: exception expected
        }
    }

    @Test
    public void testIsEmpty() {
//        Logger logger = LoggerFactory.getLogger(CLASS + ".testIsEmpty");
        Payload<PayloadKey> payload = new Payload<PayloadKey>();
        assertTrue(payload.isEmpty());
        assertFalse(payload.isSet(new PayloadKey("whatever")));
        assertNull(payload.get(new PayloadKey("whatever")));
    }

    @Test
    public void testIsSet() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testIsSet");
        Payload<PayloadKey> payload = new Payload<PayloadKey>();
        assertFalse(payload.isSet(new PayloadKey("key")));
        payload.set(new PayloadKey("key"), "1");
        assertEquals("1", payload.get(new PayloadKey("key")));
        assertTrue(payload.isSet(new PayloadKey("key")));
        logger.info("Ok");
    }

    @Test
    public void testIteration() {
//        Logger logger = LoggerFactory.getLogger(CLASS + ".testIteration");
        Payload<PayloadKey> payload = new Payload<PayloadKey>();
        payload.set(new PayloadKey("charlie"), "X1");
        payload.set(new PayloadKey("delta"), "X2");
        payload.set(new PayloadKey("echo"), "X3");
        payload.set(new PayloadKey("alpha"), "X4");
        payload.set(new PayloadKey("foxtrott"), "X5");
        payload.set(new PayloadKey("bravo"), "X6");
        String result = "";
        for (PayloadKey k : payload) {
            result = result + k;
        }
        assertEquals("alphabravocharliedeltaechofoxtrott", result);
    }
}
