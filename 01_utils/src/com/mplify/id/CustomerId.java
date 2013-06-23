package com.mplify.id;


/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A wrapper for a numeric identifier that represents an NSP role id
 * Note that the toString() is implemented by the superclass.
 * 
 * 2006.08.31 - Created
 * 2007.01.02 - Review; some code moved to superclass.
 * 2007.03.15 - Added 'checkedEquals()' to perform type-checked equals() 
 *              operation. Production code was buggy due to simple equals(Object)
 * 2010.09.30 - Renamed to "NspRoleId" -------> "CustomerId"
 * 2013.03.14 - Simplified, no longer dependes on org.w3c.dom.Node 
 ******************************************************************************/
 
public class CustomerId extends AbstractId {

	/**
	 * Construct from an integer id, which must be > 0
	 */

	public CustomerId(int x) {
		super(x);
	}
	
	/**
	 * Construct from an Integer id, which must be > 0
	 */

	public CustomerId(Integer x) {
		super(x);
	}

	/**
	 * Comparison with checked type
	 */
	
	public final boolean checkedEquals(CustomerId other) {
		return this.equals(other);
	}
	
}
