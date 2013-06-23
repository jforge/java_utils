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
 * Generic integer range, which may be mutable or final
 * 
 * 2011.10.10 - Derived from existing code 
 ******************************************************************************/

public interface IntRange {

    /**
     * Getter
     */
    
    public int getLow();

    /**
     * Getter
     */

    public int getHigh();
    
    /**
     * Size of range. Returns x >= 1
     */
    
    public int size();
    
}
