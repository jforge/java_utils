package com.mplify.names;

import com.mplify.checkers.Check;


/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 ******************************************************************************
 *******************************************************************************
 * A string which expresses that it *isn't* null and that it *is* trimmed and
 * that it *isn't* empty
 * 
 * 2010.08.10 - Created based on exiting code
 * 2010.10.01 - Added make()
 * 2011.12.21 - Introduced _check
 * 2012.08.07 - Added Comparable for sorting
 ******************************************************************************/

public class TrimmedNonemptyString implements Comparable<TrimmedNonemptyString> {

    private final String x;

    /**
     * Constructor won't accept null nor the empty string nor the whitespace-only string
     */

    public TrimmedNonemptyString(String x) {
        Check.notNull(x,"string");
        this.x = x.trim();
        Check.isFalse("".equals(this.x),"The passed String contains only whitespace or is empty");
    }

    /**
     * Give the string value
     */
    
    @Override
    public String toString() {
        return x;
    }

    /**
     * Equality is based on String contents. Comparison with types String, TrimmedString and TrimmedNonemptyString is allowed
     */
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true; // quick guess
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof TrimmedNonemptyString) {
            TrimmedNonemptyString other = (TrimmedNonemptyString) obj;
            return x.equals(other.x);
        }
        if (obj instanceof TrimmedString) {
            TrimmedString other = (TrimmedString) obj;
            return x.equals(other.toString());
        }
        if (obj instanceof String) {
            String other = (String) obj;
            return x.equals(other);
        }
        return false;
    }

    /**
     * Hash is based entirely on the underlying string
     */
    
    @Override
    public int hashCode() {
        return x.hashCode();
    }
    
    /**
     * Try to make a TrimmedNonemptyString from a String.
     * Returns (null) if "x" is (null) or contains only whitespace, otherwise returns the "TrimmedNonemptyString" based on x.
     */

    public static TrimmedNonemptyString make(String x) {
    	if (x==null || "".equals(x.trim())) {
    		return null;
    	}
    	else {
    		return new TrimmedNonemptyString(x);
    	}
    }

    /**
     * Trivial comparison
     */
    
    @Override
    public int compareTo(TrimmedNonemptyString o) {
        Check.notNull(o,"trimmed nonempty string");
        return this.x.compareTo(o.x);
    }
    
}