package com.mplify.logic;

import org.jdom.Attribute
import org.jdom.Element

import com.mplify.properties.PropertyName
import com.mplify.utils.BooleanParser;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2013, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Check whether a value in the "map of stuff" is set
 * ==================================================
 *
 * Expression as written in XML
 * 
 * <is_set   attribute="A" />
 * <is_unset attribute="A" />
 * 
 * The "is_set" expression evaluates to true if the Map used at evaluation 
 * contains an entry named "A". The "is_unset" expression predictably does
 * the reverse. 
 * 
 * The attribute "null_means_unset" can be added and takes values "true" 
 * and "false" to tune the behaviour.
 * 
 * Tracing:
 * ========
 * 
 * The node can be given a name that appears in tracing output using "location"
 *
 * <is_set attribute="A" location="here" />
 *
 * 2013.01.29 - Created based on existing Java code which used a separate
 *              datastructure for evaluation built from a DOM tree.
 *              Here, we build a JDom datastructure and use it
 *              directly when evaluating.
 ******************************************************************************/

protected class LogicExpr_SetUnset extends LogicExpr {

    private final static PropertyName NAME_IS_SET = new PropertyName('is_set')
    private final static PropertyName NAME_IS_UNSET = new PropertyName('is_unset')
    private final static PropertyName NULL_MEANS_UNSET = new PropertyName('null_means_unset')

    /**
     * Is this about us?
     */

    boolean isAbout(Element elem) {
        assert elem != null
        return NAME_IS_UNSET.lenientEquals(elem.name) || NAME_IS_SET.lenientEquals(elem.name)
    }

    /**
     * Get what is given by the "null means unset" attribute!
     */

    private static boolean getNullMeansUnset(Element elem, String path, EvalTrace evalTrace) {
        assert elem!=null
        assert path!=null
        assert evalTrace != null
        Attribute att = elem.getAttribute(NULL_MEANS_UNSET as String)
        if (!att) {
            return false
        }
        Boolean res = BooleanParser.parse(att.value)
        if (res == null) {
            throw new EvalException(path, "The value of '${NULL_MEANS_UNSET}' is '${att.value}', which is not allowed")
        }
        return res
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
        // Get the value of attribute "attribute"
        //
        PropertyName stuffMapKey = getStuffMapKey(elem, path, evalTrace)
        Boolean nullMeansUnset = getNullMeansUnset(elem, path, evalTrace)
        assert stuffMapKey != null
        assert nullMeansUnset != null
        //
        // Now compute
        //
        boolean isSet = stuff.containsKey(stuffMapKey)
        boolean res = (isSet && !(nullMeansUnset && (stuff[stuffMapKey] == null)))
        if (NAME_IS_UNSET.lenientEquals(elem.name)) {
            res = !res
        }
        if (evalTrace.isDebugEnabled()) {
            String msg = ''
            if (nullMeansUnset) {
                msg = ' (and not null)'
            }
            evalTrace.registerDebugMsg(path, "~ Checking whether key '${stuffMapKey}' is set in the 'map of stuff'${msg}: ${res}")
        }
        return res
    }
}
