package com.mplify.names;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 ******************************************************************************
 *******************************************************************************
 * A string which expresses that it *isn't* null but that it *is* trimmed
 * 
 * Compare with AbstractName, which, additionally, is lowercase and never empty
 * 
 * 2007.01.02 - Created based on exiting code
 * 2010.10.01 - Added make()
 ******************************************************************************/

public class TrimmedString {

    private final String x;

    // won't accept null

    public TrimmedString(String x) {
        this(x, false);
    }

    // will accept null if 'lenient'

    public TrimmedString(String x, boolean lenient) {
        if (x == null) {
            if (lenient) {
                this.x = "";
            } else {
                throw new IllegalArgumentException("The passed String 'x' is (null)");
            }
        } else {
            this.x = x.trim();
        }
    }

    @Override
    public String toString() {
        return x;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof TrimmedString) {
            TrimmedString other = (TrimmedString) obj;
            return x.equals(other.x);
        } else if (obj instanceof String) {
            String other = (String) obj;
            return x.equals(other);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return x.hashCode();
    }

    /**
     * Is this the empty string?
     */
    
    public boolean isEmpty() {
        return "".equals(x);
    }
    
    /**
     * Try to make a TrimmedString from a String.
     * Returns (null) if "x" is (null), otherwise returns the "TrimmedString" based on x,
     * which might well be the empty string.
     */
    
    public static TrimmedString make(String x) {
    	if (x==null) {
    		return null;
    	}
    	else {
    		return new TrimmedString(x);
    	}
    }
}
