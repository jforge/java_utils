package com.mplify.junit;

import com.mplify.logging.LogFacilitiesForThrowables
import com.mplify.logging.Story
import com.mplify.store.PersistentStore
import com.mplify.store.PersistentStore.LifecycleOp
import org.slf4j.Logger
import org.slf4j.LoggerFactory;

public class ClosureBuilder {

    private final static String CLASS = ClosureBuilder.class.getName()
    private final static Logger LOGGER_clearGenericFK = LoggerFactory.getLogger(CLASS + ".clearGenericFK")
    
    /**
     * Method that takes a Closure and returns a new Closure in which
     * - the passed closure is called FIRST
     * - the tables backing the "store" are created if they don't exist
     * - upon return, the tables backing the "store" are dropped again 
     */

    static Closure buildGeneric(Closure cl, PersistentStore store) {
        return {
            try {                          
                store.lifecycleTable(LifecycleOp.DROP_THEN_CREATE)
                cl()
            }
            finally {
                store.lifecycleTable(LifecycleOp.DROP)
            }
        }
    }
        
    /**
     * Method that takes a Closure and returns a new Closure in which
     * - the tables backing the "store" are created if they don't exist
     * - the passed closure is called
     * - upon return, the tables backing the "store" are dropped again
     */

    static Closure buildGeneric(Closure cl, PersistentStore store, boolean dropTablesAtEnd) {
        return {
            try {
                store.lifecycleTable(LifecycleOp.DROP_THEN_CREATE)
                cl()
            }
            finally {
                if (dropTablesAtEnd) {
                    store.lifecycleTable(LifecycleOp.DROP)
                }
            }
        }
    }

    /**
     * Method that takes a Closure and returns a new Closure in which
     * - the passed closure is called FIRST 
     * - the foreign keys set on the "store" are created
     * - upon return, the foreign keys set on the "store" are dropped again
     */

    static Closure buildGenericFK(Closure cl, PersistentStore store) {
        return {
            try {                
                store.lifecycleTable(LifecycleOp.ADD_FK_CONSTRAINT)
                cl()
            }
            finally {
                store.lifecycleTable(LifecycleOp.DROP_FK_CONSTRAINT)
            }
        }
    }
    
    /**
     * Method that takes a Closure and returns a new Closure in which
     * - the passed closure is called FIRST
     * - the foreign keys set on the "store" are created
     * - upon return, the foreign keys set on the "store" are dropped again
     */

    static Closure buildGenericFK(Closure cl, PersistentStore store, Class selector) {
        return {
            try {
                store.lifecycleTable(LifecycleOp.ADD_FK_CONSTRAINT, selector)
                cl()
            }
            finally {
                store.lifecycleTable(LifecycleOp.DROP_FK_CONSTRAINT, selector)
            }
        }
    }

    /**
     * Method that takes a Closure and returns a new Closure in which
     * - the passed closure is called FIRST
     * - the foreign keys set on the "store" are created
     * - upon return, the foreign keys set on the "store" are dropped again
     */

    static Closure clearGenericFK(Closure cl, PersistentStore store, Class selector) {
        return {
            try {
                store.lifecycleTable(LifecycleOp.DROP_FK_CONSTRAINT, selector)
            }
            catch (Exception exe) {
                Story story = LogFacilitiesForThrowables.throwableToSimpleMultilineStory("While dropping constraint", exe)
                LOGGER_clearGenericFK.info(story.toString())
            }
            cl()
        }
    }

    /**
     * Method that takes a Closure and returns a new Closure in which
     * - the passed closure is called FIRST
     * - the foreign keys set on the "store" are created
     * - upon return, the foreign keys set on the "store" are dropped again
     */

    static Closure clearGenericFK(Closure cl, PersistentStore store) {
        return {
            try {
                store.lifecycleTable(LifecycleOp.DROP_FK_CONSTRAINT)
            }
            catch (Exception exe) {
                Story story = LogFacilitiesForThrowables.throwableToSimpleMultilineStory("While dropping constraint", exe)
                LOGGER_clearGenericFK.info(story.toString())
            }
            cl()
        }
    }

}
