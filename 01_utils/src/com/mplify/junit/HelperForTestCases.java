package com.mplify.junit;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers.Check;
import com.mplify.logging.LogFacilities;
import com.mplify.tools.ByteCoding;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Some helper methods for Junit test cases
 * 
 * 2003.06.13 - Created for Mobilux.
 * 2004.10.14 - Copied from someplace else
 * 2004.11.17 - Review for better testing code
 * 2004.11.30 - Reused for M-PLIFY. most of the methods thrown out though.
 *              Added compareString()
 * 2005.01.12 - compareByteStream() now takes an 'alsoLog' boolean
 * 2005.11.21 - Added getPropertiesResourceName(), _main() no longer takes
 *              a package.
 * 2008.05.05 - Eliminated compile warning
 * 2008.08.21 - Simplified getPropertiesResourceName()
 *              Added getPropertiesResourceNameInPackageImmediatePropertiesLocal(9
 * 2008.12.11 - Caught the special case of "no tests run" when printing
 *              otherwise computing the relative number of failures gives
 *              a division by zero (which apparently works, giving NaN 
 * 2009.02.02 - StringBuffer --> StringBuilder
 * 2009.02.17 - Simplified calls to String and Array comparison functions
 *              by removing the "alsoLog" boolean and accepting a null logger
 *              instead.
 * 2009.08.26 - _main() explicitly calls Log4JStarter, a new class
 *              dealing with Log4J initialization
 * 2010.10.09 - Added options to create various styles of "random properties"
 * 2010.10.09 - Added ParticularTest and PropertiesResourceUsingHook 
 *              annotation.
 * 2010.10.09 - Moved equalsProperties() to a separate class. Also, main()
 *              methods and annotation handling methods moved to their own
 *              class.    
 * 2013.01.02 - Moved equalsProperties() back to here.                  
 ******************************************************************************/

public class HelperForTestCases {

    private final static String CLASS = HelperForTestCases.class.getName();
    private final static Logger LOGGER_equalsProperties = LoggerFactory.getLogger(CLASS + ".equalsProperties");

    private final static Random rand = new Random();

    /**
     * You cannot instantiate this
     */

    private HelperForTestCases() {
        // unreachable
    }

    /**
     * Compare two dates but not at their millisecond level (useful if comparing a date from the database and one not
     * from the database that has been written to it)
     */

    public static boolean equalsDateDisregardingMs(Date left, Date right) {
        long left_ms = left.getTime();
        long right_ms = right.getTime();
        left_ms = (left_ms / 1000) * 1000;
        right_ms = (right_ms / 1000) * 1000;
        return (left_ms == right_ms);
    }

    /**
     * Helper: Create a random string of length 'n' Synchronized because it accesses the static 'rand' This will be an
     * ASCII string with characters between ASCII 32 and 127.
     */

    public synchronized static String createRandomString(int n) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int x = 32 + rand.nextInt(127 - 32);
            buf.append((char) x);
        }
        return buf.toString();
    }

    /**
     * Helper: Create a random string of length 'n' Synchronized because it accesses the static 'rand' This will be an
     * ASCII string with characters A-Z, a-z, 0-9
     */

    public synchronized static String createSimpleRandomString(int n) {
        final String select = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int x = rand.nextInt(select.length());
            buf.append(select.charAt(x));
        }
        return buf.toString();
    }

    /**
     * Helper: Create a random string of length 'n' Synchronized because it accesses the static 'rand' This will be an
     * ASCII string with characters A-Z, a-z
     */

    public synchronized static String createSimpleLetterString(int n) {
        final String select = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < n; i++) {
            int x = rand.nextInt(select.length());
            buf.append(select.charAt(x));
        }
        return buf.toString();
    }

    /**
     * Create a random 'properties' structure with the given number of entries. Synchronized because it accesses the
     * static 'rand'. To make sure there are only "trimmed nonempty keys", set the "trimmedNonemptyKeys" boolean.
     */

    public static enum Allowed {
        PRINTABLES, ALPHANUMERICS, LETTERS
    }

    public synchronized static Properties createRandomProperties(int size, boolean onlyTrimmedNonemptyKeys, Allowed forKey, Allowed forValue) {
        Properties p = new Properties();
        for (int j = 0; j < size; j++) {
            int lengthKey = rand.nextInt(10) + 1; // between 1 and 10
            int lengthValue = rand.nextInt(20); // between 0 and 20
            String key = null;
            {
                boolean keyOk = false;
                while (!keyOk) {
                    if (forKey == null || forKey == Allowed.PRINTABLES) {
                        key = createRandomString(lengthKey);
                    } else if (forKey == Allowed.ALPHANUMERICS) {
                        key = createSimpleRandomString(lengthKey);
                    } else if (forKey == Allowed.LETTERS) {
                        key = createSimpleLetterString(lengthKey);
                    } else {
                        throw new IllegalArgumentException("Unknown case '" + forKey + "'");
                    }
                    if (onlyTrimmedNonemptyKeys) {
                        key = key.trim();
                    }
                    keyOk = !onlyTrimmedNonemptyKeys || !key.isEmpty();
                }
            }
            String value = null;
            {
                if (forValue == null || forValue == Allowed.PRINTABLES) {
                    value = createRandomString(lengthValue);
                } else if (forValue == Allowed.ALPHANUMERICS) {
                    value = createSimpleRandomString(lengthValue);
                } else if (forValue == Allowed.LETTERS) {
                    value = createSimpleLetterString(lengthValue);
                } else {
                    throw new IllegalArgumentException("Unknown case '" + forValue + "'");
                }
            }
            // System.out.println(key);
            // System.out.println(val);
            p.setProperty(key, value);
        }
        return p;
    }

    /**
     * Create a random 'bytestream' with the given number of byte. Synchronized because it accesses the static 'rand'
     */

    public synchronized static byte[] createFullyRandomByteStream(int size) {
        Check.largerOrEqualToZero(size, "size");
        byte[] res = new byte[size];
        rand.nextBytes(res);
        return res;
    }

    /**
     * Compare two arrays, complain if they are not equal, return an 'equal' boolean. The arrays may be null, the
     * comparison says 'ok' if both are null at the same time. Pass a non-null logger for some output.
     */

    public static boolean compareByteArrays(byte[] left, byte[] right, Logger logger) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null) {
            if (logger != null) {
                logger.info("Left array is (null) whereas right array is not");
            }
            return false;
        }
        if (right == null) {
            if (logger != null) {
                logger.info("Right array is (null) whereas left array is not");
            }
            return false;
        }
        if (left.length != right.length) {
            if (logger != null) {
                logger.info("Arrays have differing lengths: left: " + left.length + " right: " + right.length);
            }
            return false;
        }
        {
            boolean equal = true;
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < Math.min(left.length, right.length); i++) {
                if (left[i] != right[i]) {
                    buf.append("[");
                    buf.append(ByteCoding.hexifyByte(left[i]));
                    buf.append(",");
                    buf.append(ByteCoding.hexifyByte(right[i]));
                    buf.append("]");
                    equal = false; // continue printing
                } else {
                    buf.append(ByteCoding.hexifyByte(left[i]));
                }
            }
            if (!equal) {
                logger.info("Unequal byte arrays: " + buf);
            }
            return equal;
        }
    }

    /**
     * Remove any CR or NL or whitespace at the end or front of a line. Good for putting XML into a 'canonical' form
     */

    public static String flattenString(String in) throws IOException {
        StringBuilder buf = new StringBuilder();
        LineNumberReader lr = new LineNumberReader(new StringReader(in));
        String line;
        while ((line = lr.readLine()) != null) {
            buf.append(line.trim());
        }
        return buf.toString();
    }

    /**
     * Compare two strings, complain if they are not equal, return an 'equal' boolean. Pass a non-null logger for some
     * logging
     */

    public static boolean compareString(String left, String right, Logger logger) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null) {
            if (logger != null) {
                logger.error("Left string is (null) whereas right string is not");
            }
            return false;
        }
        if (right == null) {
            if (logger != null) {
                logger.error("Right string is (null) whereas left string is not");
            }
            return false;
        }
        if (left.length() != right.length()) {
            if (logger != null) {
                logger.error("Strings have differing lengths: " + left.length() + " " + right.length() + "\n" + left + "\n" + right);
            }
            return false;
        }
        {
            boolean equal = true;
            StringBuilder buf = new StringBuilder();
            for (int i = 0; i < Math.min(left.length(), right.length()); i++) {
                if (left.charAt(i) != right.charAt(i)) {
                    buf.append("(");
                    buf.append(left.charAt(i));
                    buf.append("|");
                    buf.append(right.charAt(i));
                    buf.append(")");
                    equal = false; // continue printing
                } else {
                    buf.append(left.charAt(i));
                }
            }
            if (!equal) {
                logger.error("Strings are not equal: " + LogFacilities.mangleString(buf.toString()));
            }
            return equal;
        }
    }

    /**
     * Check the equality of two "properties"
     */

    public static boolean equalsProperties(Properties left, Properties right) {
        Logger logger = LOGGER_equalsProperties;
        TreeMap<Object, Object> leftMap = new TreeMap<Object, Object>(left);
        TreeMap<Object, Object> rightMap = new TreeMap<Object, Object>(right);
        Iterator<Entry<Object, Object>> leftIter = leftMap.entrySet().iterator();
        Iterator<Entry<Object, Object>> rightIter = rightMap.entrySet().iterator();
        while (leftIter.hasNext() && rightIter.hasNext()) {
            Map.Entry<Object, Object> leftEntry = leftIter.next();
            Map.Entry<Object, Object> rightEntry = rightIter.next();
            if (!leftEntry.getKey().equals(rightEntry.getKey())) {
                logger.info("There is a difference in the keys: " + leftEntry.getKey() + " <> " + rightEntry.getKey());
                return false;
            }
            if (leftEntry.getValue() == null) {
                if (rightEntry.getValue() != null) {
                    logger.info("There is a difference in the values for key " + leftEntry.getKey() + ": " + leftEntry.getValue() + " <> " + rightEntry.getValue());
                    return false;
                }
            } else {
                if (!leftEntry.getValue().equals(rightEntry.getValue())) {
                    logger.info("There is a difference in the values for key " + leftEntry.getKey() + ": " + leftEntry.getValue() + " <> " + rightEntry.getValue());
                    return false;
                }
            }
        }
        if (leftIter.hasNext() || rightIter.hasNext()) {
            logger.info("There is a difference in the number of entries of the properties: " + leftMap.size() + " <> " + rightMap.size());
        }
        return (!leftIter.hasNext() && !rightIter.hasNext());
    }

}
