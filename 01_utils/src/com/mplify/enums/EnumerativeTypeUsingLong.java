package com.mplify.enums;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A marker interface used to find back enumerative types.
 * 
 * 2009.02.06 - Created for SMPP interface, which has 32-bit unsigned integer
 *              ids.
 ******************************************************************************/


public interface EnumerativeTypeUsingLong extends EnumerativeType {

    /**
     * For database insertions, get the numeric value
     */
    
    public long getValue();
}
