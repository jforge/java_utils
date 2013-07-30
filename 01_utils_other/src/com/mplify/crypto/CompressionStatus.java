package com.mplify.crypto;

import com.mplify.checkers.Check;
import com.mplify.enums.EnumBasedOnInteger;
import com.mplify.properties.PropertyName;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Simple enum indicating whether a value was compressed or not
 * 
 * Currently there is no indication about what algorithm was used.
 * 
 * 2011.06.16 - Created
 * 2011.11.09 - Removed ORIGINAL as redundant and confusing 
 * 2011.11.09 - Transformed from an EnumerativeTypeUsingInteger to an enum
 * 2011.12.30 - Added "compression useless"
 ******************************************************************************/

public enum CompressionStatus implements EnumBasedOnInteger {
    
    UNCOMPRESSED(0, "uncompressed"), COMPRESSED(1, "compressed"), COMPRESSION_USELESS(2, "compression_useless");

    private final int value;
    private final PropertyName name;

    CompressionStatus(int value, String name) {
        assert value >= 0;
        this.name = new PropertyName(name);
        this.value = value;
    }

    /**
     * Accessor
     */
    
    @Override
    public int getValue() {
        return value;
    }
    
    /**
     * Accessor
     */
    
    public PropertyName getName() {
        return name;
    }
    
    /**
     * Stringify
     */
    
    @Override
    public String toString()  {
        return name.toString();
    }
    
    /**
     * Obtain from an Integer. Throws if (null) is passed. Throws if not found on demand.
     */

    public static CompressionStatus obtain(Integer x, boolean throwIfNotFound) {
        _check.notNull(x, "integer");
        CompressionStatus res = null;
        if (x.intValue() == UNCOMPRESSED.value) {
            res = UNCOMPRESSED;
        } else if (x.intValue() == COMPRESSED.value) {
            res = COMPRESSED;
        } else if (x.intValue() == COMPRESSION_USELESS.value) {
            res = COMPRESSION_USELESS;
        } else {
            // NOP, problem will be caught directly below
        }
        if (res == null && throwIfNotFound) {
            throw new IllegalArgumentException("No " + CompressionStatus.class.getName() + " instance corresponding to value '" + x + "' exists");
        } else {
            return res; // may be null
        }
    }

    /**
     * Given a string, find acceptable CompressBehaviour. Does not throw if (null) is passed (but in that case, nothing will be found). Throws if not found on demand.
     */

    public static CompressionStatus obtain(String x, boolean throwIfNotFound) {
        CompressionStatus res = null;
        if (x != null) {
            String y = x.toLowerCase().trim(); // make "x" canonical
            if (UNCOMPRESSED.name.toString().equals(y)) {
                res = UNCOMPRESSED;
            } else if (COMPRESSED.toString().equals(y)) {
                res = COMPRESSED;
            } else if (COMPRESSION_USELESS.toString().equals(y)) {
                res = COMPRESSION_USELESS;
            } else {
                // NOP, problem will be caught directly below
            }            
        }
        if (res == null && throwIfNotFound) {
            throw new IllegalArgumentException("No " + CompressionStatus.class.getName() + " instance corresponding to value '" + x + "' exists");
        } else {
            return res; // may be null
        }
    }

}
