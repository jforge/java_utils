package com.mplify.junit;

import org.junit.BeforeClass;

import com.mplify.logstarter.LogbackStarter;
import com.mplify.sysprop.SystemProperties;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * This class provides a "@BeforeClass" JUnit 4 method that is invoked before
 * the Test Class is instantiated and its tests are run.
 * 
 * In this case, the method marked @BeforeClass initializes SLF4J with Logback.
 * and sets the "stage" system property to "TESTCASE".
 * 
 * All Test Classes should inherit from this one in order to make sure that the
 * "stage" system property is set and that SLF4J with Logback is properly 
 * initialized before the tests are run by the JUnit Runner.
 * 
 * 2013.01.01 - Created
 *******************************************************************************/

public abstract class TestStarter {

    @BeforeClass
    public static void setUpLogging() {
        //
        // Set the "stage" system property
        //
        System.setProperty(SystemProperties.PROP_STAGE, SystemProperties.STAGE_TESTCASE);
        //
        // Create a LogbackStarter, which will call its static initialization
        // and set everything up; the instance is useless after that
        //
        LogbackStarter dummy = new LogbackStarter();
    }
    
}
