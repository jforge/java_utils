package com.mplify.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * What is should the JDOM output style be? One of 'compact', 'pretty', 'raw'
 * 
 * THESE FORMATS DO NOT GIVE EQUIVALENT RESULTS!
 * 
 * PRETTY: For any text content, the leading and trailing whitespace is **removed**,
 * though Newlines inside the text are preserved.
 *    
 * COMPACT: For any text content, the leading and trailing whitespace is
 * **removed**, internal newlines are **removed** and sequences of whitespace is
 * reduced to a single whitespace.
 *
 * RAW: For any text content, the leading and trailing whitespace is kept and 
 * internal newlines are kept and sequences of whitespace are kept.
 * 
 * 2006.10.01 - Created
 ******************************************************************************/

public class JdomSerializationFormat {

//	private final static String CLASS = JdomSerializationFormat.class.getName();

	/*
	 * Members. State is identified by 'value', which is also what is used in the 
	 * database. The String stores explanations for this value. As no new instances
	 * of this class are ever created, this is absolutely not inefficient.
	 */

	private final Integer value;
	private final String name;
	
	/*
	 * These are the possible instance of this class; they form a quasi-enumeration
	 * These are public and used by client code. 
	 */

	public final static JdomSerializationFormat COMPACT;  
	public final static JdomSerializationFormat PRETTY; 
	public final static JdomSerializationFormat RAW; 
	
	/*
	 * An list of the values, public but immutable 
	 */

	public final static List<JdomSerializationFormat> LIST;
	
	/*
	 * The values hashed by their Integer id, private
	 */

	private final static Map<Integer, JdomSerializationFormat> MAP_BY_VALUE;
	private final static Map<String, JdomSerializationFormat> MAP_BY_NAME;
	
	/*
	 * Static construction takes care to assign unique (and stable) identifiers
	 * to the various instances. 
	 */

	static {
		{
			List<JdomSerializationFormat> myList = new ArrayList<JdomSerializationFormat>();
			myList.add(COMPACT = new JdomSerializationFormat(1, "compact"));
			myList.add(PRETTY = new JdomSerializationFormat(2, "pretty"));
			myList.add(RAW = new JdomSerializationFormat(3, "raw"));
			LIST = Collections.unmodifiableList(myList);
		}
		{
			Map<Integer, JdomSerializationFormat> myMapByValue = new HashMap<Integer, JdomSerializationFormat>();
			Map<String, JdomSerializationFormat> myMapByName = new HashMap<String, JdomSerializationFormat>();
			for (JdomSerializationFormat es : LIST) {
				Object old1 = myMapByValue.put(es.value, es);
				Object old2 = myMapByName.put(es.toString().toLowerCase(),es); // LOWERCASE
				assert(old1==null);
				assert(old2==null);
			}
			MAP_BY_NAME = Collections.unmodifiableMap(myMapByName);
			MAP_BY_VALUE = Collections.unmodifiableMap(myMapByValue);
		}
	}

	/**
	 * The constructor, can only called by this class. The class defines
	 * the possible cases using the constructor and that's it. Constructor is PRIVATE! 
	 */

	private JdomSerializationFormat(int x, String name) {
		this.value = new Integer(x);
		this.name = name;
	}

	/**
	 * Transform this value into a string
	 */

	@Override
    public String toString() {
		return name;
	}

	/**
	 * Is a value valid? (The 'invalid' value, if any, is counted as valid)
	 */

	public static boolean isValid(int x) {
		return (MAP_BY_VALUE.containsKey(new Integer(x)));
	}

	/**
	 * Comparison compares the underlying values
	 */

	@Override
    public boolean equals(Object obj) {
		if (obj == this) {
			return true; // quick guess
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof JdomSerializationFormat)) {
			return false;
		}
		JdomSerializationFormat other = (JdomSerializationFormat) obj;
		return (other.value == this.value);
	}

	/**
	 * Get the actual underlying value for the database (use sparingly)
	 */

	public int getValue() {
		return value.intValue();
	}

	/**
	 * Get the actual underlying value for the database (use sparingly)
	 */

	public Integer getValueAsInt() {
		return value;
	}

	/**
	 * Return a value instance given an int.
	 * Throws 'IllegalArgumentException' if there is no instance corresponding to 'x'
	 */

	public static JdomSerializationFormat obtain(int x) {
		JdomSerializationFormat res = MAP_BY_VALUE.get(new Integer(x));
		if (res == null) {
			throw new IllegalArgumentException("Nothing corresponding to value '" + x + "' exists");
		} else {
			return res;
		}
	}

	/**
	 * Return a value instance given an Integer.
	 * Throws 'IllegalArgumentException' if there is no instance corresponding to 'x'
	 */

	public static JdomSerializationFormat obtain(Integer x) {
		if (x == null) {
			throw new IllegalArgumentException("(null) Integer passed");
		} else {
			return obtain(x.intValue());
		}
	}

	/**
	 * Return a value instance given a String, which should correspond to the "name"
	 */

	public static JdomSerializationFormat obtain(String name, JdomSerializationFormat defaultValue, boolean throwIfNotFound) {
		if (name == null) {
			throw new IllegalArgumentException("The passed 'name' is (null)");
		} else {
			JdomSerializationFormat so = MAP_BY_NAME.get(name.toLowerCase().trim()); // LOWERCASE
			if (so == null) {
				if (throwIfNotFound) {
					throw new IllegalArgumentException("Nothing corresponding to name '" + name + "' exists");
				} else {
					return defaultValue;
				}
			} else {
				return so;
			}
		}
	}
	
	/**
	 * Wanna hash this...
	 */

	@Override
    public int hashCode() {
		return value.intValue();
	}
	
	/**
	 * Make string where possibly values are comma-separted. Used in 'usage' strings
	 */

	public static String toUsageString() {
		StringBuffer buf = new StringBuffer();
		boolean appendComma = false;
		for (JdomSerializationFormat x : LIST) {
			if (appendComma) {
				buf.append(",");
			}
			buf.append("'");
			buf.append(x); 
			buf.append("'");
			appendComma = true;
		}
		return buf.toString();
	}
	
}
