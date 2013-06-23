package com.mplify.id.ranges;


/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Representation of a range of integers of size 1 or larger. A zero-sized 
 * range cannot be represented.
 * 
 * This range is an immutable structure.
 * 
 * 2011.10.10 - Derived from IdRange<T extends AbstractId> 
 ******************************************************************************/

public class UnmodifiableIntRange implements IntRange {

    private final int low; 
    private final int high;
    private String toStringBuf = null;

    /**
     * Constructor yielding a range based on two integers.
     * (it is not necessary that a <= b; it may be that a > b, the
     * constructor will reorder the values. If a = b, we are talking about
     * a range of size 1 holding value "a"
     */
    
    public UnmodifiableIntRange(int a, int b) {
        if (a < b) {
            this.low = a;
            this.high = b;
        } else {
            this.low = b;
            this.high = a;
        }
    }

    /**
     * Constructor of a range of size 1
     */
    
    public UnmodifiableIntRange(int a) {
        this.low = a;
        this.high = a;
    }

    /**
     * Getter
     */
    
    @Override
    public int getLow() {
        return low;
    }

    /**
     * Getter
     */

    @Override
    public int getHigh() {
        return high;
    }

    /**
     * Stringification
     */
    
    @Override
    public String toString() {
        if (toStringBuf == null) {
            if (low == high) {
                toStringBuf = "[" + low + "]";
            } else {
                toStringBuf = "[" + low + "-" + high + "]";
            }
        }
        return toStringBuf;
    }

    /**
     * Size of range. Returns x >= 1
     */
    
    @Override
    public int size() {
        int res = (high - low) + 1;
        assert res >= 1;
        return res;
    }    
}