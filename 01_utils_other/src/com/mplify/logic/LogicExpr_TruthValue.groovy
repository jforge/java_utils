package com.mplify.logic

import org.jdom.Attribute
import org.jdom.Element

import com.mplify.enums.Troolean
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
 * Constant truth values or truth values obtained from the map of stuff
 * ====================================================================
 *
 * Expression as written in XML
 *
 * <true/>    - always yields true  (good for testing)                
 * <false/>   - always yields false (good for testing)
 * 
 * <true attribute="A" />  
 *
 * The above evaluates to true if the Map used at evaluation contains an entry
 * named "A", that, if transformed into a String using ".toString()", yields:
 * - If a String:   Something that can be parsed into a boolean
 * - If a Boolean:  The appropriate Boolean.TRUE
 * - If a Troolean: The appropriate Troolean.TRUE
 * 
 * The corresponding description applies to  
 * 
 * <false attribute="A" />  
 * 
 * Tracing:
 * ========
 *
 * The node can be given a name that appears in tracing output using "location"
 *
 * <true location="here"/>
 *
 * 2013.01.29 - Created based on existing Java code which used a separate
 *              datastructure for evaluation built from a DOM tree.
 *              Here, we build a JDom datastructure and use it
 *              directly when evaluating.
 ******************************************************************************/

protected class LogicExpr_TruthValue extends LogicExpr {

    private final static PropertyName NAME_FALSE = new PropertyName('false')
    private final static PropertyName NAME_TRUE = new PropertyName('true')

    /**
     * Is this about us?
     */

    boolean isAbout(Element elem) {
        assert elem != null
        return NAME_FALSE.lenientEquals(elem.name) || NAME_TRUE.lenientEquals(elem.name)
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
        // Access the element attribute, which may not exist
        //
        Attribute att = elem.getAttribute(ATTRIBUTE as String)
        //
        // No "attribute" means return the truth value is just given by the name of the element
        //
        if (!att) {
            boolean res = (NAME_TRUE.lenientEquals(elem.name))
            if (evalTrace.isDebugEnabled()) {
                evalTrace.registerDebugMsg(path, "~ Constant value: ${res}")
            }
            return res
        }
        //
        // If there *is* an attribute, consult the "stuff" map
        //
        ExtractionResult exres = extractFromStuffMap(elem, path, stuff, evalTrace, ExtractionBehaviour.MUST_EXIST_AND_NOT_BE_NULL)
        assert exres.stuffMapKey != null
        assert exres.stuffMapValue != null
        assert !exres.noSuchEntry
        //
        // Find out what the retrieved value means
        // In some cases, one needs to "always return false"
        //
        Boolean x
        boolean alwaysFalse = false
        def datum = exres.stuffMapValue
        //
        // Test by type
        //
        if (datum instanceof String) {
            x = BooleanParser.parse(datum)
            if (x == null) {
                throw new EvalException(path, "Value of key '${exres.stuffMapKey}' in 'map of stuff' is string '${datum}', which does not yield a boolean")
            }
        }
        else if (datum instanceof Boolean) {
            x = datum
        }
        else if (datum instanceof Troolean) {
            if (datum == Troolean.MU) {
                alwaysFalse = true
                x = true
            }
            else {
                x = ((Troolean)datum).booleanValue()
            }
        }
        else {
            throw new EvalException(path, "Value of key '${exres.stuffMapKey}' in 'map of stuff' has type '${datum.getClass().getName()}', which does not yield a boolean")
        }
        assert x!=null
        //
        // Finally compute a result
        //
        Boolean res
        if (NAME_TRUE.lenientEquals(elem.name)) {
            res = x && !alwaysFalse
        }
        else {
            res = !x && !alwaysFalse
        }
        if (evalTrace.isDebugEnabled()) {
            evalTrace.registerDebugMsg(path, "~ Value of key '${exres.stuffMapKey}' in 'map of stuff' is '${datum.getClass().getName()}:${datum}' -- is it equal: ${res}")
        }
        assert res != null
        return res
    }
}
