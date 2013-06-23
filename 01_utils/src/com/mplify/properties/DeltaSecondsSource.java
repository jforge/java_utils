package com.mplify.properties;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * An interface helper that is asked for refresh interval. There is generally
 * a properties handler underneath
 * 
 * 2010.11.02 - Create during refactoring of "refreshers"
 ******************************************************************************/

public interface DeltaSecondsSource {

    /**
     * This is supposed to return a value > 0
     */
    
    public int getRefreshIntervalInSeconds();

}