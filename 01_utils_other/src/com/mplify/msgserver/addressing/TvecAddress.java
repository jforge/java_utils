package com.mplify.msgserver.addressing;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdom.Element;

import com.mplify.checkers._check;
import com.mplify.enums_sms.NumberingPlanIndicator;
import com.mplify.enums_sms.TypeOfNumber;
import com.mplify.logging.LogFacilities;
import com.mplify.logging.Story;
import com.mplify.logging.storyhelpers.Doublet;
import com.mplify.properties.Payload;
import com.mplify.properties.PayloadKey;
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
 * A class which represents an address: its "value" and "associated attributes",
 * in particular the "label". Additional serialization and processing functions
 * are provided.
 * 
 * 2009.01.27 - Created because we need to have TypeOfNumber and 
 *              NumberingPlanIdentifier in address. Formerly there was 
 *              was only "value" and "label", now there is "value" and the
 *              set of "meta" pairs, among which the "label"
 * 2009.01.30 - Added equals()
 * 2009.02.03 - Added TON/NPI handlers
 * 2009.03.18 - "label" keyword cleanup
 * 2009.03.26 - "Metadata" renamed to "attributes". Serialization to XML
 *              now builds an XML structure for the "attributes" instead of
 *              just dumping stuff as a single string in the form or "text".
 *              Removed bugs in TON/NPI assignment
 * 2009.12.22 - Removed the deprecated "getSerializedMetadata()" function.
 *              Added the isEmpty() test.
 * 2010.09.07 - equals() transformed into a true "deep equals" which does
 *              correct full comparison over payload.
 * 2010.10.08 - Adapted to new "Payload<T>" (formerly only "Payload")
 * 2011.02.01 - Added hashCode(), verified semantics and serializers.
 * 2011.06.07 - Slight changes and code cleanup, add makeValueSet()
 * 2011.06.28 - Added ATTR_PREFIX_PLUS to handle bug#3282
 * 2011.11.29 - Added getAttributes()
 * 2012.07.03 - Added new constructor "public TvecAddress(String value, TvecAddress other)"
 * 
 * The possible value space:
 * 
 * 'value':         not null; may be empty; is trimmed
 * any attribute:   may be set or unset; if set, may be any String
 *                  ...except for the "label", which is trimmed and for which
 *                  "empty String" and "unset" are the same thing.
 *                  
 * Bugs: This class does not have a very "symmetric" feel. Too much historical
 * baggage? The problems with the "label" definitely suck.
 ******************************************************************************/

public class TvecAddress {

    private final static String CLASS = TvecAddress.class.getName();
    private final static Logger LOGGER_init = LoggerFactory.getLogger(CLASS + ".<init>");
    private final static Logger LOGGER_makeValueSet = LoggerFactory.getLogger(CLASS + ".makeValueSet");
    
    private final static String XML_VALUE = "value";
    private final static String XML_ATTRIBUTES = "attributes";

    public final static PayloadKey ATTR_VALUE  = new PayloadKey("value");
    public final static PayloadKey ATTR_LABEL = new PayloadKey("label");
    public final static PayloadKey ATTR_TON = new PayloadKey("ton");
    public final static PayloadKey ATTR_NPI = new PayloadKey("npi");
    public final static PayloadKey ATTR_PREFIX_PLUS = new PayloadKey("pp");
    
    private final static String NUMBER_REGEX = "^[0-9]+$";
    private final static Pattern NUMBER_PATTERN = Pattern.compile(NUMBER_REGEX);

    /**
     * The fields
     */

    private final String value; // *not null*, trimmed, possibly empty (thus "empty" and "unset" are not distinguishable as such)
    private Payload<PayloadKey> attributes; // possibly null map of additional key-value pairs (surrounding code knows how to deal with this); kept null for efficiency
    
    /**
     * Constructor sets up an instance with "value" only. 'value' be (null) when passed but will then be promoted to the
     * empty string (that is then an unset address). If it is not (null), the string is trimmed in any case.
     */

    public TvecAddress(String value) {
        this.value = unnullifyAndTrim(value);
        this.attributes = null;
    }

    /**
     * Constructor sets up an instance with "value" and a set of TvecPair. If an attribute is specified
     * several times in the pairs, the last one is retained. The lettercase of the key of any TvecPair is unimportant. 
     * The value of any TvecPair cannot be null. It can be empty. The value is stored "as is", except
     * in the case of the TvecPair with key "label". The trimmed value of "label" cannot be empty
     * (for historical reasons, the empty label is equivalent to no label), so in that case, no label
     * is actually stored.
     */

    public TvecAddress(String value, TvecPair... pairs) {
        this(value);
        assert pairs!=null; 
        if (pairs.length>0) {
            this.attributes = new Payload<PayloadKey>();
            for (TvecPair p : pairs) {
                String old;
                if (ATTR_LABEL.equals(p.getKey())) {
                    // special handling for the "label": a label that if trimmed is empty *cannot* be stored!
                    old = setLabel(p.getValue());
                }
                else {
                    // any other attribute can accept the empty string as value
                    old = this.attributes.set(p.getKey(), p.getValue());
                    if (old != null) {
                        LOGGER_init.warn("Duplicate key entry '" + p.getKey() + "' with previous value '" + old + "' and new '" + p.getValue() + "' -- replacing previous value");
                    }
                }
            }
        }        
    }
    
    /**
     * Construct a new TvecAddress with the given value and the attributes of "other"
     * "other" may be null.
     */
    
    public TvecAddress(String value, TvecAddress other) {
        this(value);
        if (other != null && other.attributes!=null && other.attributes.size()>0) {
            this.attributes = new Payload<PayloadKey>();
            for (PayloadKey pk : other.attributes) {
                if (ATTR_LABEL.equals(pk)) {
                    setLabel(other.attributes.get(pk));
                }
                else {
                    this.attributes.set(pk, other.attributes.get(pk));
                }
            }
        }
    }
    
    /**
     * Construct from a non-null "payload" which should also contain the "value". This is used when reconstrucing
     * from a "serialized-out" representation.
     * The passed payload comes under full control of TvecAddress and should not be used by the caller after this!
     * The passed payload should contain an entry having key "value" (which is trimmed), otherwise
     * the TvecAddress' "value" will be set to the empty String.
     */

    public TvecAddress(Payload<PayloadKey> payload) {
        _check.notNull(payload,"payload");        
        // handle value specially, make sure it's not null and trimmed
        this.value = unnullifyAndTrim(payload.get(ATTR_VALUE));    
        // erase the "value" from the payload because we store it outsde
        payload.set(ATTR_VALUE,null);         
        // fixup the "label", which may or may not exist
        this.attributes = payload;
        {
            String label = payload.get(ATTR_LABEL);        
            setLabel(label);
        }
    }
        
    /**
     * Helper
     */
    
    private static Integer makeInteger(String x) {
        try {
            return Integer.valueOf(x);
        }
        catch (Exception exe) {
            // not an integer
            return null;
        }
    }
    
    /**
     * Construct from a non-null JDOM Element.
     */

    public TvecAddress(Element element) throws ConstructionException {
        _check.notNull(element,"element");
        this.value = unnullifyAndTrim(element.getChildText(XML_VALUE)); // may be unset; we can live with that
        try {
            Element attrElement = element.getChild(XML_ATTRIBUTES); // attributes are in a separate element 
            if (attrElement != null) {
                this.attributes = new Payload<PayloadKey>();
                List<?> children = attrElement.getChildren();
                for (Object child : children) {
                    if (child instanceof Element) {
                        Element childElement = (Element) child;
                        String key = childElement.getName(); // not null, will be trimmed and lowercased in set()
                        String value = childElement.getText(); // not null, but may be empty
                        String old = null;
                        //
                        // The XML accepts special representations for TON and NPI
                        //
                        if (ATTR_TON.toString().equalsIgnoreCase(key)) {
                            Integer x = makeInteger(value);
                            if (x==null) {
                                // Special case! Transform non-numeric string identifying TON to its numeric id
                                TypeOfNumber ton = TypeOfNumber.obtain(value, null, true);
                                x = Integer.valueOf(ton.getValue());
                            }
                            assert x!=null;
                            old = this.attributes.set(ATTR_TON, x.intValue());
                        } else if (ATTR_NPI.toString().equalsIgnoreCase(key)) {
                            Integer x = makeInteger(value);
                            if (x==null) {
                                // Special case! Transform non-numeric string identifying NPI to its numeric id
                                NumberingPlanIndicator npi = NumberingPlanIndicator.obtain(value, null, true);
                                x = Integer.valueOf(npi.getValue());
                            }
                            assert x!=null;
                            old = this.attributes.set(ATTR_NPI, x.intValue());    
                        } else if (ATTR_LABEL.toString().equalsIgnoreCase(key)) {
                            setLabel(value); // special label semantics
                        } else {
                            // store key --> value mapping, with the value "as is"
                            old = this.attributes.set(new PayloadKey(key), value);
                        }
                        if (old != null) {
                            LOGGER_init.warn("Duplicate key entry '" + key + "' with previous value '" + old + "' and new '" + value + "' -- replaced previous value");
                        }
                    }
                }
            } else {
                this.attributes = null;
            }
        } catch (Exception exe) {
            throw new ConstructionException("While unpacking XML", exe);
        }
    }

    /**
     * Get everything as an www-urlencoded "Payload<PayloadKey>". The "value" will be missing if it's empty.
     * The "encoding" determines how attribute-keys and attribute-values are urlencoded, one should pass UTF-8 here.
     *  
     * See http://download.oracle.com/javase/1.4.2/docs/api/java/net/URLEncoder.html
     * See http://www.ietf.org/rfc/rfc1738.txt, section 2.2
     * 
     * An example of an UTF-8 URLencoded TvecAddress: "label=Imagerie+m%C3%A9dicale&value=352571177077"
     * Excellent for a table supporting ISO-8859-1 only!
     * 
     * URL encoding is pretty messy though. Safe US-ASCII characters are mapped directly, others are remapped
     * through the (UTF-8) encoding and the resulting bytes escaped using %XY sequences. Is the reverse 
     * transformation guaranteed?  
     * 
     * The reverse transformation is given by just creating a Payload through
     * 
     * Payload<PayloadKey> x = new Payload<PayloadKey>(string,Payload.factory,"UTF-8");
     * 
     * then using the constructor of this class that takes a "Payload"
     */
    
    public String getAsEncodedPayload(String encoding) {
        _check.notNull(encoding,"encoding");        
        Payload<PayloadKey> payloadOut;
        if (attributes!=null) {
            payloadOut = new Payload<PayloadKey>(attributes); // clone the attributes
        }
        else {
            payloadOut = new Payload<PayloadKey>(); // a new payload
        }
        assert value!=null;
        if (!value.isEmpty()) {
            payloadOut.set(ATTR_VALUE, value);
        }
        return payloadOut.marshal(encoding);
    }

    /**
     * Getter of "the value". Returns non-null, trimmed string, possibly empty. Case has been preserved.
     */

    public String getValue() {
        return value;
    }

    /**
     * Get any attribute value given the non-null "key". Returns null if there is no such entry. No special provision
     * for "label" is made.
     */

    public String getAttrValue(PayloadKey key) {
        _check.notNull(key, "key");
        if (attributes == null) {
            // no attributes at all, so not found
            return null;
        } else {
            // will return null if not found
            return attributes.get(key);
        }
    }
    
    /**
     * Syntactic sugar to get an Attribute Value based on a String
     */

    public String getAttrValue(String key) {
        PayloadKey pkey = new PayloadKey(key); // this may be throw...
        return getAttrValue(pkey);
    }
    
    /**
     * Set the attribute value corresponding to the non-null "key". Returns the old value or null. If you set to "null",
     * you drop the entry corresponding to "key". No special provision for "label" is made.
     */

    public String setAttrValue(PayloadKey key, String value) {
        _check.notNull(key,"key");
        if (attributes == null) {
            if (value == null) {
                // erase? not needed
            } else {
                // insert into fresh payload
                this.attributes = new Payload<PayloadKey>();
                this.attributes.set(key, value);
            }
            return null;
        } else {
            // irrespective of value:
            return this.attributes.set(key, value);
        }
    }

    /**
     * Legacy: Set the label. The label is trimmed. The empty label has the same effect as the "null" label: 
     * If null is passed, the entry corresponding to the label is dropped.
     */

    public String setLabel(String label) {
        String tlabel = label;
        if (tlabel!=null) {
            // always trim
            tlabel = tlabel.trim();
            // possibly MAKE it null
            if (tlabel.isEmpty()) {
                tlabel = null;
            }
        }
        return setAttrValue(ATTR_LABEL, tlabel); // this may erase the "label" entry by setting to null
    }

    /**
     * Legacy: The "label" was formerly in a separate field, thus a special function. Returns non-null, non-trimmed
     * string, possibly empty. As null is never returned, an "unset label" is represented by the empty string.
     */

    public String getLabel() {
        if (attributes == null) {
            return "";
        } else {
            String l = attributes.get(ATTR_LABEL);
            if (l == null) {
                return "";
            } else {
                return l;
            }
        }
    }

    /**
     * Are there any attributes? The "empty label" is not considered an attribute.
     */

    public boolean hasNoAttributes() {
        return (attributes == null || attributes.isEmpty());
    }

    /**
     * Direct access to the attributes. May return null or the empty Payload
     */
    
    public Payload<PayloadKey> getAttributes() {
        return attributes;
    }
    
    /**
     * Is the whole structure "empty", i.e. does it have no attributes and is the value also empty?
     */

    public boolean isEmpty() {
        assert value!=null;
        return value.isEmpty() && hasNoAttributes();
    }

    /**
     * Is the address unset, i.e. is the value the empty string? Attributes may still exist!
     */
    
    public boolean isUnset() {
        assert value!=null;
        return value.isEmpty();
    }
    
    
    /**
     * Storyfication. Prints out the value even if it is empty.
     */

    public Story toStory() {
        Story res = new Story();
        res.add(new Doublet("Value", "'" + value + "'"));
        if (!hasNoAttributes()) {
            res.add(attributes.toStory());
        }
        return res;
    }

    /**
     * Stringification
     */

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        boolean addComma = false;
        buf.append("[");
        if (!value.isEmpty()) {
            buf.append(value);
            addComma = true;
        }
        if (!hasNoAttributes()) {
            for (Map.Entry<PayloadKey, String> entry : attributes.getSorted().entrySet()) {
                if (addComma) {
                    buf.append(", ");
                }
                buf.append(entry.getKey());
                buf.append("=");
                buf.append("'");
                buf.append(LogFacilities.mangleString(entry.getValue()));
                buf.append("'");
                addComma = true;
            }
        }
        buf.append("]");
        return buf.toString();
    }

    /**
     * Insert JDOM elements below the passed element, so that if the constructor is passed that element, the element can
     * be reconstructed. Returns the passed element for "chaining calls". If "useText" is set, text identifiers instead
     * of numeric values are used.
     */

    public Element injectJdom(Element element, boolean useText) {
        _check.notNull(element,"element");
        element.addContent(new Element(XML_VALUE).setText(getValue()));
        if (!hasNoAttributes()) {
            Element attrElement = new Element(XML_ATTRIBUTES); // attributes are in a separate element
            for (PayloadKey key : attributes) {
                if (useText && ATTR_TON.equals(key)) {
                    TypeOfNumber ton = getTypeOfNumber();
                    attrElement.addContent(new Element(key.toString()).setText(ton.toString()));
                } else if (useText && ATTR_NPI.equals(key)) {
                    NumberingPlanIndicator npi = getNumberingPlanIndicator();
                    attrElement.addContent(new Element(key.toString()).setText(npi.toString()));
                } else {
                    attrElement.addContent(new Element(key.toString()).setText(attributes.get(key)));
                }
            }
            element.addContent(attrElement);
        }
        return element;
    }

    /**
     * Equals compares value **and** attributes (it is a "deepEquals").
     * The attributes must all have correspondences. 
     */

    @Override
    public boolean equals(Object x) {
        if (x == null) {
            return false;
        }
        if (x == this) {
           return true; // quick guess
        }
        if (!(x instanceof TvecAddress)) {
            return false;
        }
        TvecAddress other = (TvecAddress) x;
        if (!this.value.equals(other.value)) {
            return false;
        }
        boolean thisEmpty = this.hasNoAttributes();
        boolean otherEmpty = other.hasNoAttributes();
        if (thisEmpty ^ otherEmpty) {
            return false;
        }
        if (!thisEmpty) {
            assert !otherEmpty : "none empty";
            // must call "deepEquals" for contents comparison
            // note that that is not threadsafe
            return this.attributes.deepEquals(other.attributes);
        } else {
            // both empty
            return true;
        }
    }
    
    /**
     * Hash this; it's not really used though
     */
    
    @Override
    public int hashCode() {
        int res = value.hashCode();
        if (attributes!=null) {
            res = res ^ attributes.hashCode();
        }
        return res;
    }

    /**
     * Helper to get Type-Of-Number from attributes. Return a TypeOfNumber instance if correctly set; null if unset,
     * throws if bad value which cannot be transformed into a TypeOfNumber has been found.
     */

    public TypeOfNumber getTypeOfNumber() {
        String res = getAttrValue(ATTR_TON);
        if (res == null) {
            return null;
        } else {
            return TypeOfNumber.obtain(Integer.parseInt(res));
        }
    }

    /**
     * Helper to get Numbering-Plan-Identifier from attributes. Return a Numbering-Plan-Identifier instance if correctly
     * set; null if unset, throws if bad value which cannot be transformed into a Numbering-Plan-Identifier has been
     * found.
     */

    public NumberingPlanIndicator getNumberingPlanIndicator() {
        String res = getAttrValue(ATTR_NPI);
        if (res == null) {
            return null;
        } else {
            return NumberingPlanIndicator.obtain(Integer.parseInt(res));
        }
    }

    /**
     * Set Type-Of-Number in TvecAddress attributes. If "null" is passed as TON, an existing TON is dropped.
     */

    public void setTypeOfNumber(TypeOfNumber ton) {
        if (ton == null) {
            setAttrValue(ATTR_TON, null);
        } else {
            setAttrValue(ATTR_TON, Integer.toString(ton.getValue()));
        }
    }

    /**
     * Set Numbering-Plan-Identifier in TvecAddress metadata. If null is passed as NPI, an existing NPI is dropped.
     */

    public void setNumberingPlanIndicator(NumberingPlanIndicator npi) {
        if (npi == null) {
            setAttrValue(ATTR_NPI, null);
        } else {
            setAttrValue(ATTR_NPI, Integer.toString(npi.getValue()));
        }
    }


    /**
     * Fixing strings, make a string non-null
     */

    private static String unnullifyAndTrim(String x) {
        if (x == null) {
            return "";
        } else {
            return x.trim();
        }
    }    

    /**
     * Helper to map a Collection of TvecAddress to a Set of "values". "data" is not supposed to be (null).
     * And "null" entries in the "data" collection are just disregarded. Never returns null.
     */
    
    public static Set<String> makeValueSet(Collection<TvecAddress> data) {
        Logger logger = LOGGER_makeValueSet;
        _check.notNull(data,"data");
        Set<String> res = new HashSet<String>();
        for (TvecAddress x : data) {
            if (x!=null) {
                res.add(x.getValue());
            }
            else {
                logger.warn("(null) entry in passed collection skipped");
            }
        }
        return res;
    }
}
