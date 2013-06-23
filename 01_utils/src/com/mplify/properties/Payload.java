package com.mplify.properties;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers.Check;
import com.mplify.logging.Story;
import com.mplify.logging.storyhelpers.Doublet;
import com.mplify.names.AbstractName;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A better representation of Properties than 'Properties'.
 * It thought about making this an interface only but in that case methods
 * like 'equals' become exceedingly difficult/inefficient as they must
 * handle all of the Payload types or only pass through the standard set/get
 * methods.  
 * 
 * The keys in the Payload are "T" instances. The values are arbitrary
 * strings. The value "null" is not allowed. Setting the value of a key to "null"
 * means that the entry is dropped. 
 * 
 * 2006.04.21 - Created
 * 2006.06.22 - Added getIterator()
 * 2009.01.28 - cleanKey() is public now
 * 2009.01.30 - Added purgeKey
 * 2009.02.03 - Set can now handle "null", which means "purge". Purge is deprecated
 * 2009.12.22 - Keys are now "T" instead of "String". This makes
 *              functions simpler and assumptions clearer.
 * 2010.07.16 - Cleanup. equals() renamed to deepEquals() because the semantics
 *              is a bit different than simple equals() and we have no hashCode()
 *              function
 * 2010.09.30 - Cleanup; allowed for the possibility of a (null) mapping.
 *              Added the possibility to construct from JDOM (code from TVecAddress)
 *              Added the possibility to dump to JDOM (code from TVecAddress)
 * 2010.10.08 - Now depends on type "T extends AbstractName" instead of on
 *              PayloadKey.
 * 2010.10.08 - Code cleanup...         
 * 2010.10.11 - Added PayloadKeyFactory instance
 * 2011.01.03 - Introduced Check
 * 2011.02.01 - Added a (nonefficient) hashCode to get rid of warning
 * 2011.02.02 - Missing test in payload construction by cloning added
 * 2011.05.30 - Introduced Check.cannotHappen() to replace a local throw
 * 
 * BUG: The dirty flag seems to be unused
 ******************************************************************************/

public class Payload<T extends AbstractName> implements Iterable<T> {

    private final static String CLASS = Payload.class.getName();

    private final static Logger LOGGER_init = LoggerFactory.getLogger(CLASS + ".<init>");
    private final static Logger LOGGER_deepEquals = LoggerFactory.getLogger(CLASS + ".deepEquals");
    private final static Logger LOGGER_marshal = LoggerFactory.getLogger(CLASS + ".marshal");

    /**
     * A trivial factory for PayloadKey
     */

    public static class PayloadKeyFactory implements Payload.Factory<PayloadKey> {

        @Override
        public PayloadKey make(String x) {
            return new PayloadKey(x);
        }

    }

    /**
     * A static instance of the PayloadKeyFactory factory, used for efficiency
     */

    public static final PayloadKeyFactory factory = new PayloadKeyFactory();

    /**
     * Helper: a Factory of "X" from a String. Used to define factories of "T"
     */

    public static interface Factory<X> {
        public X make(String x);
    }

    /**
     * Members
     */

    protected TreeMap<T, String> mapping; // set to non-null if needed
    protected boolean dirty = false;

    /**
     * Default constructor. Sets up an empty Payload. The "dirty" flag is false at the start.
     */

    public Payload() {
        // Nop
    }

    /**
     * Construction from a previous "marshalling". If null, is passed, the empty payload is constructed
     */

    public Payload(String rawIn, Factory<T> factory, String encoding) {
        Check.notNull(factory, "factory");
        Check.notNull(encoding, "encoding");
        String raw = rawIn;
        if (raw != null) {
            raw = raw.trim();
            if (!raw.isEmpty()) {
                this.mapping = new TreeMap<T, String>();
                StringTokenizer tz = new StringTokenizer(raw, "&");
                while (tz.hasMoreTokens()) {
                    String leftright = tz.nextToken();
                    int eqPos = leftright.indexOf("=");
                    if (eqPos < 0) {
                        LOGGER_init.error("The token '" + leftright + "' does not contain an equal sign; discarding it");
                    } else if (eqPos == 0) {
                        LOGGER_init.error("The token '" + leftright + "' has its equal sign at the beginning; discarding it (original was: '" + rawIn + "')");
                    } else {
                        String decodedKey;
                        String decodedValue;
                        if (eqPos == leftright.length() - 1) {
                            decodedKey = leftright.substring(0, eqPos);
                            decodedValue = "";
                        } else {
                            decodedKey = leftright.substring(0, eqPos);
                            decodedValue = leftright.substring(eqPos + 1, leftright.length());
                        }
                        try {
                            decodedKey = URLDecoder.decode(decodedKey, encoding);
                            decodedValue = URLDecoder.decode(decodedValue, encoding);
                        } catch (UnsupportedEncodingException exe) {
                            Check.cannotHappen(exe);
                        }
                        // 'decodedKey' must not be the empty string
                        // if the 'raw' String contains several entries with the same 'decodedKey', the first
                        // one will now be overwritten
                        String oldValue = set(factory.make(decodedKey), decodedValue);
                        if (oldValue != null) {
                            LOGGER_init.warn("Overwrote an existing value of key '" + decodedKey + "'; the passed 'raw' string must have several instances of key '" + decodedKey + "'");
                        }
                    }
                }
            }
        }
    }

    /**
     * Constructor that takes on the same contents as the passed Payload (which may be null, creating the empty
     * payload). The "dirty" flag is "false" at the start.
     */

    public Payload(Payload<? extends T> cloneMe) {
        if (cloneMe == null || cloneMe.mapping == null) {
            // NOP
        } else {
            // Fastest way to create copy of a is cloning, which is a 'shallow copy' operation
            // that copies the contents of the map fully
            this.mapping = (TreeMap<T, String>) cloneMe.mapping.clone();
        }
    }

    /**
     * Constructor from Properties (which may be null, creating the empty payload). The "dirty" flag is false at the
     * start.
     */

    public Payload(Properties cloneMe, Factory<T> factory) {
        if (cloneMe != null) {
            for (Map.Entry<Object, Object> entry : cloneMe.entrySet()) {
                String keyX = (String) (entry.getKey());
                String valX = (String) (entry.getValue());
                try {
                    if (this.mapping == null) {
                        this.mapping = new TreeMap<T, String>();
                    }
                    // this may fail in various ways
                    set(factory.make(keyX), valX);
                } catch (Exception exe) {
                    LOGGER_init.warn("The passed properties has a problem with key '" + keyX + "': " + exe.getMessage());
                }
            }
        }
    }

    /**
     * Set. This is a convenience function taking an int, which is stringified
     */

    public String set(T key, int value) {
        return set(key, Integer.toString(value));
    }

    /**
     * Set. This is a convenience function taking a long, which is stringified
     */

    public String set(T key, long value) {
        return set(key, Long.toString(value));
    }

    /**
     * Set. Setting to "null" means purging the entry. An existing value is returned.
     */

    public String set(T key, String value) {
        Check.notNull(key, "key");
        if (value == null) {
            // drop the entry
            if (this.mapping == null) {
                return null;
            } else {
                String old = this.mapping.remove(key);
                if (old != null) {
                    dirty = true;
                }
                return old;
            }
        } else {
            if (this.mapping == null) {
                this.mapping = new TreeMap<T, String>();
            }
            String oldvalue = mapping.put(key, value);
            if (oldvalue != null && oldvalue.equals(value)) {
                dirty = true;
            }
            return oldvalue;
        }
    }

    /**
     * Get the value stored under "key". If "null" is returned, the entry did not exist.
     */

    public String get(T key) {
        Check.notNull(key, "key");
        if (mapping == null) {
            return null;
        } else {
            return mapping.get(key);
        }
    }

    /**
     * Check whether the value stored under "key" exists.
     */

    public boolean isSet(T key) {
        Check.notNull(key, "key");
        return (mapping != null && mapping.containsKey(key));
    }

    /**
     * Integrate-in a 'default' payload. All the values in the default payload that do not yet exist in 'this' payload
     * are added to 'this' payload. You may pass (null) - this just means NOP! This is not threadsafe! This causes the
     * 'dirty' flag to be set if any value of 'defaults' is taken up.
     */

    public void integrateDefaults(Payload<T> defaults) {
        if (defaults != null && defaults.mapping != null) {
            for (Map.Entry<T, String> entry : defaults.mapping.entrySet()) {
                if (this.mapping == null) {
                    this.mapping = new TreeMap<T, String>();
                }
                if (!mapping.containsKey(entry.getKey())) {
                    mapping.put(entry.getKey(), entry.getValue());
                    dirty = true;
                }
            }
        }
    }

    /**
     * Get a sorted representation of the underlying data. The 'SortedMap' is mutable and can be used at will by the
     * caller.
     */

    public SortedMap<T, String> getSorted() {
        if (mapping == null) {
            return new TreeMap<T, String>();
        } else {
            return new TreeMap<T, String>(mapping);
        }
    }

    /**
     * Get the 'size' of the payload, i.e. the number of keys
     */

    public int size() {
        if (mapping == null) {
            return 0;
        } else {
            return mapping.size();
        }
    }

    /**
     * Empty payload?
     */

    public boolean isEmpty() {
        return (mapping == null) || (mapping.size() == 0);
    }

    /**
     * Do not use
     */

    @Override
    @Deprecated
    public boolean equals(Object obj) {
        return false;
    }

    /**
     * Full inner comparison of payload. This is not threadsafe! This is actually used, but note that there is no "hash"
     */

    public boolean deepEquals(Object obj) {
        Logger logger = LOGGER_deepEquals;
        if (this == obj) {
            return true; // fast guess
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Payload)) {
            return false;
        }
        Payload<?> other = (Payload<?>) obj;
        //
        // deep-compare the payload contents
        //
        TreeMap<T, String> leftMap = this.mapping;
        TreeMap<T, String> rightMap = (TreeMap<T, String>) other.mapping;
        if (this.size() != other.size()) {
            return false;
        }
        if (isEmpty()) {
            assert other.isEmpty();
            return true;
        }
        //
        // same size on both sides and not empty on both sides --> the maps exist on both sides
        //
        Iterator<Map.Entry<T, String>> leftIter = leftMap.entrySet().iterator();
        Iterator<Map.Entry<T, String>> rightIter = rightMap.entrySet().iterator();
        while (leftIter.hasNext() && rightIter.hasNext()) {
            Map.Entry<T, String> leftEntry = leftIter.next();
            Map.Entry<T, String> rightEntry = rightIter.next();
            if (!leftEntry.getKey().equals(rightEntry.getKey())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("There is a difference in the keys: '" + leftEntry.getKey() + "' <> '" + rightEntry.getKey() + "'");
                }
                return false;
            }
            if (leftEntry.getValue() == null) {
                if (rightEntry.getValue() != null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("There is a difference in the values for key '" + leftEntry.getKey() + "': '" + leftEntry.getValue() + "' <> '" + rightEntry.getValue() + "'");
                    }
                    return false;
                }
            } else {
                if (!leftEntry.getValue().equals(rightEntry.getValue())) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("There is a difference in the values for key '" + leftEntry.getKey() + "': '" + leftEntry.getValue() + "' <> '" + rightEntry.getValue() + "'");
                    }
                    return false;
                }
            }
        }
        if (leftIter.hasNext() || rightIter.hasNext()) {
            if (logger.isDebugEnabled()) {
                logger.debug("There is a difference in the number of entries of the properties: " + leftMap.size() + " <> " + rightMap.size());
            }
        }
        return (!leftIter.hasNext() && !rightIter.hasNext());
    }

    /**
     * Transforming into a String - a one-liner. This is not threadsafe
     */

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        boolean addComma = false;
        if (mapping != null) {
            for (Map.Entry<T, String> entry : mapping.entrySet()) {
                if (addComma) {
                    buf.append(",");
                }
                buf.append(entry.getKey());
                buf.append("=");
                buf.append(entry.getValue());
                addComma = true;
            }
        }
        return buf.toString();
    }

    /**
     * Storyfication
     */

    public Story toStory() {
        Story res = new Story();
        if (mapping != null) {
            for (Map.Entry<T, String> entry : mapping.entrySet()) {
                res.add(new Doublet(entry.getKey().toString(), entry.getValue(), Doublet.MANGLE_STRING));
            }
        }
        return res;
    }

    /**
     * Get an iterator over the keys.
     */

    private static class MyIterator<T> implements Iterator<T> {

        private ArrayList<T> keys;
        private int index = 0;

        /**
         * Create 'keys' as an ordered array of keys found in the payload's mapping keySet()
         */

        public MyIterator(Payload<? extends T> payload) {
            assert (payload != null);
            this.keys = new ArrayList<T>(payload.size()); // can't create T[] though
            if (payload.mapping != null) {
                for (T key : payload.mapping.keySet()) {
                    keys.add(key);
                }
                // some ugly type forcing here...
                Collections.sort(keys, new Comparator<Object>() {
                    @Override
                    public int compare(Object o1, Object o2) {
                        AbstractName x1 = (AbstractName) o1;
                        AbstractName x2 = (AbstractName) o2;
                        return x1.compareTo(x2);
                    }

                });
            }
        }

        /**
         * Not at the end?
         */

        @Override
        public boolean hasNext() {
            return (index < keys.size());
        }

        /**
         * Get the next
         */

        @Override
        public T next() {
            return keys.get(index++);
        }

        /**
         * Remove stuff: Can't be done!
         */

        @Override
        public void remove() {
            throw new UnsupportedOperationException("You can't remove() on this iterator");
        }

    }

    /**
     * Get an iterator over a snapshot of the keys at call time! This is an iterator that does not support the remove()
     * operation The iterator returns the keys in trivially sorted order.
     */

    @Override
    public Iterator<T> iterator() {
        return new MyIterator<T>(this);
    }

    /**
     * This is called when writing to database. It returns the set of attribute/value pairs in a www-urlencoded form. An
     * empty payload yields "". Should the result be cached? Caching should probably be left to the caller.
     */

    public String marshal(String encoding) {
        Check.notNull(encoding, "encoding");
        Logger logger = LOGGER_marshal;
        if (mapping == null) {
            return "";
        } else {
            StringBuilder buf = new StringBuilder();
            boolean addSeparator = false;
            for (Entry<T, String> entry : mapping.entrySet()) {
                T key = entry.getKey();
                String value = entry.getValue();
                if (key == null) {
                    // this can't happen because you can't set a null key!
                    logger.error("Found a (null) key! Discarding it");
                    continue;
                }
                if (value == null) {
                    // this can't happen because you can't set a null value!
                    logger.error("The value of key " + key + " is (null)! Discarding it");
                    continue;
                }
                String encodedKey = key.toString();
                String encodedValue = value;
                try {
                    encodedKey = URLEncoder.encode(encodedKey, encoding);
                    encodedValue = URLEncoder.encode(encodedValue, encoding);
                } catch (UnsupportedEncodingException exe) {
                    Check.cannotHappen(exe);
                }
                if (addSeparator) {
                    buf.append("&");
                }
                buf.append(encodedKey);
                buf.append("=");
                buf.append(encodedValue);
                addSeparator = true;
            }
            return buf.toString();
        }
    }

    /**
     * Hash this
     */

    @Override
    public int hashCode() {
        int res = 0;
        if (mapping != null) {
            for (T x : mapping.keySet()) {
                res = res ^ x.hashCode() ^ (mapping.get(x).hashCode() * 13);
            }
        }
        return res;
    }
}
