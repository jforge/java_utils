package com.mplify.threads;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.logging.DateTexter;
import com.mplify.logging.Story;
import com.mplify.logging.TimeDecayingAverage;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * This is bunch of values kept by Threads for statistical purposes. These
 * values are supposed to survive Thread death.
 * 
 * We collect the following statistical information:
 * 
 * Average Workload
 *   This is the (exponential decay average of) ratio of the time that the
 *   DealerThread spends actually working divided by the time elapse. This 
 *   value thus varies between 0 (only sleeping) to 1 (only working). It is
 *   updated by the DealerThread by calls to measureWorkloadAverage(sleeptime_ms), 
 *   where the passed time is the the time the DealerThread spent sleeping.
 *
 * Aliveness
 *   This is the (exponential decay average of) ratio of 0. The value
 *   starts off at 1. It is used to make visible in MRTG graphs the moment
 *   at which aliveness passed to 1,which is the time at which DealerThread
 *   was restarted.
 * 
 * Average Delay
 *   This is the  (exponential decay average of) the time spent
 *   communicating with a remote gateway.
 * 
 * 2003.09.24 - First relase using new classes. These values existed before in
 *              code but were not so well expressed 
 * 2003.10.11 - In the wee hours of the morning: Review, integration
 * 2003.10.19 - No longer associated to DealerThread only: general! It's
 *              called ThreadStatistics now.
 * 2004.04.16 - Added toPolyline()
 * 2006.04.24 - Modified for new TimeDecayingAverage
 ******************************************************************************/

public class ThreadStatistics {

	private final static String CLASS = ThreadStatistics.class.getName();

	private final double alivenessDecay_per_s; // the decay for the aliveness is storead as the 'TimeDecaying average may have to be recreated
	private TimeDecayingAverage aliveness; // an abstract measure between 0 and 1 measuring the thread's aliveness; recreated on Thread restart
	private final TimeDecayingAverage averageWorkload; // a value between 0 and 1 expressing the workload of the thread 
	private final TimeDecayingAverage averageDelay_ms; // average delay that can be expected for a transaction with the remote gateway
	private final TimeDecayingAverage averageThreadLoopTime_ms; // average time a passage through the thread loop takes

	/**
	 * Constructor, takes the decays to apply to the values. The passed
	 * decays are checked for reasonableness i.e. are they in [0,1] 
	 */

	public ThreadStatistics(double alivenessDecay_per_s, double averageDelayDecay_per_s, double averageWorkloadDecay_per_s, double averageThreadLoopTimeDecay_per_s) {
		if (averageDelayDecay_per_s < 0 || averageDelayDecay_per_s > 1) {
			throw new IllegalArgumentException("Passed averageDelayDecay is " + averageDelayDecay_per_s + " and thus outside of [0,1]");
		}
		if (alivenessDecay_per_s < 0 || alivenessDecay_per_s > 1) {
			throw new IllegalArgumentException("Passed alivenessDecay_per_s is " + alivenessDecay_per_s + " and thus outside of [0,1]");
		}
		if (averageWorkloadDecay_per_s < 0 || averageWorkloadDecay_per_s > 1) {
			throw new IllegalArgumentException("Passed averageWorkloadDecay_per_s is " + averageWorkloadDecay_per_s + " and thus outside of [0,1]");
		}
		if (averageThreadLoopTimeDecay_per_s < 0 || averageThreadLoopTimeDecay_per_s > 1) {
			throw new IllegalArgumentException("Passed averageThreadLoopTimeDecay_per_s is " + averageThreadLoopTimeDecay_per_s + " and thus outside of [0,1]");
		}
		this.alivenessDecay_per_s = alivenessDecay_per_s;
		resetAliveness();
		this.averageDelay_ms = new TimeDecayingAverage(averageDelayDecay_per_s);
		this.averageWorkload = new TimeDecayingAverage(averageWorkloadDecay_per_s);
		this.averageThreadLoopTime_ms = new TimeDecayingAverage(averageThreadLoopTimeDecay_per_s);
	}

	/**
	 * Recompute the current 'delay' average;  this is called regularly by the DealerThread
	 * which causes the 'delay' to be udpated according to the number of ms spent communicating 
	 * with the remote gateway
	 */

	public double measureAverageDelay(long delay_ms) {
		return averageDelay_ms.measure(delay_ms, System.currentTimeMillis());
	}

	/**
	 * Get the current 'delay' average without modifying it
	 */

	public double getAverageDelay_ms() {
		return averageDelay_ms.getAverage();
	}

	/**
	 * Recompute the current 'workload' average; this is called regularly by the DealerThread
	 * which causes the 'workload' to be udpated according to the number of ms spent sleeping 
	 * passed as parameter
	 */

	public double measureAverageWorkload(long sleeptime_ms) {
		long now = System.currentTimeMillis();
		long delta_ms = now - averageWorkload.getWhen();
		double newValue = ((double) (delta_ms - sleeptime_ms) / (double) delta_ms);
		if (newValue < 0) {
			Logger logCat = LoggerFactory.getLogger(CLASS + ".measureAverageWorkload");
			logCat.warn("The 'newValue' is negative, i.e. the sleeptime_ms (" + sleeptime_ms + ") is longer than the elapsed time (" + delta_ms + ")");
			return 0;
		} else {
			return averageWorkload.measure(newValue, now);
		}
	}

	/**
	 * Recompute the current DealerThread loop time; this is called once per loop 
	 */

	public double measureAverageThreadLoopTime_ms() {
		long now = System.currentTimeMillis();
		long delta_ms = now - averageThreadLoopTime_ms.getWhen();
		return averageThreadLoopTime_ms.measure(delta_ms, now);
	}

	/**
	 * Get the current DealerThread loop time 
	 */

	public double getAverageThreadLoopTime_ms() {
		return averageThreadLoopTime_ms.getAverage();
	}

	/**
	 * Get the current 'workload' average without modifying it
	 */

	public double getAverageWorkload() {
		return averageWorkload.getAverage();
	}

	/**
	 * Recompute the current 'aliveness' value; this is called regularly by the DealerThread
	 * which causes the 'aliveness' to decay to 0.
	 */

	public double measureAliveness() {
		return aliveness.measure(0.0, System.currentTimeMillis());
	}

	/**
	 * Get the current 'aliveness' value without modifiying it
	 */

	public double getAliveness() {
		return aliveness.getAverage();
	}

	/**
	 * Reset the 'aliveness' to 1. This is called whenever the DealerThread is restarted
	 */

	public void resetAliveness() {
		this.aliveness = new TimeDecayingAverage(alivenessDecay_per_s, 1.0);
	}

	/**
	 * Transform to a Vector-of-Strings
	 */

	public Story toStory() {
	    Story res = new Story();
		res.add("Aliveness decay per second (const): " + alivenessDecay_per_s);
		res.add("Aliveness: " + aliveness.getAverage() + ", measured at " + DateTexter.ALTERNATE0.inUTC(new Date(aliveness.getWhen())) + " UTC");
		res.add("Average workload: " + averageWorkload.getAverage() + ", measured at " + DateTexter.ALTERNATE0.inUTC(new Date(averageWorkload.getWhen())));
		res.add("Average delay in ms: " + averageDelay_ms.getAverage() + ", measured at " + DateTexter.ALTERNATE0.inUTC(new Date(averageDelay_ms.getWhen())));
		res.add("Average thread loop time in ms: " + averageThreadLoopTime_ms.getAverage() + ", measured at " + DateTexter.ALTERNATE0.inUTC(new Date(averageThreadLoopTime_ms.getWhen())));
		return res;
	}
}
