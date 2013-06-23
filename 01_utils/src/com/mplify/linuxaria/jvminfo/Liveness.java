package com.mplify.linuxaria.jvminfo;

import java.util.Date;

import com.mplify.mbeans.MBeanMarker;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Yield "liveness" information, which consists in:
 * 
 * - A startup date of a process, thread or whatever (or rather, the date at 
 *   which this class was loaded, which should be approximately the same)
 * - The liveness value, a float value that starts off at 1.0 and exponentially
 *   decays over the lifetime of the process. Useful to find out when restarts
 *   happened, if graphed along the time axis.
 * 
 * In order for JMX to recognize this as a StandardMBean implementing
 * "LivenessMBean", the class MUST be named "Liveness"
 * 
 * 2011.03.28 - Created 
 ******************************************************************************/

public class Liveness implements LivenessMBean, MBeanMarker {

    private final static Date creationDate = new Date();
    private final static double HALF_LIFE_IN_MS = 3600000; 
    private final static double k = Math.log(2) / HALF_LIFE_IN_MS; 
    
    @Override
    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public double getLiveness() {
        double delta_ms = System.currentTimeMillis() - creationDate.getTime();
        return Math.exp(-k * delta_ms);
    }
    
}
