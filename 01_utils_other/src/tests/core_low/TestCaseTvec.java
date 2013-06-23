package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jdom.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.enums_sms.NumberingPlanIndicator;
import com.mplify.junit.TestStarter;
import com.mplify.msgserver.addressing.Tvec;
import com.mplify.msgserver.addressing.TvecAddress;
import com.mplify.msgserver.addressing.TvecPair;
import com.mplify.properties.PayloadKey;
import com.mplify.xml.JDomHelper;
/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Stuff moved out of test for DealPersistentStore
 * 
 * 2008.12.08 - Created
 * 2009.01.27 - Added serialization test for new style code
 * 2009.03.23 - Slight review as errors appeared due to code changes
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 ******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseTvec extends TestStarter {

    private final static Tvec triv_tvec = new Tvec("sender.label", "sender.value", "receiver.label", "receiver.value", "replyto.label", "replyto.value");
    private final static String CLASS = TestCaseTvec.class.getName();
        
    @Test
    public void testTransmissionVector() {
        // vanilla test
        {
            Tvec tvec = triv_tvec;
            assertEquals("receiver.value", tvec.getReceiverValue());
            assertEquals("receiver.label", tvec.getReceiverLabel());
            assertEquals("sender.value", tvec.getSenderValue());
            assertEquals("sender.label", tvec.getSenderLabel());
            assertEquals("replyto.value", tvec.getReplyToValue());
            assertEquals("replyto.label", tvec.getReplyToLabel());
        }
        // test with whitespace
        {
            Tvec tvec = new Tvec("  sender.label  ", " sender.value  ", " receiver.label  ", "  receiver.value  ", "  replyto.label  ", "  replyto.value  ");
            assertEquals("receiver.value", tvec.getReceiverValue());
            assertEquals("receiver.label", tvec.getReceiverLabel());
            assertEquals("sender.value", tvec.getSenderValue());
            assertEquals("sender.label", tvec.getSenderLabel());
            assertEquals("replyto.value", tvec.getReplyToValue());
            assertEquals("replyto.label", tvec.getReplyToLabel());
        }
        // test with nulls
        {
            Tvec tvec = new Tvec(null, "sender.value", null, "receiver.value", null, "replyto.value");
            assertEquals("receiver.value", tvec.getReceiverValue());
            assertEquals("", tvec.getReceiverLabel());
            assertEquals("sender.value", tvec.getSenderValue());
            assertEquals("", tvec.getSenderLabel());
            assertEquals("replyto.value", tvec.getReplyToValue());
            assertEquals("", tvec.getReplyToLabel());
        }
    }

    /**
     * TODO: Study more how the TON/NPI is handled.
     */

    @Test
    public void testSerialization() throws Exception {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testSerialization");       
        TvecAddress receiver = new TvecAddress("the_receiver",new TvecPair(new PayloadKey("TON"),"44"),new TvecPair("label","receiver_label"),new TvecPair(new PayloadKey("NPI"),NumberingPlanIndicator.ERMES.toString()));
        TvecAddress sender = new TvecAddress("the_sender",new TvecPair(new PayloadKey("TON"),"42"),new TvecPair("label","sender_label"),new TvecPair(new PayloadKey("NPI"),NumberingPlanIndicator.INTERNET.toString()));
        TvecAddress replyTo = new TvecAddress("the_replyTo",new TvecPair(new PayloadKey("TON"),"43"),new TvecPair("label","reply_to_label"));
        Tvec original = new Tvec(receiver,sender,replyTo);
        Element root = new Element("root");
        Element serializedOriginal = original.injectJdom(root);        
        String out = JDomHelper.stringifyPrettily(serializedOriginal);
        logger.info(out);
        Tvec restored = new Tvec(serializedOriginal);        
        assertEquals(original.getReceiverLabel(), restored.getReceiverLabel());
        assertEquals(original.getReceiverValue(), restored.getReceiverValue());
        assertEquals(original.getReplyToLabel(), restored.getReplyToLabel());
        assertEquals(original.getReplyToValue(), restored.getReplyToValue());
        assertEquals(original.getSenderLabel(), restored.getSenderLabel());
        assertEquals(original.getSenderValue(), restored.getSenderValue());
        assertEquals("44",original.getReceiver().getAttrValue(new PayloadKey("TON")));
        assertEquals("42",original.getSender().getAttrValue(new PayloadKey("TON")));
        assertEquals("43",original.getReplyTo().getAttrValue(new PayloadKey("TON")));
        assertEquals("44",restored.getReceiver().getAttrValue(new PayloadKey("TON")));
        assertEquals("42",restored.getSender().getAttrValue(new PayloadKey("TON")));
        assertEquals("43",restored.getReplyTo().getAttrValue(new PayloadKey("TON")));
        assertEquals(Integer.toString(NumberingPlanIndicator.ERMES.getValue()),restored.getReceiver().getAttrValue(new PayloadKey("NPI")));
        assertEquals(Integer.toString(NumberingPlanIndicator.INTERNET.getValue()),restored.getSender().getAttrValue(new PayloadKey("NPI")));
        assertNull(restored.getReplyTo().getAttrValue(new PayloadKey("NPI")));
        assertEquals(NumberingPlanIndicator.ERMES.toString(),original.getReceiver().getAttrValue(new PayloadKey("NPI")));
        assertEquals(NumberingPlanIndicator.INTERNET.toString(),original.getSender().getAttrValue(new PayloadKey("NPI")));
        assertNull(original.getReplyTo().getAttrValue(new PayloadKey("NPI")));
    }

    @Test
    public void testSerializationOldToNew() throws Exception {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testSerializationOldToNew");
        TvecAddress receiver = new TvecAddress("the_receiver",new TvecPair(new PayloadKey("TON"),"44"),new TvecPair(new PayloadKey("label"),"receiver_label"));
        TvecAddress sender = new TvecAddress("the_sender",new TvecPair(new PayloadKey("TON"),"42"),new TvecPair(new PayloadKey("label"),"sender_label"));
        TvecAddress replyTo = new TvecAddress("the_replyTo",new TvecPair(new PayloadKey("TON"),"43"),new TvecPair(new PayloadKey("label"),"reply_to_label"));
        Tvec original = new Tvec(receiver,sender,replyTo);
        Element root = new Element("root");
        Element serializedOriginal = original.injectJdom(root,true); // old style!!        
        String out = JDomHelper.stringifyPrettily(serializedOriginal);
        logger.info(out);
        Tvec restored = new Tvec(serializedOriginal);        
        assertEquals(original.getReceiverLabel(), restored.getReceiverLabel());
        assertEquals(original.getReceiverValue(), restored.getReceiverValue());
        assertEquals(original.getReplyToLabel(), restored.getReplyToLabel());
        assertEquals(original.getReplyToValue(), restored.getReplyToValue());
        assertEquals(original.getSenderLabel(), restored.getSenderLabel());
        assertEquals(original.getSenderValue(), restored.getSenderValue());
        assertEquals("44",original.getReceiver().getAttrValue(new PayloadKey("TON")));
        assertEquals("42",original.getSender().getAttrValue(new PayloadKey("TON")));
        assertEquals("43",original.getReplyTo().getAttrValue(new PayloadKey("TON")));
        assertNull(restored.getReceiver().getAttrValue(new PayloadKey("TON")));
        assertNull(restored.getSender().getAttrValue(new PayloadKey("TON")));
        assertNull(restored.getReplyTo().getAttrValue(new PayloadKey("TON")));
    }
}
