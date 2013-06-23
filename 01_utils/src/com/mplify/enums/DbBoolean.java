package com.mplify.enums;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A simple bi-state enumerative value: used to map Booleans to the database
 * and vice-versa. Does not use a map.
 * 
 * 2004.10.04 - Created. 
 * 2005.05.02 - Mobilux code compared with this class. This class moved from
 *              com.mplify.scf.core.values to here.
 ******************************************************************************/

public class DbBoolean implements EnumerativeTypeUsingInteger {

//	private final static String CLASS = DbBoolean.class.getName();

//	private final static Logger LOGGER_obtain = LoggerFactory.getLogger(CLASS + ".obtain");
	
	/*
	 * Members. Identified by 'value', which is also what is used in the database
	 * The String stores explanations for this value. As no new instances of this class are ever
	 * created, this is absolutely not inefficient.
	 */

	private final int value;
	private final String shortText;

	/*
	 * These are the possible instance of this class; they form a quasi-enumeration
	 * These are public and used by client code.
	 */

	public static final DbBoolean TRUE;
	public static final DbBoolean FALSE;
	
	/*
	 * An list of the values, public but immutable 
	 */

	public final static List<DbBoolean> LIST;

	/*
	 * Static construction takes care to assign unique (and constant) numeric identifiers
	 * to the various instances. They must be stable because they are used in the database. 
	 * At the same time, the values are assigned to the 'list', which will then be
	 * made immutable and assigned to LIST.
	 */

	static {
		ArrayList<DbBoolean> myList = new ArrayList<DbBoolean>();
		myList.add(FALSE = new DbBoolean(0,"false")); 
		myList.add(TRUE = new DbBoolean(1,"true"));
		LIST = Collections.unmodifiableList(myList);
	}

	/**
	 * The constructor, can only called by this class. The class defines
	 * the possible cases using the constructor and that's it. Constructor is PRIVATE! 
	 */

	private DbBoolean(int x, String shortText) {
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
	 * Transform to a Boolean
	 */

	public Boolean toBoolean() {
		// fastest comparison?
		if (this.value == TRUE.value) {
			return Boolean.TRUE;
		}
		else {
			return Boolean.FALSE;
		}
	}

	/**
	 * Get the boolean value
	 */
	
	public boolean booleanValue() {
		// fastest comparison?
		return (this.value == TRUE.value);
	}
	
	/**
	 * Transform to a Logical NOT. 
	 */

	public DbBoolean toNot() {
		// fastest comparison?
		if (this.value == TRUE.value) {
			return DbBoolean.FALSE;
		}
		else {
			return DbBoolean.TRUE;
		}
	}

	/**
	 * Is a value valid, i.e. is there a Priority for this value
	 */

	public static boolean isValid(int x) {
		return (0<=x && x<=1);
	}

	/**
	 * Comparison compares the underlying values
	 * TODO: use superequals or replace by '=='
	 */

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true; // quick guess
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DbBoolean)) {
			return false;
		}
		DbBoolean other = (DbBoolean) obj;
		return (other.value == this.value);
	}

	/**
	 * Get the actual underlying value (use sparingly, e.g. for database updates)
	 */

	@Override
    public int getValue() {
		return value;
	}

	/**
	 * Obtain from a Boolean. Boolean.TRUE yields TRUE,
	 * Boolean.FALSE yields FALSE.
	 */
	
	public static DbBoolean obtain(Boolean x) {
		if (x==null) {
			throw new IllegalArgumentException("The passed value is (null)");
		}
		else if (x.booleanValue()) {
			// fastest decision for value of x?
			return DbBoolean.TRUE;
		}
		else {
			return DbBoolean.FALSE;
		}
	}

	/**
	 * Obtain from a boolean. Can only return Troolean.TRUE or Troolean.FALSE
	 */
	
	public static DbBoolean obtain(boolean x) {
		if (x) {
			return DbBoolean.TRUE;
		}
		else {
			return DbBoolean.FALSE;
		}
	}
		
	/**
	 * Return a value instance given an int. Note that if -1 is passed (case of 'unset' stuff in the
	 * DB for example) an Exception will be thrown
	 */

	public static DbBoolean obtain(int x) {
		if (x==1) {
			return DbBoolean.TRUE;
		}
		else if (x==0) {
			return DbBoolean.FALSE;
		}
		else {
			throw new IllegalArgumentException("A DbBoolean corresponding to value " + x + " was requested");
		}
	}
	
	/**
	 * Useful when retrieving from database
	 */
	
	public static DbBoolean obtain(Integer x) {
		if (x==null) {
			throw new IllegalArgumentException("The passed Integer was (null)");
		}
		else {
			return obtain(x.intValue());
		}
	}
	
	/**
	 * Return a value instance given an String.
	 */

	public static DbBoolean obtain(String x) {
		if (DbBoolean.FALSE.shortText.equals(x)) {
			return DbBoolean.FALSE;
		}
		else if (DbBoolean.TRUE.shortText.equals(x)) {
			return DbBoolean.TRUE;
		}
		else {
			// this is LESS lenient than Boolean.valueOf(String), which eats everything
			throw new IllegalArgumentException("A DbBoolean corresponding to text '" + x + "' was requested");
		}
	}

	/**
	 * Wanna hash this...
	 */

	@Override
	public int hashCode() {
		return value;
	}
}
