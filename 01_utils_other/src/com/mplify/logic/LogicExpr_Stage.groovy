package com.mplify.logic;

import java.util.Map;

import org.jdom.Attribute
import org.jdom.Element

import com.mplify.checkers._check
import com.mplify.listparsing.Parsing
import com.mplify.logging.Story
import com.mplify.mutable.MutableBoolean;
import com.mplify.properties.PropertyName
import com.mplify.sysprop.SystemProperties

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2013, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Check whether the system is in some "stage"
 * ===========================================
 *
 * Expression as written in XML
 *
 * <stage value="prod, test, foo, test, testcase" />
 * 
 * The "stage" expression evaluates to true if the "stage" system property 
 * matches one of the (comma-separated) strings in the "value". Comparison
 * is done after trimming and lowercasing. 
 *
 * Tracing:
 * ========
 *
 * The node can be given a name that appears in tracing output using "location"
 *
 * <stage value="prod" location="here"/>
 *
 * 2013.01.29 - Created based on existing Java code which used a separate
 *              datastructure for evaluation built from a DOM tree.
 *              Here, we build a JDom datastructure and use it
 *              directly when evaluating.
 ******************************************************************************/

protected class LogicExpr_Stage extends LogicExpr {

    private final static PropertyName NAME_STAGE = new PropertyName('stage')

    private final static String currentStage = getCurrentStage()

    /**
     * Get the current "stage". Note that this value does not change at runtime, 
     * so one should obtain it once only
     */

    private static String getCurrentStage() {
        boolean throwIfNotFound, namify
        String stage = SystemProperties.getStage(throwIfNotFound=false, namify=true)
        return stage == null ? '' : stage
    }

    /**
     * Get the stages listed in the XML
     */

    private static List getStages(Element elem, String path, EvalTrace evalTrace) {
        String str = getValue(elem, path, evalTrace)
        List l = Parsing.parseCommaSeparatedAtoms(str, null, null, false);
        assert l != null;
        return l.collect( {
            PropertyName.namify(it)
        })
    }

    /**
     * Is this about us?
     */

    boolean isAbout(Element elem) {
        assert elem != null
        return NAME_STAGE.lenientEquals(elem.name)
    }

    /**
     * Handle
     */

    boolean handle(Element elem, String path, Map stuff, EvalTrace evalTrace) {
        assert elem != null
        assert path != null
        assert stuff != null
        assert evalTrace != null
        //
        // Determine the stages listed in "elem"
        //
        List listedStages = getStages(elem, path, evalTrace) // TODO: This should be buffered to avoid repeats
        assert listedStages != null
        //
        // Check
        //
        boolean res = listedStages.contains(currentStage)
        if (evalTrace.isDebugEnabled()) {
            String rejoined = listedStages.join(',') // TODO: Buffer this!
            evalTrace.registerDebugMsg(path, "~ Checking whether the current stage '${currentStage}' is in set {${rejoined}}: ${res}")
        }
        return res
    }

}
