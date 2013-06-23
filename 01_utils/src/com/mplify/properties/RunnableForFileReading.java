package com.mplify.properties;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers.Check;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2009, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A Runnable which can re-read a file from time to time.
 * 
 * This is flaky code, but can maybe be used as the basis of something
 * 
 * 2009.11.05 - Created
 * 2011.10.20 - No longer used for Log4J, moved to core..
 ******************************************************************************/

public abstract class RunnableForFileReading implements Runnable {

    private final static String CLASS = RunnableForFileReading.class.getName();
    private final static Logger LOGGER_run = LoggerFactory.getLogger(CLASS + ".run");

    private final File file;

    private long nextRead = 0;
    private long lastModified = 0;
    private static long READ_INTERVAL_MS = 60000;

    /**
     * Constructor
     */

    public RunnableForFileReading(File file) {
        Check.notNull(file, "file");
        this.file = file;
    }

    /**
     * Run this through a daemon thread
     */

    @Override
    public void run() {
        Logger logger = LOGGER_run;
        for (;;) {
            sleepTillNextRead();
            nextRead = System.currentTimeMillis() + READ_INTERVAL_MS;
            long newLastModified = file.lastModified(); // 0L if file does not
                                                        // exist!
            if (newLastModified != 0 && lastModified < newLastModified) {
                lastModified = newLastModified;
                if (logger.isInfoEnabled()) {
                    logger.info("Re-reading '" + file.getAbsolutePath() + "' as it was modified");
                }
                read(file);
            }
        }
    }

    /**
     * Fill this in
     */

    public abstract void read(File file);

    /**
     * Sleep helper
     */

    private void sleepTillNextRead() {
        long delta_ms = nextRead - System.currentTimeMillis();
        while (delta_ms > 0) {
            try {
                Thread.sleep(delta_ms);
            } catch (Exception exe) {
                // we can ignore even InterruptedException as we are simply a
                // daemon (we hope)
            }
            delta_ms = nextRead - System.currentTimeMillis();
        }
    }
}