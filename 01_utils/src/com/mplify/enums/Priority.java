package com.mplify.enums;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers.Check;
import com.mplify.properties.PropertyName;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A class that is used to represent queueing priorities. Priorities are 
 * represented by instances of this class. A factory call yields an instance
 * given a priority value. All very straightforward.
 * Design notes: Do it this way is actually a Good Thing! Constants are not
 * bunched up in some mongo common 'structure of all constants'
 * 
 * 2001.10.15 - Implemented as 'DealerPriorities'
 * 2002.04.19 - Added function to verify validity of a priority
 * 2003.08.26 - No longer exports just integers but class instances to be even
 *              more sure that no-one uses a priority value that he should not.
 * 2003.10.15 - Review,moved to m3p.values
 * 2004.01.24 - Rewritten using best idiom, also implements Comparable
 * 2004.11.11 - Added obtain(String x,Priority defaultValue,boolean throwIfNoMatch)
 * 2004.11.21 - Recopied from Mobilux project.
 * 2005.05.10 - Modifications added as the same class is reused for ILR project
 *              Now implements Serializable
 * 2005.07.14 - Added LIST_LOW_TO_HIGH and LIST_HIGH_TO_LOW
 * 2009.03.30 - Removed the serializationId.
 * 2010.07.27 - Re-add a serializationId as we are "Serializable".
 * 2011.02.20 - Implemented Comparable simplified from 
 *              "Comparable" to "Comparable<Priority>" 
 ******************************************************************************/

public class Priority implements Comparable<Priority>, Serializable, EnumerativeTypeUsingInteger {

	private static final long serialVersionUID = 2701681722464403405L;

	private final static String CLASS = Priority.class.getName();
	private final static Logger LOGGER_obtain = LoggerFactory.getLogger(CLASS + ".obtain");

	/*
	 * Members. The Priority is identified by 'value', which is also what is used in the database
	 * The String stores explanations for this value. As no new instances of this class are ever
	 * created, this is absolutely not inefficient.
	 */

	private final int value;
	private final String name;

	/*
	 * These are the possible instance of this class; they form a quasi-enumeration
	 * These are public and used by client code.
	 * Note that there is no 'Invalid' Priority. 
	 * N.B.: PrioritizedQueue relies on the fact that the actual values of Priority
	 * start at 0 and move upwards to LIST.size()-1.
	 */

	public static final Priority LOW;
	public static final Priority MEDIUM;
	public static final Priority HIGH;

	/*
	 * An list of the values, public but immutable. Priorities are ordered from low to high 
	 */

	public final static List<Priority> LIST_LOW_TO_HIGH;
	public final static List<Priority> LIST_HIGH_TO_LOW;

	/*
	 * The values hashed by their Integer id and their lowercased String, private
	 */

	private final static Map<Integer, Priority> MAP_BY_ID;
	private final static Map<String, Priority> MAP_BY_NAME;

	/*
	 * Static construction takes care to assign unique (and constant) numeric identifiers
	 * to the various instances. They must be stable because they are used in the database. 
	 * At the same time, the values are assigned to the 'list', which will then be
	 * made immutable and assigned to LIST.
	 * Priorities must be continuously-numbered, as elsewhere they are used as indexes
	 * into arrays. Thus, '0' is NOT the 'unset' value (such a value does not exist) but 
	 * indeed priority zero.  Higher priorities have higher values -- very intuitive.
	 */

	static {
		ArrayList<Priority> list = new ArrayList<Priority>();
		list.add(LOW = new Priority(0, "low"));
		list.add(MEDIUM = new Priority(1, "medium"));
		list.add(HIGH = new Priority(2, "high"));
		//
		// hash the values into the private hash, too, and put them into the ARRAY
		// should we check for duplicates here? naahh...
		//
		{
			HashMap<Integer, Priority> map = new HashMap<Integer, Priority>();
			HashMap<String, Priority> reverseMap = new HashMap<String, Priority>();
			for (Priority priority : list) {
				map.put(new Integer(priority.value), priority);
				// the reverse map takes up the *lowercase* string
				reverseMap.put(priority.toString().toLowerCase(), priority);
			}
			MAP_BY_ID = Collections.unmodifiableMap(map);
			MAP_BY_NAME = Collections.unmodifiableMap(reverseMap);
		}
		{
			List<Priority> list2 = new ArrayList<Priority>();
			list2.add(LOW);
			list2.add(MEDIUM);
			list2.add(HIGH);
			LIST_LOW_TO_HIGH = Collections.unmodifiableList(list2);
		}
		{
			List<Priority> list3 = new ArrayList<Priority>();
			list3.add(HIGH);
			list3.add(MEDIUM);
			list3.add(LOW);
			LIST_HIGH_TO_LOW = Collections.unmodifiableList(list3);
		}
	}

	/**
	 * The constructor, can only called by this class. The class defines
	 * the possible cases using the constructor and that's it. Constructor is PRIVATE! 
	 */

	private Priority(int x, String name) {
		assert x>=0; // exceptionally, allow 0
		assert name!=null;
		assert !"".equals(name.trim());
		this.value = x;
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
	 * Is a value valid, i.e. is there a Priority for this value
	 */

	public static boolean isValid(int x) {
		return (MAP_BY_ID.containsKey(new Integer(x)));
	}

	/**
	 * Comparison compares the underlying values
	 */

	@Override
    public boolean equals(Object obj) {
		if (this == obj) {
			return true; // quick guess
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Priority)) {
			return false;
		}
		Priority other = (Priority) obj;
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
	 * Warns and returns the nearest approximation if there is no instance corresponding to 'x'
	 */

	public static Priority obtain(int x) {
		Priority res = MAP_BY_ID.get(Integer.valueOf(x));
		if (res == null) {
			Logger logCat = LOGGER_obtain;
			logCat.warn("A Priority corresponding to value " + x + " was requested; returning nearest approximation instead");
			if (x < Priority.LOW.value) {
				return LOW;
			} else {
				// as the priority values are continuous, only HIGH can be the solution
				return HIGH;
			}
		} else {
			return res;
		}
	}

	public static Priority obtain(Integer x) {
	    Check.notNull(x,"integer");
		return obtain(x.intValue());
	}

	/**
	 * Obtain a Priority from the passed String, which is lowercased and trimmed
	 * before being handled and must yield either 'high', 'medium' or 'low'. In
	 * case no match is found, the 'defaultValue' is returned. That value can be (null).
	 * You may also want to throw an IllegalArgumentException, set 'throwIfNoMatch' to
	 * true.
	 */

	public static Priority obtain(String x, Priority defaultValue, boolean throwIfNoMatch) {
		Priority p = null;
		if (x != null) {
			p = MAP_BY_NAME.get(PropertyName.namify(x));
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
	 * Comparable implementation. This implementation is consistent with equals().
	 * Throw is (null) is passed. 
	 * Result negative: this object is "less than" other.
	 * Result 0: this object is "equal to" other.
	 * Result positive: this object is "greater than" other.
	 * The magnitude of the value has no direct significance. 
	 */

	@Override
    public int compareTo(Priority other) {
	    Check.notNull(other,"other priority");
	    return this.value - other.value;
	}

	/**
	 * Make string where possibly values are comma-separated. Used in 'usage' strings
	 */

	public static String toUsageString() {
		StringBuilder buf = new StringBuilder();
		boolean addComma = false;
		for (Priority p : LIST_LOW_TO_HIGH) {
			if (addComma) {
				buf.append(",");
			}
			buf.append("'");
			buf.append(p);
			buf.append("'");
			addComma = true;
		}
		return buf.toString();
	}

}
