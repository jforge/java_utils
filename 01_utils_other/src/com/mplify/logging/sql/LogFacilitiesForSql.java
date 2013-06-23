package com.mplify.logging.sql;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

import org.slf4j.Logger;

import com.mplify.logging.LoglevelAid;
import com.mplify.logging.LoglevelAid.Loglevel;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Odds and sods used in logging. See also: 'StoryHandling'
 *
 * 2005.11.28 - Created
 * 2011.10.19 - Review for LOG4J --> SLF4J adaptation
 ******************************************************************************/

public class LogFacilitiesForSql {

    /*
     * Print Drivers known to the DriverManager currently accessible
     */

    public static void printKnownDrivers(Logger logger, Loglevel level) {
        // if (level.isGreaterOrEqual(logger.getEffectiveLevel())) {
        if (LoglevelAid.isEnabledFor(logger, level)) {
            StringBuilder buf = new StringBuilder();
            buf.insert(0, " +-----------------------------------------\n");
            Enumeration<Driver> iter = DriverManager.getDrivers();
            while (iter.hasMoreElements()) {
                Driver driver = iter.nextElement();
                buf.append(" | ");
                buf.append(driver.getClass().getName());
                buf.append(" version ");
                buf.append(driver.getMajorVersion());
                buf.append(".");
                buf.append(driver.getMinorVersion());
                buf.append(" at ");
                buf.append(driver.hashCode());
                buf.append("\n");
            }
            buf.append(" +-----------------------------------------\n");
            LoglevelAid.log(logger, level, buf.toString());
        }
    }
}
