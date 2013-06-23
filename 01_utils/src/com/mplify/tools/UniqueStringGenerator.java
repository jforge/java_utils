package com.mplify.tools;


/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A class that returns a unique String on every call to "getNext()".
 * This string can be used to index stuff in a unique way.
 * 
 * 2007.01.18 - Created
 * 2009.05.26 - Dropping milliseconds and hexifying for legibility in log
 * 2009.11.03 - Subindexing makes things looks weird, just step instead.
 * 2012.12.17 - Applying a permutation to make strings more dissimilar
 *              but in the end, this is stupid so removed again.
 ******************************************************************************/

public class UniqueStringGenerator {

	private long lasttime = (System.currentTimeMillis()/1000L); // moment of last name creation; seconds should do

	/**
	 * Get a "next value", which is basically a timestamp unequal to any prior timestamp, but permuted 
	 */
	
	public synchronized String getNext() {	    
		long newtime = (System.currentTimeMillis()/1000L); // seconds should do -- 
		// Seconds are currently (2012) at around "1355748002", these are 30.33 bits; 32 bit should suffice for unsigned display, then		
		// Check whether the new time is actually larger than lasttime, if not, inc. the index
		if (lasttime >= newtime) {
		    // just set newtime to the "future" with a minimal jump
		    lasttime = lasttime + 1;			
		} else {
			lasttime = newtime;
		}
		// Permutation is of no use and hashing might destroy uniqueness...
		// So we don't permute
		return Long.toHexString(lasttime);
	}
}
