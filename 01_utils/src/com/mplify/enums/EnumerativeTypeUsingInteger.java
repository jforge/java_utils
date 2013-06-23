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
 * 2006.12.21 - Created
 * 2008.09.04 - getValue() moved naturally to this place, very useful for
 *              database insertions
 ******************************************************************************/


public interface EnumerativeTypeUsingInteger extends EnumerativeType {

    /**
     * For database insertions, get the numeric value
     */
    
    public int getValue();
}
