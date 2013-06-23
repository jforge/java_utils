package com.mplify.logic;

import java.util.concurrent.atomic.AtomicInteger

import org.jdom.Attribute
import org.jdom.Element;

import com.mplify.checkers._check
import com.mplify.logging.Story
import com.mplify.logging.LoglevelAid.Loglevel
import com.mplify.logging.ThrowableEntryChain;
import com.mplify.properties.PropertyName;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2013, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Determine how evaluation should work and where errors and warnings are
 * collected.
 *
 * The "mode" is either 
 * 
 * "VERIFICATION" - the whole tree is traversed, errors are not fatal (though
 * they are flagged), commentary accumulates in "trace"
 * 
 * "EVALUATION"   - the tree is only traversed as needed, errors may be fatal
 * (in the sense that exceptions may be thrown)
 * 
 * 2013.01.31 - Created
 ******************************************************************************/

class EvalTrace {

    /**
     * The "location" attribute of an element can be woven into a path 
     */

    private final static PropertyName LOCATION = new PropertyName('location')

    /**
     * Current "Mode" for expression evaluation
     * EVALUATION   - Actually evaluate the expression; problems lead to exceptions 
     * VERIFICATION - Just verify as much of an expression as possible; problems lead to logging
     */

    enum Mode {
        EVALUATION, VERIFICATION
    }

    /**
     * Non-mutables
     */
    
    private final Mode mode // non-null mode
    private final Loglevel loglevel // non-null loglevel, used to decide whether to store INFO and DEBUG msgs
    
    /**
     * Mutables
     */
    
    private int errorCount = 0 // number of errors encountered so far
    private int warningCount = 0  // number of warnings encountered so far    
    private final Story story = new Story() // accumulate the "story" of the evaluation
    private final Set stuffMapKeys = new HashSet() // accumulate stuff map keys found (they are all PropertyNames) 
    
    /**
     * Construct from non-null "mode" and non-null "loglevel"
     */

    EvalTrace(Mode mode, Loglevel loglevel) {
        _check.notNull(loglevel, 'loglevel')
        _check.notNull(mode, 'mode')
        this.loglevel = loglevel
        this.mode = mode
    }

    /**
     * What's the non-null mode?
     */

    public Mode getMode() {
        return mode
    }

    /**
     * Is loglevel at debug?
     */

    boolean isDebugEnabled() {
        return loglevel <= Loglevel.DEBUG 
    }

    /**
     * Is loglevel 
     */

    boolean isInfoEnabled() {
        return loglevel <= Loglevel.INFO  
    }

    /**
     * Number of errors
     */
    
    int getErrorCount() {
        return errorCount
    }

    /**
     * Number of warnings
     */
    
    int getWarningCount() {
        return warningCount
    }

    /**
     * Errors detected?
     */

    boolean isErrorsOccurred() {
        return errorCount.value > 0
    }

    /**
     * Warnings detected?
     */

    boolean isWarningsOccurred() {
        return warningCount.value > 0
    }

    /**
     * Get the appropriate loglevel with which to log the story;
     * it depends on whether errors or warnings occurred; otherwise
     * it depends on the loglevel passed at construction time.
     */
    
    Loglevel getAppropriateLoglevel() {
        if (errorsOccurred) {
            return Loglevel.ERROR
        }    
        else if (warningsOccurred) {
            return Loglevel.WARN
        }
        else { 
            return loglevel
        }
    }
    
    /**
     * Add stuff to "story", increment error counter
     */

    void registerError(String path, String msg) {
        errorCount++
        story.add("At '${path}': ERROR -- ${msg}")
    }

    /**
     * Add stuff to "story" based on an EvalException, increment error counter
     */

    void registerError(EvalException exe) {
        _check.notNull(exe, "exception")
        errorCount++
        story.add("At '${exe.path}': ERROR -- ${exe.message}")
    }

    /**
     * Add stuff to "story", increment warnings counter
     */

    void registerWarning(String path, String msg) {
        warningCount++
        story.add("At '${path}': WARNING -- ${msg}")
    }

    /**
     * Add stuff to "story", but only if log level accepts debug messages
     */

    void registerDebugMsg(String path, String msg) {
        if (isDebugEnabled()) {
            story.add("At '${path}': ${msg}")
        }
    }

    /**
     * Add stuff to "story", but only if log level accepts info messages
     */

    void registerInfoMsg(String path, String msg) {
        if (isInfoEnabled()) {
            story.add("At '${path}': ${msg}")
        }
    }

    /**
     * Access the "story"; in principle it can be manipulated by the caller
     */
    
    Story getStory() {
        return story
    }
    
    /**
     * Add another name-of-an-attribute (of type PropertyName) 
     */
    
    boolean addStuffMapKey(PropertyName key) {
        _check.notNull(key,"key")
        return stuffMapKeys.add(key)
    }
    
    /**
     * Get the set of PropertyName; the returned set is immutable
     */
    
    Set getStuffMapKeys() {
        return Collections.unmodifiableSet(stuffMapKeys)
    }
    
    /**
     * Helper to build debugging string. Collects the name and, if it exists, the "location" attribute of
     * the passed "elem" and appends it to "pathOfParent", which is then returned.
     */

    static String extendPath(Element elem, String pathOfParent) {
        _check.notNull(elem, "elem")
        _check.notNull(pathOfParent, "pathOfParent")
        Attribute att = elem.getAttribute(LOCATION as String)
        if (att != null) {
            return "${pathOfParent}/${elem.name}(${att.value})"
        }
        else {
            return "${pathOfParent}/${elem.name}"
        }
    }
    
}