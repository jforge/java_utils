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
 * A class that is used to represent Verbosity levels in logging.
 * 
 * 2005.08.01 - Created
 * 2005.01.09 - Added toUsageString(), moved to core classes
 * 2006.01.25 - Slight modification of code text
 * 2006.06.15 - Slight rewrite of static init
 ******************************************************************************/

public class Verbosity implements Comparable<Verbosity>, EnumerativeTypeUsingInteger {

	private final static String CLASS = Verbosity.class.getName();

	/*
	 * Members. They are identified by 'value', which is also what is used in
	 * the database. The String stores explanations for this value. As no new instances of this class
	 * are ever created, this is absolutely not inefficient.
	 */

	private final int value;
	private final String shortText;

	/*
	 * These are the possible instance of this class; they form a quasi-enumeration
	 * These are public and used by client code
	 */

	public final static Verbosity LOW;
	public final static Verbosity MEDIUM;
	public final static Verbosity HIGH;

	/*
	 * An list of the values, public but immutable. Goes from LOW to HIGH
	 */

	public final static List<Verbosity> LIST_LOW_TO_HIGH;
	public final static List<Verbosity> LIST_HIGH_TO_LOW;

	/*
	 * The values hashed by their Integer id, private
	 */

	private final static Map<Integer, Verbosity> MAP;
	private final static Map<String, Verbosity> REVERSE_MAP;

	/*
	 * Static construction takes care to assign unique (and stable) identifiers
	 * to the various instances. They must be stable because they are used in the database. 
	 * At the same time, the values are assigned to the 'list', which will then be
	 * made immutable and assigned to LIST.
	 */

	static {
		{
			List<Verbosity> myList = new ArrayList<Verbosity>();
			myList.add(LOW = new Verbosity(0, "LOW"));
			myList.add(MEDIUM = new Verbosity(1, "MEDIUM"));
			myList.add(HIGH = new Verbosity(2, "HIGH"));
			LIST_LOW_TO_HIGH = Collections.unmodifiableList(myList);
		}
		{
			List<Verbosity> myList = new ArrayList<Verbosity>(LIST_LOW_TO_HIGH);
			Collections.reverse(myList);
			LIST_HIGH_TO_LOW = Collections.unmodifiableList(myList);
		}
		//
		// hash the values into the private hash, too, and put them into the ARRAY
		//
		{
			Map<Integer, Verbosity> myMap = new HashMap<Integer, Verbosity>();
			Map<String, Verbosity> myReverseMap = new HashMap<String, Verbosity>();
			for (Verbosity vl : LIST_LOW_TO_HIGH) {
				Object old1 = myMap.put(new Integer(vl.value), vl);
				assert (old1 == null);
				Object old2 = myReverseMap.put(vl.shortText.toLowerCase(), vl); // note the 'to lower case'
				assert (old2 == null);
			}
			MAP = Collections.unmodifiableMap(myMap);
			REVERSE_MAP = Collections.unmodifiableMap(myReverseMap);
		}
	}

	/**
	 * The constructor, can only called by this class. The class defines
	 * the possible cases using the constructor and that's it. Constructor is PRIVATE! 
	 */

	private Verbosity(int x, String shortText) {
		this.value = x;
		this.shortText = shortText;
	}

	/**
	 * Transform this value into a string
	 */

	@Override
    public String toString() {
		return shortText;
	}

	/**
	 * Is a value valid? (The 'invalid' value, if any, is counted as valid)
	 */

	public static boolean isValid(int x) {
		return (MAP.containsKey(new Integer(x)));
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
		if (!(obj instanceof Verbosity)) {
			return false;
		}
		Verbosity other = (Verbosity) obj;
		return (other.value == this.value);
	}

	/**
	 * Get the actual underlying value (use sparingly)
	 */

	@Override
    public int getValue() {
		return value;
	}

	/**
	 * Return a value instance given an int.
	 * Throws 'IllegalArgumentException' if there is no instance corresponding to 'x'
	 */

	public static Verbosity obtain(int x) {
		Verbosity res = MAP.get(new Integer(x));
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

	public static Verbosity obtain(Integer x) {
		if (x == null) {
			throw new IllegalArgumentException("The passed value is (null)");
		} else {
			return obtain(x.intValue());
		}
	}

	/**
	 * Obtain a VerbosityLevel from the passed String, which is lowercased and trimmed
	 * before being handled and must yield either 'high', 'medium' or 'low'. In
	 * case no match is found, the 'defaultValue' is returned. That value can be (null).
	 * You may also want to throw an IllegalArgumentException, set 'throwIfNoMatch' to
	 * true.
	 */

	public static Verbosity obtain(String x, Verbosity defaultValue, boolean throwIfNoMatch) {
		Verbosity p = null;
		if (x != null) {
			p = REVERSE_MAP.get(x.toLowerCase().trim());
		}
		if (p == null) {
			if (throwIfNoMatch) {
				throw new IllegalArgumentException("The passed String, '" + x + "' could not be transformed into a " + CLASS);
			} else {
				return defaultValue;
			}
		} else {
			return p;
		}
	}

	/**
	 * Wanna hash this...
	 */

	@Override
    public int hashCode() {
		return value;
	}

	/**
	 * Wanna sort: HIGH > MEDIUM > LOW
	 * This is consistent with equals (i.e. x.compareTo(y) == 0  <=> x.equals(y)
	 * Returns z > 0 if "'this' is more detailed/has higher verbosity than 'other'" 
	 */

	@Override
    public int compareTo(Verbosity other) {
		int x = this.value - other.value;
		return x;
	}

	/**
	 * Make string where possibly values are comma-separted. Used in 'usage' strings
	 */

	public static String toUsageString() {
		StringBuffer buf = new StringBuffer();
		boolean appendComma = false;
		for (Verbosity v : LIST_LOW_TO_HIGH) {
			if (appendComma) {
				buf.append(",");
			}
			buf.append("'");
			buf.append(v);
			buf.append("'");
			appendComma = true;
		}
		return buf.toString();
	}
}
