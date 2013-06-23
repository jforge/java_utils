package com.mplify.mutable;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A boolean which can be modified in called methods (thus it implements a 
 * pass-and-return value)
 * 
 * 2013.02.11 - Some extensions for Groovy ease-of-use
 ******************************************************************************/

public class MutableBoolean {

    private boolean value;

    public MutableBoolean(boolean init) {
        value = init;
    }

    public synchronized void set(boolean x) {
        value = x;
    }

    public synchronized boolean get() {
        return value;
    }

    public synchronized boolean booleanValue() {
        return value;
    }

    // this one is for groovy: MutableBoolean x; x as Boolean
    public synchronized boolean asBoolean() {
        return value;
    }

    public synchronized boolean isTrue() {
        return value;
    }

    public synchronized boolean isFalse() {
        return !value;
    }

    @Override
    public synchronized String toString() {
        return Boolean.toString(value);
    }

}