package com.mplify.logic;

import org.jdom.Attribute
import org.jdom.Element

import com.mplify.checkers._check
import com.mplify.enums.Troolean
import com.mplify.id.AbstractId
import com.mplify.logic.LogicExpr.ExtractionBehaviour
import com.mplify.logic.LogicExpr.ExtractionResult
import com.mplify.properties.PropertyName
import com.mplify.utils.BooleanParser


/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2013, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * "equality between strings"
 * ==========================
 * 
 * The expression is written down using XML as:
 * 
 * <equals attribute="A" value="Y" />
 *
 * It evaluates to true if the Map used at evaluation contains an entry
 * named "A", that, if transformed into a String using ".toString()", yields
 * exactly the string "Y".
 *             
 * A "mode" attribute, which can be "trim", "lowercase", 
 * "namify" (i.e. trim & lowercase) or "nop" can be added to modify the values
 * prior to comparison. If "mod" is not given "nop" is assumed:
 * 
 * <equals attribute="X" value="Y" mode="Z" />
 *
 * Note that the value may not be missing in the passed "stuff" map.
 * 
 * TODO: Add a flag allowing it to be missing.
 * 
 * Tracing:
 * ========
 * 
 * The node can be given a name that appears in tracing output using "location"
 * 
 * <equals attribute="A" value="Y" location="here" />
 *
 * 2013.01.29 - Created based on existing Java code which used a separate
 *              datastructure for evaluation built from a DOM tree.
 *              Here, we build a JDom datastructure and use it 
 *              directly when evaluating.
 ******************************************************************************/

protected class LogicExpr_Equals extends LogicExpr {

    private final static PropertyName NAME_EQUALS = new PropertyName('equals')

    /**
     * Is this about us?
     */

    boolean isAbout(Element elem) {
        assert elem != null
        return NAME_EQUALS.lenientEquals(elem.name)
    }

    /**
     * Evaluate
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
        //
        // The value from the "stuff map" yields the first string that goes into a comparison
        //
        String leftString
        if (stuffMapValue instanceof AbstractId) {
            leftString = ((AbstractId)stuffMapValue).value as String
        }
        else {
            leftString = (stuffMapValue as String) // may yield null!
        }
        //
        // The "value" stored in the element yields the second string that goes into a comparison
        //
        String rightString = getValue(elem, path, evalTrace)
        assert rightString != null
        //
        // Canonicize "rightString" depending on the type of "datum" for a good comparison
        //
        if (stuffMapValue instanceof Boolean) {
            Boolean x = BooleanParser.parse(rightString)
            if (x != null) {
                rightString = (x as String)
            }
        }
        else if (stuffMapValue instanceof Troolean) {
            Troolean x = Troolean.obtain(rightString, null, false)
            if (x != null) {
                rightString = (x as String)
            }
        }
        //
        // Get the preprocessing mode, if it exists
        //
        PreproMode preproMode = obtainPreproMode(elem, PREPRO_MODE, path, evalTrace)
        assert preproMode != null
        //
        // Modify both strings according to "mode"
        //
        String leftStringMod = leftString
        String rightStringMod = rightString
        if (leftString != null) {
            switch (preproMode) {
                case PreproMode.TRIM:
                    leftStringMod = leftString.trim()
                    rightStringMod = rightString.trim()
                    break
                case PreproMode.NAMIFY:
                    leftStringMod = PropertyName.namify(leftString)
                    rightStringMod = PropertyName.namify(rightString)
                    break
                case PreproMode.LOWERCASE:
                    leftStringMod = leftString.toLowerCase()
                    rightStringMod = rightString.toLowerCase()
                    break
                case PreproMode.NOP:
                    break
                default:
                    _check.cannotHappen("Unhandled preprocessing mode '${preproMode}' -- fix code")
            }
        }
        //
        // Compute
        //
        boolean res = (leftStringMod == rightStringMod)
        if (evalTrace.isDebugEnabled()) {
            evalTrace.registerDebugMsg(path, "~ Value of key '${stuffMapKey}' in 'map of stuff' is '${leftString}' -- equality with '${rightString}' (preprocessing mode '${preproMode}'): ${res}")
        }
        return res
    }

}
