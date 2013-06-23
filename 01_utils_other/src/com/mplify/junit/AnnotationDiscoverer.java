package com.mplify.junit;

import java.lang.annotation.Annotation;


//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import com.mplify.checkers._check;
import com.mplify.junit.annotations.DbPropertiesResourceUsingHook;
import com.mplify.junit.annotations.ParticularTest;
import com.mplify.junit.annotations.PropertiesResourceUsingHook;
import com.mplify.names.TrimmedNonemptyString;
import com.mplify.names.TrimmedString;
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
 * Handling of annotations used in test cases.
 * 
 * Package visibility should suffice.
 * 
 * 2010.10.09 - Code moved out from HelperForTestCases
 * 2011.10.18 - Added handling of the "SuiteClasses" annotation
 * 2013.01.02 - Review, unudes code removed (the one handling the array of
 *              TestCase classes, which seems only relevant to JUnit 3)
 ******************************************************************************/

class AnnotationDiscoverer {

//    private final static String CLASS = AnnotationDiscoverer.class.getName();
//    private final static Logger LOGGER_discoverAnnotations = LoggerFactory.getLogger(CLASS + ".discoverAnnotations");

    /**
     * You cannot instantiate this
     */

    private AnnotationDiscoverer() {
        // NOP
    }

    /**
     * The result
     */

    public static class AnnData {

        private TrimmedNonemptyString particularTest; // may be null; not null iff particular test demanded 
        private TrimmedNonemptyString fqPropertiesResourceName; // may be null; not null iff particular properties demanded
        private TrimmedNonemptyString fqDbPropertiesResourceName; // may be null; not null iff particular properties demanded
        
        public TrimmedNonemptyString getParticularTest() {
            return particularTest;
        }
        
        public void setParticularTest(String x) {
            this.particularTest = new TrimmedNonemptyString(x);
        }
        
        public TrimmedNonemptyString getFqPropertiesResourceName() {
            return fqPropertiesResourceName;
        }
        
        public void setFqPropertiesResourceName(String x) {
            this.fqPropertiesResourceName = new TrimmedNonemptyString(x);
        }
        
        public TrimmedNonemptyString getFqDbPropertiesResourceName() {
            return fqDbPropertiesResourceName;
        }
        
        public void setFqDbPropertiesResourceName(String x) {
            this.fqDbPropertiesResourceName = new TrimmedNonemptyString(x);
        }
        
    }

    /**
     * A "ParticularTest" annotation needs to give a test name (the method name)
     */
    
    private static void handleParticularTest(ParticularTest ann, AnnData res) {
        assert ann!=null;
        assert res!=null;
        assert ann.name() != null : "The 'name' of the " + ParticularTest.class.getName() + " is never null";
        TrimmedString name = new TrimmedString(ann.name());
        if (!name.isEmpty() && !name.equals("*")) { 
            res.setParticularTest(name.toString());
        }
    }

    /**
     * A "PropertiesResourceUsingHook" annotation indicates in what package a properties file can be found
     */

    private static void handlePropertiesResourceUsingHook(PropertiesResourceUsingHook ann, AnnData res) {
        assert ann!=null;
        assert res!=null;
        assert ann.hookClass() != null : "The 'hook class' of the " + PropertiesResourceUsingHook.class.getName() + " is never null";
        assert ann.name() != null : "The 'name' of the " + PropertiesResourceUsingHook.class.getName() + " is never null";
        TrimmedString name = new TrimmedString(ann.name());
        if (!name.isEmpty()) {                
            String fqName = ResourceHelpers.fullyQualifyResourceName(ann.hookClass(), name.toString());
            if (!ResourceHelpers.existsResource(fqName)) {
                _check.fail("The resource '" + fqName + "' specified through annotation " + PropertiesResourceUsingHook.class.getName() + " does not exist");
            }
            res.setFqPropertiesResourceName(fqName);
        }
    }

    /**
     * A "DbPropertiesResourceUsingHook" annotation indicates in what package a properties file indicating a database resource can be found
     */

    private static void handleDbPropertiesResourceUsingHook(DbPropertiesResourceUsingHook ann, AnnData res) {
        assert ann!=null;
        assert res!=null;
        assert ann.hookClass() != null;
        assert ann.name() != null;
        assert ann.hookClass() != null : "The 'hook class' of the " + DbPropertiesResourceUsingHook.class.getName() + " is never null";
        assert ann.name() != null : "The 'name' of the " + DbPropertiesResourceUsingHook.class.getName() + " is never null";
        TrimmedString name = new TrimmedString(ann.name());
        if (!name.isEmpty()) {
            String fqName = ResourceHelpers.fullyQualifyResourceName(ann.hookClass(), name.toString());
            if (!ResourceHelpers.existsResource(fqName)) {
                _check.fail("The resource '" + fqName + "' specified through annotation " + DbPropertiesResourceUsingHook.class.getName() + " does not exist");
            }
            res.setFqDbPropertiesResourceName(fqName);
        }
    }

    /**
     * Pass the class that holds the tests; annotations will be extracted at the class level.
     */

    public static AnnData discoverAnnotations(Class<?> clazz) {
//        Logger logger = LOGGER_discoverAnnotations;
        _check.notNull(clazz,"class");
        AnnData res = new AnnData();        
        Annotation[] annArray = clazz.getAnnotations();
        assert annArray != null : "Java always returns at least an empty array of annotations";
        //
        // Handle each annotation in turn; this will throw on problem
        //
        for (Annotation ann : annArray) {            
            if (ann instanceof ParticularTest) {
                handleParticularTest((ParticularTest) ann, res);
            }            
            if (ann instanceof PropertiesResourceUsingHook) {
                handlePropertiesResourceUsingHook((PropertiesResourceUsingHook) ann, res);
            }            
            if (ann instanceof DbPropertiesResourceUsingHook) {               
                handleDbPropertiesResourceUsingHook((DbPropertiesResourceUsingHook) ann, res);                
            }
        }
        //
        // All annotations handled
        //
        return res;
    }
}
