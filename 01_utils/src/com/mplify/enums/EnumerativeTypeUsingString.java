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
 * 2008.09.04 - Created to distinguihs it from EnumerativeType where getValue()
 *              returns an Integer instead of a String
 ******************************************************************************/


public interface EnumerativeTypeUsingString extends EnumerativeType {

    /**
     * For database insertions (?) get the string value
     */
    
    public String getValue();
}
