package com.mplify.threads;

import java.text.NumberFormat;
import java.util.Date;

import org.slf4j.Logger;

import com.mplify.logging.DateTexter;
import com.mplify.logging.Story;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A simple class used in doing sleeptime computation.
 *
 * 2001.11.29 - Class created
 * 2002.04.22 - Added getLoad() 
 * 2008.12.03 - Unsure whether still used. Made package visible only to see.
 ******************************************************************************/

class SleeptimeManager {

//	private final static String CLASS = SleeptimeManager.class.getName();

	private final int sleeptime_max_ms; // maximum sleeptime possible
	private final int sleeptime_delta_ms; // by how much to vary the sleeptime
	private int sleeptime_ms; // actual sleeptime to apply    
	private double sleeptime_dxdt_ms; // differential change in sleeptime ms/ms, just for curiosity
	private long startOfdoWork; // when did the 'do Work' procedure start?
	private long expectedLinger_ms; // time doWork() will approx.take          

	/**
	 * Constructor checks the passed values & throws an exception if there is a problem
	 */

	public SleeptimeManager(int sleeptime_ms, int sleeptime_max_ms, int sleeptime_delta_ms) {
		if (sleeptime_delta_ms < 0) {
			throw new IllegalArgumentException("Passed sleeptime delta was <0 : " + sleeptime_delta_ms);
		}
		if (sleeptime_ms <= 0) {
			throw new IllegalArgumentException("Passed sleeptime was <=0 : " + sleeptime_ms);
		}
		if (sleeptime_max_ms <= 0) {
			throw new IllegalArgumentException("Passed sleeptime maximum was <=0 : " + sleeptime_max_ms);
		}
		if (sleeptime_max_ms < sleeptime_ms) {
			throw new IllegalArgumentException("Passed sleeptime maximum (" + sleeptime_max_ms + ") was < sleeptime (" + sleeptime_ms + ")");
		}
		this.sleeptime_ms = sleeptime_ms;
		this.sleeptime_delta_ms = sleeptime_delta_ms;
		this.sleeptime_dxdt_ms = 0;
		this.startOfdoWork = System.currentTimeMillis();
		this.expectedLinger_ms = sleeptime_ms;
		this.sleeptime_max_ms = sleeptime_max_ms;
	}

	/**
	 * Decrement the sleeptime somewhat
	 */

	public void decSleeptime() {
		modSleeptime(-sleeptime_delta_ms);
	}

	/**
	* Get the expected linger (time doWork() will approximately take)
	*/

	public long getExpectedLinger_ms() {
		return Math.max(1, expectedLinger_ms);
	}

	/**
	 * Generate output describing structure
	 */

	public Story toStory() {
		NumberFormat fmt = NumberFormat.getNumberInstance();
		fmt.setMaximumFractionDigits(3);
		Story res = new Story();
		res.add("Sleeptime: " + sleeptime_ms + " ms");
		res.add("Sleeptime max: " + sleeptime_max_ms + " ms");
		res.add("Sleeptime delta: " + sleeptime_delta_ms + " ms");
		res.add("Sleeptime dx/dt: " + fmt.format(sleeptime_dxdt_ms));
		res.add("Start of doWork(): " + DateTexter.EXTENDED.inDefault(new Date(startOfdoWork)));
		res.add("Expected linger time: " + expectedLinger_ms + " ms");
		return res;
	}

	/**
	 * This is indirectly called by the dealer thread's doWork() to signal end-of-do-work.
	 * It computes a new 'expectedLinger_ms' (time doWork() will approx. take)
	 * as the mean of the last expectedLinger_ms and the new time-delta between
	 * start-of-do-work and end-of-do-work
	 */

	public void touchEndOfDoWork() {
		long endOfDoWork = System.currentTimeMillis();
		expectedLinger_ms = (expectedLinger_ms + (endOfDoWork - startOfdoWork)) / 2;
	}

	/**
	 * This is called by the dealer thread's doWork() to signal start-of-do-work.
	 */

	public void touchStartOfDoWork() {
		startOfdoWork = System.currentTimeMillis();
	}

	/**
	 * Modify the sleeptime somewhat; the new one is the mean of the old
	 * and new temporary one -- the latter being clamped between 1 and sleeptime_max_ms
	 */

	private void modSleeptime(int delta) {
		int old_sleeptime_ms = sleeptime_ms;
		sleeptime_ms = Math.min(sleeptime_max_ms, Math.max(sleeptime_ms + delta, 1));
		double dx = sleeptime_ms - old_sleeptime_ms;
		double dt = getExpectedLinger_ms();
		sleeptime_dxdt_ms = 0.5 * (dx / dt + sleeptime_dxdt_ms);
	}

	/**
	 * Increment the sleeptime somewhat
	 */

	public void incSleeptime() {
		modSleeptime(+sleeptime_delta_ms);
	}

	/**
	 * Get the actual time-to-sleep in milliseconds
	 */

	public int getSleeptime_ms() {
		return sleeptime_ms;
	}

	/**
	 * Sleep a bit, then adjust the sleeptime. This is supposed to be called
	 * at the end of a 'working procedure' at the start of which
	 * 'touchStartOfDoWork()' is supposed to have been called. A logging
	 * Category can be passed. The sleeptime will be incremented if 'inc'
	 * and decremented if 'dec'.
	 * Note that this function automatically calls touchEndOfDoWork()
	 */

	public void sleepOMatic(boolean inc, boolean dec, Logger logger) {
		try {
			Thread.sleep(getSleeptime_ms());
		} catch (Exception tio) {
			if (logger != null) {
			    logger.warn("Woke up with exception: " + tio.getMessage());
			}
		}
		//
		// adjust the sleeptime 
		//
		touchEndOfDoWork();
		if (dec) {
			decSleeptime();
		}
		if (inc) {
			incSleeptime();
		}
	}

	/**
	 * Get the 'load' which is computed as (sleeptime_max_ms-sleeptime_ms)/sleeptime_max_ms
	 * and thus 1 if the sleeptime_ms is 0
	 */

	public double getLoad() {
		double sleeptime_ms = this.sleeptime_ms;
		double sleeptime_max_ms = this.sleeptime_max_ms;
		return (sleeptime_max_ms - sleeptime_ms) / sleeptime_ms;
	}
}
