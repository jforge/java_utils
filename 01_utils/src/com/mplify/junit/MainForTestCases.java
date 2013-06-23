package com.mplify.junit;

import java.text.DecimalFormat;
import java.util.Enumeration;

import junit.framework.TestFailure;
import junit.framework.TestResult;

import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Code called from TestCases' main()
 * 
 * 2010.10.09 - Code moved out from HelperForTestCases
 * 2010.10.13 - New annotation specially made for database properties
 * 2011.12.07 - Added possibility to pass --file=... or --resource=...
 *              which is useful if the test case is actually for migration.
 * 2013.01.01 - Removed initialization of logger and stage              
 ******************************************************************************/

public class MainForTestCases {

//    private final static String CLASS = MainForTestCases.class.getName();
//    private final static Logger LOGGER_main = LoggerFactory.getLogger(CLASS + "._main");

    /**
     * Process a unit testing result by writing to log4j (i.e. the passed 'loger') This is called at the end of a 'main'
     * class that does a JUnit run.
     * Called as:
     * Run tests and process results
     * 
     * {
     *     TestResult result = new TestResult();
     *     suite.run(result);
     *     processTestResult(result, logger);
     * }
     */

    private static void processTestResult(TestResult result, Logger logger) {
        logger.info("+---------------------------------------------------------------------------");
        //
        // FAILURES are unfulfilled tested-for conditions
        //
        if (result.failureCount() > 0) {
            if (result.failureCount() == 1) {
                logger.error("| There was 1 failure (i.e. an unfulfilled tested-for condition)");
            } else {
                logger.error("| There were " + result.failureCount() + " failures (i.e. unfulfilled tested-for conditions)");
            }
            Enumeration<TestFailure> iter = result.failures();
            while (iter.hasMoreElements()) {
                TestFailure tf = iter.nextElement();
                logger.info("| ..." + tf.toString());
            }
        }
        //
        // ERRORS are unanticipated problems during a test
        //
        if (result.errorCount() > 0) {
            if (result.errorCount() == 1) {
                logger.error("| There was 1 error (i.e. an unanticipated problem)");
            } else {
                logger.error("| There were " + result.errorCount() + " errors (i.e. unanticipated problems)");
            }
            Enumeration<TestFailure> iter = result.errors();
            while (iter.hasMoreElements()) {
                TestFailure tf = iter.nextElement();
                if (tf.toString() != null) {
                    logger.info("| ...exception message: " + tf.exceptionMessage());
                } else {
                    logger.info("| ...no exception message was given");
                }
                logger.info("| ...stack trace", tf.thrownException());
            }
        }
        //
        // the résumé
        //
        logger.info("+------8<-------------8<----------------8<------------------8<--------------");
        logger.info("| " + result.runCount() + " tests were run");
        DecimalFormat df = new DecimalFormat("0.00%");
        if (result.runCount() > 0) {
            logger.info("| Relative number of failures: " + df.format((double) result.failureCount() / (double) result.runCount()));
            logger.info("| Relative number of errors  : " + df.format((double) result.errorCount() / (double) result.runCount()));
            if (result.wasSuccessful()) {
                // Only the case if the test had no errors and no failures
                logger.info("| **** The test was successful ****");
            } else {
                logger.info("| **** The test failed ****");
            }
        } else {
            logger.info("| **** No tests were run ****");
        }
        logger.info("+---------------------------------------------------------------------------");
    }

}
