package com.mplify.logic;

import org.jdom.Attribute
import org.jdom.Element

import com.mplify.listparsing.Parsing
import com.mplify.logic.LogicExpr.ExtractionBehaviour
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
 * "check set membership"
 * ======================
 * 
 * The expression is written down using XML as:
 *
 * <member_of_set attribute="A" >
 *    <member>M1</member>
 *    <member>M2</member>
 * </member_of_set>
 * 
 *    or
 *    
 * <member_of_set attribute="A" value="M1,M2" />
 * 
 *    or both combined
 *    
 * <member_of_set attribute="A" value="M1,M2" />
 *    <member>M3</member>
 *    <member>M4</member>
 * </member_of_set>
 *
 * It evaluates to true if the Map used at evaluation contains an entry
 * named "A", that, if transformed into a String using ".toString()", yields
 * something that is equal to one of the listed "set member" strings.
 *
 * Tracing:
 * ========
 * 
 * The node can be given a name that appears in tracing output using "location"
 *
 * <member_of_set attribute="A" location="here" />
 * 
 * 2013.01.29 - Created based on existing Java code which used a separate
 *              datastructure for evaluation built from a DOM tree.
 *              Here, we build a JDom datastructure and use it
 *              directly when evaluating.
 *              
 * TODO: Buffering of set data and rejoined string              
 ******************************************************************************/

protected class LogicExpr_MemberOfSet extends LogicExpr {

    private final static PropertyName NAME_MEMBER_OF_SET = new PropertyName('member_of_set')
    private final static PropertyName MEMBER = new PropertyName('member')

    /**
     * Get the set of string, which is given by subelements!
     */

    private static Set getSetViaChildren(Element elem, String path, EvalTrace evalTrace) {
        assert elem!=null
        assert path!=null
        assert evalTrace!=null
        Set res = new HashSet()
        elem.children.each { Element child ->
            if (MEMBER.lenientEquals(child.name)) {
                res << child.text
            }
            else {
                evalTrace.registerWarning(path, "Unknown child '${child.name}' -- skipped")
            }
        }
        return res
    }

    /**
     * Get the set of string, which is given by the value attribute!
     */

    private static Set getSetViaValue(Element elem, String path, EvalTrace evalTrace) {
        assert elem!=null
        assert path!=null
        assert evalTrace != null
        Set res = new HashSet()
        Attribute att = elem.getAttribute(VALUE as String)
        if (!att) {
            return res
        }
        assert att != null
        res.addAll(Parsing.parseCommaSeparatedAtoms(att.value, null, null, false))
        return res
    }

    /**
     * Is this about us?
     */

    boolean isAbout(Element elem) {
        assert elem != null
        return NAME_MEMBER_OF_SET.lenientEquals(elem.name)
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
        // Collect set members both from attribute and children
        //
        Set set = getSetViaChildren(elem, path, evalTrace)
        set.addAll(getSetViaValue(elem, path, evalTrace))
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
        // Stringify datum of unknown type
        //
        String value = stuffMapValue as String
        if (value == null) {
            throw new EvalException(path, "The map-of-stuff contains '${stuffMapValue.getClass().getName()}' as entry '${stuffMapKey}', which cannot be stringified")
        }
        //
        // Check
        //
        boolean res = set.contains(value)
        if (evalTrace.isDebugEnabled()) {
            String rejoined = set.join(',')
            evalTrace.registerDebugMsg(path, "~ Value of key '${stuffMapKey}' in 'map of stuff' is '${value}' -- is it in the set {${rejoined}}: ${res}")
        }
        return res
    }
}