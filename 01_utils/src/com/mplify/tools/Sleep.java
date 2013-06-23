package com.mplify.tools;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Sleep a bit.
 * 
 * 2010.10.26 - Created
 * 2011.02.28 - Added "sleepFor", with "sleepUntil" a special case
 ******************************************************************************/

public class Sleep {

    private final static String CLASS = Sleep.class.getName();
    private final static Logger LOGGER_sleepFor = LoggerFactory.getLogger(CLASS + ".sleepFor");

    public static void sleepShortly() {
        int sleeptime_ms = 1000;
        long now = System.currentTimeMillis();
        long stopAt = now + sleeptime_ms;
        while (now < stopAt) {
            long newSleeptime_ms = Math.max(1, stopAt - now);
            try {
                Thread.sleep(newSleeptime_ms);
            } catch (InterruptedException exe) {
                // suppress
            }
            now = System.currentTimeMillis();
        }
    }

    public static void sleepTwoSeconds() {
        int sleeptime_ms = 2000;
        long now = System.currentTimeMillis();
        long stopAt = now + sleeptime_ms;
        while (now < stopAt) {
            long newSleeptime_ms = Math.max(1, stopAt - now);
            try {
                Thread.sleep(newSleeptime_ms);
            } catch (InterruptedException exe) {
                // suppress
            }
            now = System.currentTimeMillis();
        }
    }

    public static void sleepHalfASecond() {
        int sleeptime_ms = 500;
        long now = System.currentTimeMillis();
        long stopAt = now + sleeptime_ms;
        while (now < stopAt) {
            long newSleeptime_ms = Math.max(1, stopAt - now);
            try {
                Thread.sleep(newSleeptime_ms);
            } catch (InterruptedException exe) {
                // suppress
            }
            now = System.currentTimeMillis();
        }
    }
    
    public static void sleepFiveSeconds() {
        int sleeptime_ms = 5000;
        long now = System.currentTimeMillis();
        long stopAt = now + sleeptime_ms;
        while (now < stopAt) {
            long newSleeptime_ms = Math.max(1, stopAt - now);
            try {
                Thread.sleep(newSleeptime_ms);
            } catch (InterruptedException exe) {
                // suppress
            }
            now = System.currentTimeMillis();
        }
    }

    /**
     * Sleeping until time's up. Use only in daemon threads as this sleep can't be notified or interrupted
     */

    public static void sleepUntil(Date x) {
        assert x != null;
        sleepFor(x.getTime() - System.currentTimeMillis());
    }
    
    /**
     * Sleeping until time's up. Use only in daemon threads as this sleep can't be notified or interrupted
     */

    public static void sleepFor(long x_ms) {
        boolean wasInterrupted = false;
        long start = System.currentTimeMillis();
        long leftToSleep_ms = x_ms;
        while (leftToSleep_ms > 0 && !wasInterrupted) {
            try {
                Thread.sleep(leftToSleep_ms);
            } catch (InterruptedException exe) {
                wasInterrupted = true;
                // get out soon
            }
            leftToSleep_ms = x_ms - (System.currentTimeMillis() - start);
            if (leftToSleep_ms > 0) {
                LOGGER_sleepFor.warn("Returning early from sleep; still " + leftToSleep_ms + " ms to sleep");
            }
        }
    }
}
