package com.mplify.msgserver.addressing;

import com.mplify.checkers._check;
import com.mplify.properties.PayloadKey;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A class which represents an attribute-value pair to be used in address
 * attributes.
 * 
 * 2009.01.27 - Created because we need to have TypeOfNumber and 
 *              NumberingPlanIdentifier in address.
 * 2009.12.22 - Now uses PayloadKey as "key" instead of "String", making
 *              assumptions clearer.
 ******************************************************************************/

public class TvecPair {
    
    private final PayloadKey key; // not null
    private final String value; // not null, possibly empty, not trimmed
    
    /**
     * Constructor using PayloadKey and value
     */
    
    public TvecPair(PayloadKey key,String value) {
        _check.notNull(key,"key");
        _check.notNull(value,"value");
        this.key = key;
        this.value = value;
    }

    /**
     * Syntactic sugar: Instead of a PayloadKey, use a String
     */
    
    public TvecPair(String key,String value) {
        _check.notNull(key,"key");
        _check.notNull(value,"value");        
        this.key = new PayloadKey(key); // bad values will throw!
        this.value = value;
    }

    /**
     * Never returns null
     */
    
    public PayloadKey getKey() {
        return key;
    }

    /**
     * Never returns null, but may return the empty string
     */

    public String getValue() {
        return value;
    }
}
