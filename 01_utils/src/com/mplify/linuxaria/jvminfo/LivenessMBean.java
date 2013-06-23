package com.mplify.linuxaria.jvminfo;

import java.util.Date;

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
 * - The process startup date (or rather, the date at which this class
 *   was loaded, which should be approximately the same)
 * - The liveness value, a double value that starts off at 1.0 and exponentially
 *   decays over the lifetime of the process (the half-life is encoded in the
 *   implementation, see there=. Useful to find out when restarts happened, 
 *   if graphed along the time axis.
 * 
 * This currently is a "standard MBean"
 * 
 * 2011.03.28 - Created 
 * 2011.03.30 - Renamed to "LivenessMBean" as it's not necessarily baout the 
 *              process only.
 ******************************************************************************/

public interface LivenessMBean {
    
    public Date getCreationDate();
    
    public double getLiveness();

}
