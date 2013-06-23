package com.mplify.msgserver.addressing;

import javax.mail.internet.InternetAddress;

import org.jdom.Element;

import com.mplify.checkers._check;
import com.mplify.logging.LogFacilities;
import com.mplify.logging.Story;
import com.mplify.logging.storyhelpers.Doublet;
import com.mplify.tools.AddressAcceptor;
import com.mplify.xml.ConstructionException;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * a class that encapsulates the triplet:
 *  
 * (receiver value / receiver label) == "receiver address"
 * (sender value / sender label)     == "sender address"
 * (reply-to value / reply-to label) == "reply-to address"
 *
 * 2007.01.17 - Created
 * 2007.11.30 - Added emptyValueYieldsNull to make assumptions explicit
 * 2008.11.05 - Removed stuff for fax handling which didn't seem to belong
 *              here
 * 2008.12.08 - Added equals()
 * 2009.01.07 - Added new constructor: TransmissionVector(Element element)
 *              and injectJdom()
 * 2009.03.18 - "label" keyword cleanup
 * 2009.10.19 - makeNullIfEmpty() copied to here, seems useful
 * 2010.07.27 - Renamed TransmissionVector --> Tvec
 * 2010.12.17 - Using AddressAcceptor for better Internet Email Address
 *              Syntax Checks in handleAddress()
 * 2011.01.03 - Added hashCode(), but for good hashing, the hashCode
 *              of TvecAddress still needs to be implemented. At least the
 *              warning about the missing hashCode() method is gone.
 *              
 * Problems; an "empty" value and an unset value are not distinguishable
 *           at this level. Is that bad?
 *           In a sense, this class should have few assumptions and leave
 *           special case handling and knowledge to the caller, e.g. the
 *           knowledge that the "replyTo" parts are not meaningful in a 
 *           specific setting.
 *           There is also a direct mapping between the fields in here
 *           and the database table...
 ******************************************************************************/

public class Tvec {

    private final static String CLASS = Tvec.class.getName();

//    private final static Logger LOGGER_munge = LoggerFactory.getLogger(CLASS + ".munge");

    private final static String RECEIVER_STR = "Receiver";
    private final static String REPLY_TO_STR = "ReplyTo";
    private final static String SENDER_STR = "Sender";

    private final static String SENDER_LABEL_STR = "senderLabel";
    private final static String SENDER_VALUE_STR = "senderValue";
    private final static String RECEIVER_LABEL_STR = "receiverLabel";
    private final static String RECEIVER_VALUE_STR = "receiverValue";
    private final static String REPLY_TO_LABEL_STR = "replyToLabel";
    private final static String REPLY_TO_VALUE_STR = "replyToValue";

    /**
     * Various types of addresses
     */

    private final TvecAddress receiver;
    private final TvecAddress sender;
    private final TvecAddress replyTo;

    /**
     * Old-style Constructor. You may pass 'null' values
     */

    public Tvec(String senderLabel, String senderValue, String receiverLabel, String receiverValue, String replyToLabel, String replyToValue) {
        // retrofit to assume functionality of old code
        this.receiver = new TvecAddress(receiverValue, new TvecPair(TvecAddress.ATTR_LABEL, unnullifyAndTrim(receiverLabel)));
        this.sender = new TvecAddress(senderValue, new TvecPair(TvecAddress.ATTR_LABEL, unnullifyAndTrim(senderLabel)));
        this.replyTo = new TvecAddress(replyToValue, new TvecPair(TvecAddress.ATTR_LABEL, unnullifyAndTrim(replyToLabel)));
    }

    /**
     * New-style Constructor. You may pass 'null' values on all three fields
     */

    public Tvec(TvecAddress receiver, TvecAddress sender, TvecAddress replyTo) {
        this.receiver = receiver;
        this.sender = sender;
        this.replyTo = replyTo;
    }

    /**
     * Getter. Never returns null, may return the empty string.
     */

    public String getReceiverValue() {
        // retrofit to assume functionality of old code
        if (receiver == null) {
            return "";
        } else {
            return receiver.getValue();
        }
    }

    /**
     * Getter. Never returns null, may return the empty string.
     */

    public String getReceiverLabel() {
        // retrofit to assume functionality of old code
        if (receiver == null) {
            return "";
        } else {
            return receiver.getLabel();
        }
    }

    /**
     * Getter. Never returns null, may return the empty string.
     */

    public String getReplyToValue() {
        // retrofit to assume functionality of old code
        if (replyTo == null) {
            return "";
        } else {
            return replyTo.getValue();
        }
    }

    /**
     * Getter. Never returns null, may return the empty string.
     */

    public String getReplyToLabel() {
        // retrofit to assume functionality of old code
        if (replyTo == null) {
            return "";
        } else {
            return replyTo.getLabel();
        }
    }

    /**
     * Getter. Never returns null, may return the empty string.
     */

    public String getSenderValue() {
        // retrofit to assume functionality of old code
        if (sender == null) {
            return "";
        } else {
            return sender.getValue();
        }
    }

    /**
     * Getter. Never returns null, may return the empty string.
     */

    public String getSenderLabel() {
        // retrofit to assume functionality of old code
        if (sender == null) {
            return "";
        } else {
            return sender.getLabel();
        }
    }

    /**
     * Stringification. The string is multi-line!
     */

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.receiver != null) {
            buf.append("[TO:" + LogFacilities.mangleString(this.receiver.getLabel()) + "/" + LogFacilities.mangleString(this.receiver.getValue()) + "]");
        }
        if (this.replyTo != null) {
            buf.append("[REPLY:" + LogFacilities.mangleString(this.replyTo.getLabel()) + "/" + LogFacilities.mangleString(this.replyTo.getValue()) + "]");
        }
        if (this.sender != null) {
            buf.append("[FROM:" + LogFacilities.mangleString(this.sender.getLabel()) + "/" + LogFacilities.mangleString(this.sender.getValue()) + "]");
        }
        return buf.toString();
    }

    /**
     * Storyfication
     */

    public Story toStory() {
        Story res = new Story();
        if (this.receiver != null && !this.receiver.isEmpty()) {
            res.add(new Doublet("Receiver",this.receiver));
        }
        if (this.replyTo != null && !this.replyTo.isEmpty()) {
            res.add(new Doublet("ReplyTo",this.replyTo));
        }
        if (this.sender != null && !this.sender.isEmpty()) {
            res.add(new Doublet("Sender",this.sender));
        }
        return res;
    }

    /**
     * Extract the sender as an Internet Mail Address. Returns null if the sender value is unset (empty)
     * Throws IllegalArgumentException if the underlying data is bad and the address x@y cannot be build
     */

    public InternetAddress getSenderEmailAddress() {
        boolean emptyValueYieldsNull;
        return handleAddress(getSenderValue(), getSenderLabel(), emptyValueYieldsNull=true);
    }

    /**
     * Extract the receiver as an Internet Mail Address. Returns null if the sender value is unset (empty)
     * Throws IllegalArgumentException if the underlying data is bad and the address x@y cannot be build
     */

    public InternetAddress getReceiverEmailAddress() {
        boolean emptyValueYieldsNull;
        return handleAddress(getReceiverValue(), getReceiverLabel(), emptyValueYieldsNull=true);
    }

    /**
     * Extract the reply-to as an Internet Mail Address. Returns null if the sender value is unset (empty)
     * Throws IllegalArgumentException if the underlying data is bad and the address x@y cannot be build
     */

    public InternetAddress getReplyToEmailAddress() {
        boolean emptyValueYieldsNull;
        return handleAddress(getReplyToValue(), getReplyToLabel(), emptyValueYieldsNull=true);
    }

    /**
     * Helper for storification; creates a Doublet with a large multiline value
     */

    public static Doublet transmissionVectorToDoublet(String text, Tvec tvec) {
        String storyString = "--"; // default
        if (tvec != null) {
            storyString = tvec.toStory().toString();
        }
        return new Doublet(text, storyString);
    }

    /**
     * Construct an InternetAddress given the "address" (which we call 'value') and "personal" (which we call 'label').
     * If the address cannot be properly created, an IllegalArgumentException is thrown. 
     * 
     * Passing 'null' as "value" will return null (instead of throwing!). 
     * Additionally, you can request that the empty value also yield null instead of an exception. 
     * 
     * The acceptable values for the value must all be of the form "a@b" or "a@b.", i.e. an unqualified address won't do!!
     * 
     * See also the test case.
     */

    public static InternetAddress handleAddress(String valueIn, String label, boolean emptyValueYieldsNull) {
        if (valueIn == null) {
            return null; // null is allowed!
        }        
        String value = valueIn.trim();
        if (value.isEmpty() && emptyValueYieldsNull) {
            return null;
        }
        // Test the syntax of "value" according to our own recognizer
        if (!AddressAcceptor.acceptAddress(value)) {
            throw new IllegalArgumentException("Inacceptable value '" + LogFacilities.mangleString(value) + "' for an InternetAddress");
        }    
        // Create and validate, throw IllegalArgumentException on problem.
        try {
            InternetAddress iaddr;            
            if (label == null) {
                boolean strict;
                iaddr = new InternetAddress(value, strict = true);
            } else {
                // N.B.: Trimming of label is NOT automatic!!
                iaddr = new InternetAddress(value, label.trim());
            }            
            // Additionally validate it unless you want to have suprises just before mail sending.
            // In principle, our recognizer should have caught problems, but one never knows...
            // If this doesn't throw, assume good
            iaddr.validate(); 
            return iaddr;
        } catch (Exception exe) {
            throw new IllegalArgumentException("InternetAddress cannot be created from value='" + LogFacilities.mangleString(value) + "' and label='" + LogFacilities.mangleString(label) + "'",exe);
        }
    }

    /**
     * Implementation of equals calling the equal of substructures (the TvecAddress structures)
     */

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Tvec)) {
            return false;
        }
        Tvec other = (Tvec) obj;
        if (this.receiver == null ^ other.receiver == null) {
            return false;
        }
        if (this.sender == null ^ other.sender == null) {
            return false;
        }
        if (this.replyTo == null ^ other.replyTo == null) {
            return false;
        }
        if (this.receiver != null) {
            assert other.receiver != null;
            if (!this.receiver.equals(other.receiver)) {
                return false;
            }
        }
        if (this.sender != null) {
            assert other.sender != null;
            if (!this.sender.equals(other.sender)) {
                return false;
            }
        }
        if (this.replyTo != null) {
            assert other.replyTo != null;
            if (!this.replyTo.equals(other.replyTo)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Construction from a JDOM Element. Very useful even though this means TransmissionVector has to link to JDOM
     * (should we subclass maybe?)
     */

    public Tvec(Element element) throws ConstructionException {
        _check.notNull(element,"element");
        try {
            boolean newStyle = (element.getChild(SENDER_STR) != null || element.getChild(RECEIVER_STR) != null || element.getChild(REPLY_TO_STR) != null);
            boolean oldStyle = (element.getChild(SENDER_LABEL_STR) != null || element.getChild(SENDER_VALUE_STR) != null || element.getChild(RECEIVER_LABEL_STR) != null
                    || element.getChild(RECEIVER_VALUE_STR) != null || element.getChild(REPLY_TO_LABEL_STR) != null || element.getChild(REPLY_TO_VALUE_STR) != null);
            if (newStyle && !oldStyle) {
                Element senderElement = element.getChild(SENDER_STR);
                Element receiverElement = element.getChild(RECEIVER_STR);
                Element replyToElement = element.getChild(REPLY_TO_STR);
                if (senderElement != null) {
                    this.sender = new TvecAddress(senderElement);
                } else {
                    this.sender = null;
                }
                if (receiverElement != null) {
                    this.receiver = new TvecAddress(receiverElement);
                } else {
                    this.receiver = null;
                }
                if (replyToElement != null) {
                    this.replyTo = new TvecAddress(replyToElement);
                } else {
                    this.replyTo = null;
                }
            } else if (oldStyle && !newStyle) {
                // if the values don't exist, we fix them up
                // note that fixup will trim() the strings
                String senderLabel = unnullifyAndTrim(element.getChildText(SENDER_LABEL_STR));
                String senderValue = unnullifyAndTrim(element.getChildText(SENDER_VALUE_STR));
                String receiverLabel = unnullifyAndTrim(element.getChildText(RECEIVER_LABEL_STR));
                String receiverValue = unnullifyAndTrim(element.getChildText(RECEIVER_VALUE_STR));
                String replyToLabel = unnullifyAndTrim(element.getChildText(REPLY_TO_LABEL_STR));
                String replyToValue = unnullifyAndTrim(element.getChildText(REPLY_TO_VALUE_STR));
                this.receiver = new TvecAddress(receiverValue, new TvecPair(TvecAddress.ATTR_LABEL, receiverLabel));
                this.sender = new TvecAddress(senderValue, new TvecPair(TvecAddress.ATTR_LABEL, senderLabel));
                this.replyTo = new TvecAddress(replyToValue, new TvecPair(TvecAddress.ATTR_LABEL, replyToLabel));
            } else if (!oldStyle && !newStyle) {
                // assume empty...
                this.receiver = null;
                this.sender = null;
                this.replyTo = null;
            } else {
                throw new IllegalArgumentException("Detected elements of both new style and old style -- can't decide");
            }
        } catch (Exception exe) {
            throw new ConstructionException("While constructing " + CLASS, exe);
        }
    }

    /**
     * Insert JDOM elements below the passed element, so that if the constructor is passed that element, the element can
     * be reconstructed. Returns the passed element for "chaining calls"
     */

    public Element injectJdom(Element element) {
        return injectJdom(element, false);
    }

    /**
     * Insert JDOM elements below the passed element, so that if the constructor is passed that element, the element can
     * be reconstructed. Returns the passed element for "chaining calls". Pass a boolean to indicate whether "old style"
     * format should be forced
     */

    public Element injectJdom(Element element, boolean oldStyle) {
        _check.notNull(element,"element");
        if (oldStyle) {
            // obsolete, old-school coding
            // surrounding empty space is irrelevant: should not exist, will be trimmed anyway
            if (!"".equals(getSenderLabel())) {
                element.addContent(new Element(SENDER_LABEL_STR).setText(getSenderLabel()));
            }
            if (!"".equals(getSenderValue())) {
                element.addContent(new Element(SENDER_VALUE_STR).setText(getSenderValue()));
            }
            if (!"".equals(getReceiverLabel())) {
                element.addContent(new Element(RECEIVER_LABEL_STR).setText(getReceiverLabel()));
            }
            if (!"".equals(getReceiverValue())) {
                element.addContent(new Element(RECEIVER_VALUE_STR).setText(getReceiverValue()));
            }
            if (!"".equals(getReplyToLabel())) {
                element.addContent(new Element(REPLY_TO_LABEL_STR).setText(getReplyToLabel()));
            }
            if (!"".equals(getReplyToValue())) {
                element.addContent(new Element(REPLY_TO_VALUE_STR).setText(getReplyToValue()));
            }
        } else {
            // new coding, with separate subelements and "payloads"
            if (sender != null) {
                boolean useText;
                Element sub = new Element(SENDER_STR);
                sender.injectJdom(sub,useText=false);
                element.addContent(sub);
            }
            if (receiver != null) {
                boolean useText;
                Element sub = new Element(RECEIVER_STR);
                receiver.injectJdom(sub,useText=false);
                element.addContent(sub);
            }
            if (replyTo != null) {
                boolean useText;
                Element sub = new Element(REPLY_TO_STR);
                replyTo.injectJdom(sub,useText=false);
                element.addContent(sub);
            }
        }
        return element;
    }

    /**
     * Get the "receiver" substructure. May return null
     */

    public TvecAddress getReceiver() {
        return receiver;
    }

    /**
     * Get the "sender" substructure. May return null
     */

    public TvecAddress getSender() {
        return sender;
    }

    /**
     * Get the "reply-to" substructure. May return null
     */

    public TvecAddress getReplyTo() {
        return replyTo;
    }
    
    /**
     * hashCode; not really needed
     */
    
    @Override
    public int hashCode() {
        int res = 0x672edf10;
        if (receiver!=null) {
            res = res ^ receiver.hashCode();
        }
        if (sender!=null) {
            res = res ^ sender.hashCode();
        }
        if (replyTo!=null) {
            res = res ^ replyTo.hashCode();
        }
        return res;
    }
    
    /**
     * Fixing strings
     */

    private static String unnullifyAndTrim(String x) {
        if (x == null) {
            return "";
        } else {
            return x.trim();
        }
    }
}
