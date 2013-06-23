package com.mplify.logic;

import org.jdom.Element
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.jdom.Attribute
import org.jdom.Element
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.mplify.checkers._check
import com.mplify.logic.EvalTrace.Mode;
import com.mplify.mutable.MutableBoolean
import com.mplify.properties.PropertyName

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2013, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Entry point for logic evaluations
 * =================================
 * 
 * 2013.01.29 - Created to replace Java-based code for Routing, Munging and
 *              Channelling
 ******************************************************************************/

class Evaluator {

    private final static String CLASS = Evaluator.class.getName()
    private final static Logger LOGGER_handle = LoggerFactory.getLogger(CLASS + '.handle')

    /**
     * Possible logic expressions; currently just search through the list for the correct one
     * One should use a Map instead
     */

    private final static List LOGIC_EXPR_LIST = [
        new LogicExpr_Op(),
        new LogicExpr_Equals(),
        new LogicExpr_Match(),
        new LogicExpr_MemberOfIdSet(),
        new LogicExpr_MemberOfSet(),
        new LogicExpr_SetUnset(),
        new LogicExpr_Stage(),
        new LogicExpr_TruthValue()
    ]

    /**
     * Recursively evaluate a "node" in the expression tree. Throws if unknown node found 
     */

    protected static boolean handle(Element elem, String path, Map stuff, EvalTrace evalTrace) {
        Logger logger = LOGGER_handle
        assert elem != null
        assert path != null
        assert stuff  != null
        assert evalTrace  != null
        //
        // Prepare
        //
        if (evalTrace.isDebugEnabled()) {
            evalTrace.registerDebugMsg(path, "Entering")
        }
        //
        // Called "handle()" methods may throw "EvalException" (LogicExpr_Op may
        // catch and handle them in VERIFICATION mode to permit running through the tree)
        //
        LogicExpr exp = LOGIC_EXPR_LIST.find( { it.isAbout(elem) } )
        boolean res
        if (exp) {
            res = exp.handle(elem, path, stuff, evalTrace)
        }
        else {
            String msg = "Unknown element '${elem.name}' encountered; skipping it"
            if (evalTrace.mode == Mode.EVALUATION) {
                throw new EvalException(path, msg)
            }
            else {
                assert evalTrace.mode == Mode.VERIFICATION
                evalTrace.registerError(path, msg)
            }
        }
        if (evalTrace.isDebugEnabled()) {
            evalTrace.registerDebugMsg(path, "Exiting with: ${res}")
        }
        return res
    }

    /**
     * Main entry point. Evaluate the "elem", which is a "condition" element of not-further-specified name.
     * If "stuff" is non-null, it is used in evaluation of leaf nodes, otherwise an empty map is assumed.
     * "evalTrace" is used both to control behaviour (whether evaluation of AND and OR chains is complete or
     * breaks off ASAP) as well as used in collection of runtime errors.
     * If "parentPath" is not null, it is used as a prefix for location in the JDom tree, in error messages
     * and the like.
     * In "evaluation mode" as given by "eval trace", this method may throw "EvalException" if
     * something is fishy with the expression.
     * In "verification mode" as given by "eval trace", this method may set the error flag in the "eval trace"
     * The caller should catch and/or check that.
     */

    static boolean eval(Element elem, String parentPath, Map stuff, EvalTrace evalTrace) {
        _check.notNull(elem,'element')
        _check.notNull(evalTrace,'eval trace')
        Map passedStuff = (stuff==null ? [:] : stuff)
        String path = EvalTrace.extendPath(elem, (parentPath != null ? parentPath : ''))
        try {
            return handle(elem,path,passedStuff,evalTrace)
        }
        catch (EvalException exe) {
            if (evalTrace.mode == Mode.VERIFICATION)  {
                // just register and return something
                evalTrace.registerError(exe)
                return false
            }
            else {
                assert evalTrace.mode == Mode.EVALUATION
                throw exe
            }
        }
    }

}
