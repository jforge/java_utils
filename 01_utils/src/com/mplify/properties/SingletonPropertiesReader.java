package com.mplify.properties;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers.Check;
import com.mplify.logging.DateTexter;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Something to hold a globally (or at least classloader-wide) accessible 
 * PropertiesReader instance. 
 * 
 * 2005.08.23 - Created
 *******************************************************************************/

public class SingletonPropertiesReader {

    private final static String CLASS = SingletonPropertiesReader.class.getName();
    private final static Logger LOGGER_initialize = LoggerFactory.getLogger(CLASS + ".initialize");
    
    private static PropertiesReader properties;
    private static Date whenInitialized;
    private static String alreadyMsg;

    /**
     * Obtain the underlying 'PropertiesReader'. Note that if properties could not be
     * loaded, it will be empty; it will not be (null).
     */

    public static PropertiesReader getIt() {
        if (properties == null) {
            Check.fail("Before calling this method, you have to initialize " + CLASS);
        }
        return properties;
    }
    
    /**
     * Sugar to access the PoolFrontend directly
     */
    
    /*
    public static PoolFrontend getPoolFrontend() {
        return getIt().getPoolFrontend();
    }
    */

    /**
     * Set the underlying 'PropertiesReader'. This can only be called once
     */

    public static synchronized void initialize(PropertiesReader propertiesIn, boolean throwIfAlreadyInitialized) {
        Check.notNull(propertiesIn, "properties");
        if (isInitialized()) {
            if (throwIfAlreadyInitialized) {
                Check.fail(alreadyMsg);
            }
            else {
                LOGGER_initialize.warn(alreadyMsg);
            }
        }
        else {
            properties = propertiesIn;
            whenInitialized = new Date();
            alreadyMsg = "Initialization was already done at " + DateTexter.EXTENDED.inUTC(whenInitialized) + " UTC";
        }
    }
    
    /**
     * Check
     */
    
    public static boolean isInitialized() {
        return properties != null;
    }
        
}
