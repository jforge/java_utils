package com.mplify.enums;

import java.util.Set;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Something to collect helper procedures 
 * 
 * 2005.XX.XX - Created
 ******************************************************************************/

public class EnumerativeTypeHelper {

	/**
	 * Constructor cannot be called, so this class cannot be instantiated
	 */
	
	private EnumerativeTypeHelper() {
		// NOP
	}
	
	/**
	 * Helper: add something to a set of stuff.
	 * Note that this method may also use non-generic parameters:
	 *   public static void myAdd(Set set, Object x)
	 * but for niceness  
	 */

	public static <T> void myAdd(Set<T> set, T x) {
		assert set != null : "The passed Set is (null)";
		assert x != null : "The passed Object to insert into Set is (null)";
		boolean added = set.add(x);
		assert added : "Could not add Object " + x + " to the Set";
	}
}
