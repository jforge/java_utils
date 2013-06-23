package com.mplify.id.ranges;

import com.mplify.checkers.Check;
import com.mplify.id.AbstractId;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2009, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Representation of a range of 1 or more integer-based ids. A zero-sized 
 * range cannot be represented.
 * 
 * 2009.12.18 - Moved out of RangeSequence to its own toplevel class
 * 2010.09.27 - Moved from the specialized project "70_msgserver_cli" 
 *              to project "04_core_low" and package "com.mplify.id_ranges".
 * 2012.10.13 - Added generateSqlFactor() and introduced Check
 ******************************************************************************/

public class IdRange<T extends AbstractId> {

    private final T low;
    private final T high;
    private String toStringBuf = null;

    /**
     * Constructor yielding a range based on the two non-null concrete Ids
     * (it is not necessary that a <= b; it may be that a > b, the
     * constructor will reorder the values. If a = b, we are talking about
     * a range of size 1 holding value "a"
     */

    public IdRange(T a, T b) {
        assert a != null;
        assert b != null;
        if (a.getValue() < b.getValue()) {
            this.low = a;
            this.high = b;
        } else {
            this.low = b;
            this.high = a;
        }
    }

    /**
     * Constructo of a range of size 1
     */

    public IdRange(T a) {
        assert a != null;
        this.low = a;
        this.high = a;
    }

    /**
     * Getter; return reference
     */

    public T getLow() {
        return low;
    }

    /**
     * Getter; return reference
     */

    public T getHigh() {
        return high;
    }

    /**
     * Stringification
     */

    @Override
    public String toString() {
        if (toStringBuf == null) {
            if (low.getValue() == high.getValue()) {
                toStringBuf = "[" + low.getValue() + "]";
            } else {
                toStringBuf = "[" + low.getValue() + "-" + high.getValue() + "]";
            }
        }
        return toStringBuf;
    }

    /**
     * Size of range. Returns x >= 1
     */

    public int size() {
        int res = (high.getValue() - low.getValue()) + 1;
        assert res >= 1;
        return res;
    }

    /**
     * Generate an SQL WHERE criterium on the given fieldName. It's called a "factor" because it will be AND-ed with
     * other factors. It is an error if this is called with an empty range sequence.
     */

    public String generateSqlFactor(String fieldName, boolean withParentheses) {
        Check.notNull(fieldName, "field name");
        StringBuilder buf = new StringBuilder();
        if (withParentheses) {
            buf.append("(");
        }
        if (size() == 1) {
            buf.append(fieldName);
            buf.append("=");
            buf.append(getLow().getValue());
        } else {
            buf.append(fieldName);
            buf.append(">=");
            buf.append(getLow().getValue());
            buf.append(" AND ");
            buf.append(fieldName);
            buf.append("<=");
            buf.append(getHigh().getValue());
        }
        if (withParentheses) {
            buf.append(")");
        }
        return buf.toString();
    }
}