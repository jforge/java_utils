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
 * Simple enum indicating whether a value was encrypted or not
 * 
 * Currently there is no indication about what algorithm was used.
 * 
 * 2011.06.16 - Created
 * 2011.11.09 - Transformed from an EnumerativeTypeUsingInteger to an enum
 ******************************************************************************/

public enum EncryptionStatus implements EnumBasedOnInteger {

    UNENCRYPTED(0, "unencrypted"), ENCRYPTED(1, "encrypted");

    private final int value;
    private final PropertyName name;

    EncryptionStatus(int value, String name) {
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

    public static EncryptionStatus obtain(Integer x, boolean throwIfNotFound) {
        _check.notNull(x, "integer");
        EncryptionStatus res = null;
        if (x.intValue() == UNENCRYPTED.value) {
            res = UNENCRYPTED;
        } else if (x.intValue() == ENCRYPTED.value) {
            res = ENCRYPTED;
        }
        if (res == null && throwIfNotFound) {
            throw new IllegalArgumentException("No " + EncryptionStatus.class.getName() + " instance corresponding to value '" + x + "' exists");
        } else {
            return res; // may be null
        }
    }

    /**
     * Obtain from a string. Does not throw if (null) is passed (but in that case, nothing will be found). Throws if not found on demand.
     */

    public static EncryptionStatus obtain(String x, boolean throwIfNotFound) {
        EncryptionStatus res = null;
        if (x != null) {
            String y = x.toLowerCase().trim(); // make "x" canonical
            if (UNENCRYPTED.name.toString().equals(y)) {
                res = UNENCRYPTED;
            } else if (ENCRYPTED.toString().equals(y)) {
                res = ENCRYPTED;
            }
        }
        if (res == null && throwIfNotFound) {
            throw new IllegalArgumentException("No " + EncryptionStatus.class.getName() + " instance corresponding to value '" + x + "' exists");
        } else {
            return res; // may be null
        }
    }

}
