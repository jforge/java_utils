package com.mplify.logic;

import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

import org.jdom.Attribute
import org.jdom.Element

import com.mplify.checkers._check
import com.mplify.logic.LogicExpr.ExtractionBehaviour;
import com.mplify.logic.LogicExpr.ExtractionResult
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
 * "(Java) regexp matching"
 * ========================
 * 
 * The expression is written down using XML as:
 *
 * <match attribute="A" pattern="P" matchmode="F" prepromode="X"/>
 *
 * It evaluates to true if the Map used at evaluation contains an entry
 * named "A", that, if transformed into a String using ".toString()", yields
 * something that can be matched with a matcher created based on the Java regex
 * "P". The matching is done using "find()" by default, i.e. the match can start
 * at any position in the input.
 * 
 * The attribute "matchmode" can take on the values "find" and "match". Setting to 
 * "find" means default behaviour, but setting to "match" means the pattern must 
 * match the whole input. 
 * 
 * The attribute "prepromode" can be used to apply an operation to the string to be
 * matched before matching. The allowed values are: "trim", "lowercase", "namify" 
 * (lowercase and trim) and "nop". The default is "nop".
 *
 * Tracing:
 * ========
 * 
 * The node can be given a name that appears in tracing output using "location"
 * 
 * <match attribute="A" pattern="P" mode="F" pre="X" location="here" />
 *
 * 2013.01.29 - Created based on existing Java code which used a separate
 *              datastructure for evaluation built from a DOM tree.
 *              Here, we build a JDom datastructure and use it
 *              directly when evaluating.
 ******************************************************************************/

protected class LogicExpr_Match extends LogicExpr {

    private final static PropertyName NAME_MATCH = new PropertyName('match')
    private final static PropertyName PATTERN = new PropertyName('pattern')
    private final static PropertyName MATCH_MODE = new PropertyName('matchmode')
        
    /**
     * Is this about us?
     */

    boolean isAbout(Element elem) {        
        assert elem != null
        return NAME_MATCH.lenientEquals(elem.name)
    }
    
    /**
     * Compile a regex stored under the attribute PATTERN in the "elem" and return the Pattern
     * TODO: The compiled Pattern should be stored in a cache.
     */

    private static List compilePattern(Element elem, String path, EvalTrace evalTrace) {
        assert elem!=null && path!=null && evalTrace != null
        Attribute att = elem.getAttribute(PATTERN as String)        
        if (!att) {
            throw new EvalException(path, "There is no attribute '${PATTERN}'")
        }
        Pattern pattern
        try {
            pattern = Pattern.compile(att.value)
        } catch (PatternSyntaxException exe) {
            throw new EvalException(path, "Cannot compile value '${att.value}' given by attribute '${PATTERN}' into a ${Pattern.class.getName()}")
        }
        assert pattern != null
        return [ pattern, att.value ]
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
        // Consult the "stuff" map
        //
        ExtractionResult exres = extractFromStuffMap(elem, path, stuff, evalTrace, ExtractionBehaviour.MUST_EXIST_AND_NOT_BE_NULL)
        assert exres.stuffMapKey != null
        assert exres.stuffMapValue != null
        assert !exres.noSuchEntry
        String stuffMapKey = exres.stuffMapKey
        def stuffMapValue = exres.stuffMapValue
        String inputStr = stuffMapValue as String // may yield null               
        //
        // Get and compile the pattern; TODO pattern compilation is done during eval of an expression;
        // one might consider adding a cache of patterns
        //
        Pattern pattern
        String patternAsStr
        (pattern , patternAsStr) = compilePattern(elem, path, evalTrace)
        assert pattern != null
        assert patternAsStr != null
        //
        // Get and apply the preprocessing mode
        //
        PreproMode preproMode = obtainPreproMode(elem, PREPRO_MODE, path, evalTrace)
        assert preproMode != null
        String inputStrMod
        if (inputStr != null) {            
            switch (preproMode) {
                case PreproMode.TRIM:
                    inputStrMod = inputStr.trim()
                    break
                case PreproMode.NAMIFY:
                    inputStrMod = PropertyName.namify(inputStr)
                    break
                case PreproMode.LOWERCASE:
                    inputStrMod = inputStr.toLowerCase()
                    break
                case PreproMode.NOP:
                    inputStrMod = inputStr
                    break
                default:
                    _check.cannotHappen("Unhandled pre '${preproMode}' -- fix code")
            }
        }
        //
        // Get the match mode
        //
        MatchMode matchMode = obtainMatchMode(elem, MATCH_MODE, path, evalTrace)
        assert matchMode != null
        //
        // Match using pattern
        //
        boolean res = false           
        switch (matchMode) {
            case MatchMode.FIND:
                res = pattern.matcher(inputStrMod).find()
                break
            case MatchMode.MATCH:
                res = pattern.matcher(inputStrMod).matches()
                break
        }
        if (evalTrace.isDebugEnabled()) {
            evalTrace.registerDebugMsg(path, "~ Value of key '${stuffMapKey}' in 'map of stuff' yields '${inputStr}' -- matching against '${patternAsStr}' (match mode = '${matchMode}' and preprocessing mode = '${preproMode}'): ${res}")
        }
        return res
    }

}
 