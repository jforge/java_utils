package com.mplify.junit;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers._check;
import com.mplify.dbconn.PoolFrontend;
import com.mplify.junit.AnnotationDiscoverer.AnnData;
import com.mplify.junit.annotations.DbPropertiesResourceUsingHook;
import com.mplify.junit.annotations.PropertiesResourceUsingHook;
import com.mplify.properties.PropertiesReader;
import com.mplify.properties.SingletonPropertiesReader;
import com.mplify.resources.ResourceHelpers;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * This class provides a method "setUpDbConnPool()" which is supposed to
 * be called from a "@BeforeClass" JUnit 4 method in a class holding tests.
 * 
 * The method marked @BeforeClass initializes the connection pool which 
 * provides connections to a database.
 * 
 * If the caller derives from "TestStarter", the "@BeforeClass" method of the
 * "TestStarter" is invoked first. That method sets the system property
 * "stage" to "testcase" and initializes logging. One may thus assume that to
 * be the case here.
 * 
 * 2013.01.02 - Created
 ******************************************************************************/

public final class DatabaseStarter {

    private final static String CLASS = DatabaseStarter.class.getName();
    private final static Logger LOGGER_setUpDbConnPool = LoggerFactory.getLogger(CLASS + ".setUpDbConnPool");

    /**
     * Cannot be instantiated
     */
    
    private DatabaseStarter() {
        // NOP
    }
    
    /**
     * Function will return null if no properties configured via annotation.
     * It will throw if properties not found
     */

//    @SuppressWarnings("resource")
    private static PropertiesReader readProperties(AnnData annData) throws Exception {
        assert annData != null;
        PropertiesReader properties = null;
        if (annData.getFqPropertiesResourceName() != null) {
            InputStream is = null;
            try {
                is = ResourceHelpers.getStreamFromResource(annData.getFqPropertiesResourceName().toString());
                properties = new PropertiesReader(is, null); // TODO... charset!
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        return properties;
    }

    /**
     * Function will return null if no properties configured via annotation.
     * It will throw if properties not found
     */

//    @SuppressWarnings("resource")
    private static PropertiesReader readDbProperties(AnnData annData) throws Exception {
        assert annData != null;
        PropertiesReader dbProperties = null;
        if (annData.getFqDbPropertiesResourceName() != null) {
            InputStream is = null;
            try {
                is = ResourceHelpers.getStreamFromResource(annData.getFqDbPropertiesResourceName().toString());
                dbProperties = new PropertiesReader(is, null); // TODO... charset!
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        return dbProperties;
    }

    /**
     * Called from a @BeforeClass method of class doing the tests.
     */

    public static void setUpDbConnPool(Class<?> annotatedClass, boolean throwIfAlreadyInitialized) throws Exception {
        Logger logger = LOGGER_setUpDbConnPool;
        _check.notNull(annotatedClass, "annotated class");
        //
        // Examine annotations on the passed class (generally, "AllTests")
        //
        AnnData annData = AnnotationDiscoverer.discoverAnnotations(annotatedClass);
        //
        // No exception; we are good to go
        //
        //
        // Properties, which are initialized to the empty Properties if needed and then
        // set in the singleton PropertiesReader
        //
        if (!SingletonPropertiesReader.isInitialized()) {
            PropertiesReader properties = readProperties(annData);
            if (properties == null) {
                // still null, so initialize to empty
                properties = new PropertiesReader();
            }
            else {
                logger.warn("No properties found -- you may want to configure the '" + PropertiesResourceUsingHook.class.getName() + "' annotation");
            }
            SingletonPropertiesReader.initialize(properties, throwIfAlreadyInitialized);
        }
        //
        // Database properties, which are set in the previously set properties
        //
        {
            PropertiesReader properties = SingletonPropertiesReader.getIt();
            assert properties != null;
            if (properties.getPoolFrontend() == null) {
                // not yet initialized...
                PropertiesReader dbProperties = readDbProperties(annData);            
                if (dbProperties != null) {
                    PoolFrontend theFrontend = PoolFrontend.obtainNewPoolFrontend(dbProperties); // may fail
                    SingletonPropertiesReader.getIt().setPoolFrontend(theFrontend); // <<< can be found in properties now
                    logger.info("Database connectivity initialized");
                    {
                        logger.info("Testing connection to database");
                        SingletonPropertiesReader.getPoolFrontend().obtainPooledConnection().close();
                        logger.info("Test of connection to database succeeded");
                    }
                }
                else {
                    logger.warn("No database properties found -- you may want to configure the '" + DbPropertiesResourceUsingHook.class.getName() + "' annotation");
                }
            }
            else {
                logger.warn("The database properties have already been initialized -- skipping initialization based on " + annotatedClass.getName());
            }
        }
    }
}
