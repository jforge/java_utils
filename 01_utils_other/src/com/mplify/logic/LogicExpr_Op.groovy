package com.mplify.logic

import org.jdom.Element
import java.util.Map
import com.mplify.checkers._check
import com.mplify.enums.Troolean
import com.mplify.logging.LogFacilitiesForThrowables;
import com.mplify.logging.Story
import com.mplify.logging.storyhelpers.Dedent
import com.mplify.logging.storyhelpers.Indent
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
 * "check logic expression of OR, XOR, AND, NOT"
 *
 * The semantics are as expected...
 *
 * OR   - admits 0..N enclosed expressions. 0 expressions yields FALSE
 * AND  - admits 0..N enclosed expressions. 0 expressions yields TRUE
 * NOT  - admits exactly 1 enclosed expressions
 * XOR  - admits 2..N enclosed expressions. XOR is associative, so (a ^ b) ^ c == a ^ (b ^ c) 
 * IMPL - admits exactly 2 enclosed expressions
 * 
 * Evaluation for AND and OR can be conditional ("fast", same as using && and || in
 * Java) or complete (same as using '&' and '|' in Java). The same applies to
 * IMPL where the right-hand side may not need evaluation if the left-hand side is
 * false. 
 * 
 * Tracing:
 * ========
 * 
 * The node can be given a name that appears in tracing output using "location"
 *
 * <or attribute="A" location="here" />
 *
 * 2013.01.29 - Created based on existing Java code which used a separate
 *              datastructure for evaluation built from a DOM tree.
 *              Here, we build a JDom datastructure and use it
 *              directly when evaluating.
 * 2013.03.05 - Missing dedentation added             
 ******************************************************************************/

protected class LogicExpr_Op extends LogicExpr {

    private final static PropertyName OP_OR = new PropertyName('or')
    private final static PropertyName OP_AND = new PropertyName('and')
    private final static PropertyName OP_NOT = new PropertyName('not')
    private final static PropertyName OP_XOR = new PropertyName('xor')
    private final static PropertyName OP_IMPL = new PropertyName('impl')   

    /**
     * Closures used to evaluate "operation OR/AND/NOT/XOR/IMPL"
     */

    private static Closure compose_or = { List resultList, Troolean curResult, MutableBoolean wontChange, String path, int childSize ->
        assert resultList != null
        assert curResult != null
        assert wontChange != null 
        assert path != null
        if (resultList.isEmpty()) {
            // initialization, no child eval'ed yet
            return Troolean.FALSE
        }
        else {
            // at least 1 child (the last entry in resultList) has been evaled and we return NOT MU earlier
            assert curResult != Troolean.MU
            Troolean res = Troolean.valueOf(curResult.booleanValue() || (boolean)(resultList.last()))
            wontChange.set(res == Troolean.TRUE)
            return res
        }
    }

    private static Closure compose_and = { List resultList, Troolean curResult, MutableBoolean wontChange, String path, int childSize ->
        assert resultList != null
        assert curResult != null
        assert wontChange != null
        assert path != null
        if (resultList.isEmpty()) {
            // initialization, no child eval'ed yet
            return Troolean.TRUE
        }
        else {
            // at least 1 child (the last entry in resultList) has been evaled and we return NOT MU earlier
            assert curResult != Troolean.MU
            Troolean res = Troolean.valueOf(curResult.booleanValue() && (boolean)(resultList.last()))
            wontChange.set(res == Troolean.FALSE)
            return res
        }
    }

    private static Closure compose_xor = { List resultList, Troolean curResult, MutableBoolean wontChange, String path, int childSize ->
        assert resultList != null
        assert curResult != null
        assert wontChange != null
        assert path != null
        if (childSize < 2) {
            throw new EvalException(path, "Cannot run XOR with ${childSize} operands")
        }
        if (resultList.size() < 2) {
            return Troolean.MU 
        }
        else if (resultList.size() == 2) {
            // earliest possibility to return valid data
            return Troolean.valueOf((boolean)(resultList[0]) ^ (boolean)(resultList[1]))
        }
        else {
            // extensions; we never know about "wontChange"
            assert curResult != Troolean.MU
            return Troolean.valueOf(curResult.booleanValue() ^ (boolean)(resultList.last()))
        }
    }    

    private static Closure compose_not = { List resultList, Troolean curResult, MutableBoolean wontChange, String path, int childSize ->
        assert resultList != null
        assert curResult != null
        assert wontChange != null
        assert path != null
        if (childSize != 1) {
            throw new EvalException(path, "Cannot run NOT with ${childSize} operands")
        }
        if (resultList.isEmpty()) {
            // initialization, no child eval'ed yet
            return Troolean.MU
        }
        else if (resultList.size() == 1) {
            // unique case where valid data can be returned
            // don't care about wontChange
            return Troolean.valueOf((boolean)(resultList.last())).toNot()
        }
        else {
            throw new EvalException(path, "More than 1 operand for NOT")
        }
    }

    private static Closure compose_impl = { List resultList, Troolean curResult, MutableBoolean wontChange, String path, int childSize ->
        assert resultList != null
        assert curResult != null
        assert wontChange != null
        assert path != null
        if (childSize != 2) {
            throw new EvalException(path, "Cannot run IMPL with ${childSize} operands")
        }
        if (resultList.isEmpty()) {
            // initialization, no child eval'ed yet
            return Troolean.MU
        }
        else if (resultList.size() == 1) {
            // a result could indeed be returned, if the praemissa is FALSE
            if (!(boolean)(resultList[0])) {
                wontChange.set(true)
                return Troolean.TRUE
            }
            else {
                return Troolean.MU
            }
        }
        else if (resultList.size() == 2) {
            // used the (!A || B) expression
            return Troolean.valueOf(!(boolean)(resultList[0]) || (boolean)(resultList[1]))
        }
        else {
            throw new EvalException(path, "More than 2 operands for IMPL")
        }
    }

    /**
     * Is this about us?
     */

    boolean isAbout(Element elem) {
        assert elem != null
        return OP_OR.lenientEquals(elem.name) || OP_AND.lenientEquals(elem.name) || OP_XOR.lenientEquals(elem.name) || OP_NOT.lenientEquals(elem.name) || OP_IMPL.lenientEquals(elem.name)
    }

    /**
     * Handle
     */

    boolean handle(Element elem, String path, Map stuff, EvalTrace evalTrace) {
        assert elem != null
        assert path != null
        assert stuff != null
        assert evalTrace != null
        String elemName = PropertyName.namify(elem.name)
        boolean res
        if (OP_OR.lenientEquals(elemName)) {
            res = locHandle(compose_or, elem.getChildren(), path, stuff, evalTrace)
        }
        else if (OP_AND.lenientEquals(elemName)) {
            res = locHandle(compose_and, elem.getChildren(), path, stuff, evalTrace)
        }
        else if (OP_NOT.lenientEquals(elemName)) {
            res = locHandle(compose_not, elem.getChildren(), path, stuff, evalTrace)
        }
        else if (OP_XOR.lenientEquals(elemName)) {
            res = locHandle(compose_xor, elem.getChildren(), path, stuff, evalTrace)
        }
        else if (OP_IMPL.lenientEquals(elemName)) {
            res = locHandle(compose_impl, elem.getChildren(), path, stuff, evalTrace)
        }
        else {
            _check.cannotHappen("Unhandled case where element name is '${elemName}' -- fix code!")
        }
        return res;
    }

    /**
     * Handle
     */

    private static boolean locHandle(Closure compose, List children, String path, Map stuff, EvalTrace evalTrace) {
        assert compose != null
        assert children != null
        assert path != null
        assert stuff != null
        assert evalTrace != null
        if (evalTrace.isDebugEnabled()) {
            evalTrace.getStory().add(Indent.CI)
        }
        //
        // The "children" are evaluated and the evaluation results is added to a list of boolean 
        // The evaluation is done using "children.find()" so that an early return is possible.
        // If the closure wants to the loop to be broken off, it just needs to return true.
        //
        List resultList  = []
        MutableBoolean wontChange = new MutableBoolean(false)
        Troolean curResult = compose(resultList, Troolean.MU, wontChange, path, children.size()) // initialization
        //
        // Recursive calls, with the resulting boolean added to the "resultList".
        // Do not use a Closure as that means suppressing exceptions which we want to throw upstacks
        //
        for (Element child : children) {            
            String childPath = EvalTrace.extendPath(child, path)
            try {
                resultList << Evaluator.handle(child, childPath, stuff, evalTrace)
                //
                // Ask the operation-dependent compositing closure to compose
                // Returns: MU    in case the loop should continue
                //          TRUE  if the result will be TRUE no matter what; the loop can be broken, unless !beFast
                //          FALSE if the result will be FALSE no matter what; the loop can be broken, unless !beFast
                // Throws if there is a problem with the number of arguments so far.
                //
                curResult = compose(resultList, curResult, wontChange, path, children.size())
                assert curResult!=null
            }
            catch (EvalException exe) {
               if (evalTrace.mode == Mode.VERIFICATION)  {
                   // just register an error and continue
                   evalTrace.registerError(exe)
               }
               else {
                   assert evalTrace.mode == Mode.EVALUATION
                   throw exe
               }
            }
            //
            // Break off if the result isn't going to change AND we we are in eval mode
            //
            if (wontChange.isTrue() && (evalTrace.mode == EvalTrace.Mode.EVALUATION)) {
                break
            }
        }
        if (evalTrace.isDebugEnabled()) {
            evalTrace.getStory().add(Dedent.CI)
        }
        //
        // On return, "finalResult" must have been set, otherwise trouble!
        //
        if (evalTrace.mode == Mode.EVALUATION) {        
            if (curResult == Troolean.MU) {
                throw new EvalException(path, "Impossible to determine truth value at end of loop")
            }                    
            else {
                // 
                // Return normally with the boolean result
                //
                if (evalTrace.isDebugEnabled()) {
                    evalTrace.getStory().add(Dedent.CI)
                    evalTrace.registerDebugMsg(path, "~ ${resultList.size()} of ${children.size()} expressions evaluated (mode == '${evalTrace.mode}'): ${curResult} ")
                }
                assert curResult != null && curResult != Troolean.MU
                return curResult as Boolean // it's a troolean, so booleanize
            }
        }
        else {
            assert evalTrace.mode == Mode.VERIFICATION
            // errors may or may not have occurred
            if (curResult == Troolean.MU) {
                evalTrace.registerError("path", "Impossible to determine truth value at end of loop")
                return true                
            }
            else {
                return curResult as Boolean // it's a troolean, so booleanize
            }
        }
    }
}
