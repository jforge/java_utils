package com.mplify.properties;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers.Check;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Methods to transform a Property structure to and from a string. Used
 * in serializing/unserializing to the database.
 * 
 * Test this for fastness
 * 
 * This should probably be separated into PropertyMarshaller and PayloadMarshaller,
 * and the latter's methods should probably be methods of Payload.
 * 
 * 2004.10.08 - Reviewed
 * 2009.12.22 - Changes because Payload uses PayloadKey instead of String as keys
 * 2010.10.08 - Code of Payload moved to Payload
 * 2011.03.11 - Removed the dependence on type <T>
 * 2011.05.30 - Introduced _check.cannotHappen() to replace a local throw
 ******************************************************************************/

public class PropertyMarshaller {

	private final static String CLASS = PropertyMarshaller.class.getName();
	private final static Logger LOGGER_marshalProperties = LoggerFactory.getLogger(CLASS + ".marshalProperties");
	private final static Logger LOGGER_unmarshalProperties = LoggerFactory.getLogger(CLASS + ".unmarshalProperties");

	/**
	 * Unreachable constructor
	 */

	private PropertyMarshaller() {
		// Unreachable
	}

	/**
	 * This is called when writing to database. It returns the set of attribute/value
	 * pairs in a www-urlencoded form. 
	 * Package visibility to make it accessible by testcase.
	 * TODO: Move to to an XML representation - maybe (probably not)
	 * Added special case: If 'null' is passed, then the empty string is returned
	 */

	static public String marshalProperties(Properties props) {
		Logger logger = LOGGER_marshalProperties;
		if (props == null) {
			return "";
		}
		// Sort the Properties by inserting into a Tree structure (TODO: Only if more than 10?)
		TreeMap<Object,Object> tree = new TreeMap<Object,Object>(props);
		StringBuffer buf = new StringBuffer();
		Iterator<Entry<Object,Object>> entryIter = tree.entrySet().iterator();
		boolean addSeparator = false;
		while (entryIter.hasNext()) {
			Entry<Object,Object> entry = entryIter.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
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
			if (!(value instanceof String)) {
				// this SHOULD not happen, it's a compromised Property structure
				logger.warn("The value of key " + key + " is of type " + value.getClass().getName() + "; stringifying it");
			}
			String encodedKey = key.toString();
			String encodedValue = value.toString();
			try {
				encodedKey = URLEncoder.encode(encodedKey, "UTF-8");
				encodedValue = URLEncoder.encode(encodedValue, "UTF-8");
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

	/**
	 * This is called when reading from database. It returns the set of attribute/value pairs,
	 * urlencoded in the passed string, as a Properties structure.
	 * Package visibility to make it accessible by testcase
	 * If the empty string (or even null) is passed as 'raw', empty properties are returned.
	 */

	static public Properties unmarshalProperties(String rawIn) {
		Logger logger = LOGGER_unmarshalProperties;
		Properties res = new Properties();
		if (rawIn == null) {
			return res;
		}
		String raw = rawIn.trim();
		if ("".equals(raw)) {
			return res;
		}
		StringTokenizer tz = new StringTokenizer(raw, "&");
		while (tz.hasMoreTokens()) {
			String leftright = tz.nextToken();
			int eqPos = leftright.indexOf("=");
			if (eqPos < 0) {
				logger.error("The token '" + leftright + "' does not contain an equal sign; discarding it");
			} else if (eqPos == 0) {
				logger.error("The token '" + leftright + "' has its equal sign at the beginning; discarding it");
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
					decodedKey = URLDecoder.decode(decodedKey, "UTF-8");
					decodedValue = URLDecoder.decode(decodedValue, "UTF-8");
				} catch (UnsupportedEncodingException exe) {
				    Check.cannotHappen(exe);
				}
				res.setProperty(decodedKey, decodedValue);
			}
		}
		return res;
	}
	
}
