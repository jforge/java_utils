package com.mplify.mutable;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A String which can be modified in called methods (thus it implements a 
 * pass-and-return value)
 * 
 * It is also fully synchronized
 * 
 * 2011.06.04 - Created
 * 2012.07.19 - Added constructor taking String
 ******************************************************************************/

public class MutableString {

    private String string;
    
    public MutableString() {
        string = null;
    }

    public MutableString(String x) {
        string = x;
    }

    public synchronized String get() {
        return string;
    }
    
    public synchronized void set(String x) {
        string = x;
    }

    @Override
    public synchronized String toString() {
        if (string==null) {
            return "(null)";
        }
        else {
            return string;
        }
    }
}
