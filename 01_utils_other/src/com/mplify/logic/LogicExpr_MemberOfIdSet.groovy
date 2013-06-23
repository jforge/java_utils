package com.mplify.logic;

import org.jdom.Attribute
import org.jdom.Element

import com.mplify.checkers._check
import com.mplify.id.AbstractId
import com.mplify.listparsing.Parsing
import com.mplify.logic.LogicExpr.ExtractionBehaviour;
import com.mplify.logic.LogicExpr.ExtractionResult;
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
 * "check set membership for ids"
 * ==============================
 * 
 * The expression is written down using XML as:
 *
 * <member_of_id_set attribute="A" >
 *    <member>7987988</member>
 *    <member>1188991</member>
 * </member_of_id_set>
 * 
 *    or
 *    
 * <member_of_id_set attribute="A" value="7987988,1188991" />
 * 
 *    or both combined
 *    
 * <member_of_id_set attribute="A" value="7987988,1188991" />
 *    <member>7987988</member>
 *    <member>9988111</member>
 * </member_of_id_set>
 *
 * It evaluates to true if the Map used at evaluation contains an entry
 * named "A", that, if transformed into a Set of AbstractId, yields
 * something that is equal to one of the listed "set member" ids.
 *
 * Tracing:
 * ========
 * 
 * The node can be given a name that appears in tracing output using "location"
 *
 * <member_of_id_set attribute="A" location="here" />
 * 
 * 2013.01.31 - Created based on existing Java code which used a separate
 *              datastructure for evaluation built from a DOM tree.
 *              Here, we build a JDom datastructure and use it
 *              directly when evaluating.
 *              
 * TODO: Buffering of set data and rejoined string              
 ******************************************************************************/

protected class LogicExpr_MemberOfIdSet extends LogicExpr {

    private final static PropertyName NAME_MEMBER_OF_ID_SET = new PropertyName('member_of_id_set')
    private final static PropertyName MEMBER = new PropertyName('member')

    /**
     * Get the set of integer, which is given by subelements!
     * Returns a Set of Integer, never null
     */

    private static Set getSetViaChildren(Element elem, String path, EvalTrace evalTrace) {
        assert elem!=null
        assert path!=null
        assert evalTrace != null
        Set res = new HashSet()
        // do not use a Closure here as we need to throw Exceptions
        for (Element child : elem.children) {
            if (MEMBER.lenientEquals(child.name)) {
                try {
                    int x = Integer.valueOf(child.text)
                    _check.largerThanZero(x, 'check the permitted id value')
                    res << x
                }
                catch (Exception exe) {
                    throw new EvalException(path, "Could not transform '${child.text}' into an id")
                }
            }
            else {
                evalTrace.registerWarning(path, "Unknown child '${child.name}' -- skipped")
            }
        }
        return res
    }

    /**
     * Get the set of integer, which is given by the value attribute!
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
        List rawList = Parsing.parseCommaSeparatedAtoms(att.value, null, null, false)
        assert rawList != null
        // do not use a Closure here as we need to throw Exceptions
        for (String atom : rawList) {
            try {
                int x = Integer.valueOf(atom)
                _check.largerThanZero(x, 'check the permitted id value')
                res << x
            }
            catch (Exception exe) {
                throw new EvalException(path, "Could not transform '${atom}' into an id")
            }
        }
        return res
    }

    /**
     * Is this about us?
     */

    boolean isAbout(Element elem) {
        assert elem != null
        return NAME_MEMBER_OF_ID_SET.lenientEquals(elem.name)
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
        // "Datum" must be an "id" - check that!
        //
        Integer value
        if (stuffMapValue instanceof AbstractId) {
            value = ((AbstractId)stuffMapValue).value
        }
        else {
            throw new EvalException(path, "The map-of-stuff contains '${stuffMapValue.getClass().getName()}:${stuffMapValue}' as entry '${stuffMapKey}', which is not allowed here")
        }
        assert value != null
        //
        // Check
        //        
        boolean res = set.contains(value)
        if (evalTrace.isDebugEnabled()) {
            String rejoined = set.join(',') // TODO: cache this
            evalTrace.registerDebugMsg(path, "~ Value of key '${stuffMapKey}' in 'map of stuff' is '${stuffMapValue}' -- is it in the set {${rejoined}}: ${res}")
        }
        return res
    }
}