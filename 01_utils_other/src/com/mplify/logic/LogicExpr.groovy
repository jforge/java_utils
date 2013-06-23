package com.mplify.logic;

import java.util.List;
import java.util.Map;

import org.jdom.Attribute
import org.jdom.Element;

import com.mplify.logic.EvalTrace.Mode;
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
 * Abstract base class of the Logic Expressions
 * 
 * 2013.02.15 - Created
 * 2013.03.05 - Handling special case of "evaluation mode" in 
 *              extractFromStuffMap()
 ******************************************************************************/

abstract class LogicExpr {

    protected final static PropertyName ATTRIBUTE = new PropertyName('attribute')
    protected final static PropertyName VALUE = new PropertyName('value')
    protected final static PropertyName PREPRO_MODE = new PropertyName('prepromode')

    /**
     * Helper class
     */
    
    protected static class ExtractionResult {

        final PropertyName stuffMapKey // key into stuff map retrieved from an element
        final Object stuffMapValue // unspecified type, may not exist..
        final boolean noSuchEntry // true if no such entry in stuff map

        ExtractionResult(PropertyName stuffMapKey, Object stuffMapValue, boolean noSuchEntry) {
            this.stuffMapKey = stuffMapKey
            this.stuffMapValue = stuffMapValue
            this.noSuchEntry = noSuchEntry
        }
    }

    /**
     * Helper class
     */
    
    protected enum ExtractionBehaviour {

        MUST_EXIST,
        MUST_EXIST_AND_NOT_BE_NULL,
        MAY_BE_MISSING

    }

    /**
     * Used in mapping strings to PreproMode instance
     */
    
    private static final PREPRO_MODE_MAP = [ 'trim' : PreproMode.TRIM, 'lowercase' : PreproMode.LOWERCASE, 'namify' : PreproMode.NAMIFY, 'nop' : PreproMode.NOP ]

    /**
     * Get what is given by the "prepromode" attribute!
     */

    protected static PreproMode obtainPreproMode(Element elem, PropertyName attName, String path, EvalTrace evalTrace) {
        assert elem!=null
        assert path!=null
        assert evalTrace != null
        assert attName != null
        Attribute att = elem.getAttribute(attName as String)
        if (!att) {
            return PreproMode.NOP
        }
        else {
            String key = PropertyName.namify(att.value)
            if (key == null) {
                throw new EvalException(path, "The value of '${attName}' is '${att.value}', which cannot be namified")
            }
            PreproMode res = PREPRO_MODE_MAP[key]
            if (!res) {
                throw new EvalException(path, "The value of '${attName}' is '${att.value}', which is not allowed; use one of ${PreproMode.values.join(',')}")
            }
            return res;
        }
    }

    private static final MATCH_MODE_MAP = [ 'match' : MatchMode.MATCH, 'find' : MatchMode.FIND ]
    
    /**
     * Get what is given by the "matchmode" attribute!
     */

    protected static MatchMode obtainMatchMode(Element elem, PropertyName attName, String path, EvalTrace evalTrace) {
        assert elem!=null
        assert path!=null
        assert evalTrace != null
        assert attName != null
        Attribute att = elem.getAttribute(attName as String)
        if (!att) {
            return MatchMode.FIND
        }
        else {
            String key = PropertyName.namify(att.value)
            if (key == null) {
                throw new EvalException(path, "The value of '${attName}' is '${att.value}', which cannot be namified")
            }
            MatchMode res = MATCH_MODE_MAP[key]
            if (!res) {
                throw new EvalException(path, "The value of '${attName}' is '${att.value}', which is not allowed; use one of ${MatchMode.values().join(',')}")
            }
            return res;
        }
    }
    
    /**
     * Helper: Extract a mandatory attribute named by the attribute ATTRIBUTE in "elem" from "stuff" and return it.
     * If requested, check for null-ness and flag null-ness as an error. The type of the returned Object is not constrained!
     * May throw LocalHandleException. Returns [ key , datum ] where datum is stuff[key], and key comes from the value of "attribute"
     */

    protected static ExtractionResult extractFromStuffMap(Element elem, String path, Map stuff, EvalTrace evalTrace, ExtractionBehaviour behaviour) {
        assert elem!=null
        assert path!=null
        assert stuff!=null
        assert evalTrace!= null
        assert behaviour!= null
        //
        // Get the key into the "stuff" map by looking at the attribute "attribute" of "elem"
        // It must exist (otherwise an EvalException will be raised)
        //
        PropertyName stuffMapKey = getStuffMapKey(elem, path, evalTrace)
        assert stuffMapKey != null
        //
        // If we are just verifying, don't bother looking in "stuff"
        //
        if (evalTrace.mode == Mode.VERIFICATION) {
            return new ExtractionResult(stuffMapKey, "placeholder", false)
        }
        //
        // Retrieve what's stored in "stuff" under "key".
        // Note that this means that in verification mode, "stuff" must be filled with complete data...
        //
        if (!stuff.containsKey(stuffMapKey)) {
            if (behaviour == ExtractionBehaviour.MAY_BE_MISSING) {
                return new ExtractionResult(stuffMapKey, null, true)
            }
            else {
                throw new EvalException(path, "Value of '${ATTRIBUTE}' is '${stuffMapKey}', but there is no such entry in the passed 'stuff' map")
            }
        }
        //
        // The type of the value is not further specified
        //
        def value = stuff[stuffMapKey]
        if (value == null) {
            if (behaviour == ExtractionBehaviour.MUST_EXIST) {
                return new ExtractionResult(stuffMapKey, null, false)
            }
            else {
                assert behaviour == ExtractionBehaviour.MUST_EXIST_AND_NOT_BE_NULL
                throw new EvalException(path, "Value of '${ATTRIBUTE}' is '${stuffMapKey}', but the corresponding entry in the 'stuff' map is (null), which is not allowed")
            }
        }
        else {
            return new ExtractionResult(stuffMapKey, value, false)
        }
    }

    /**
     * Extract the value of the element's attribute named "ATTRIBUTE", 
     * which is "namified" (lowercased and trimmed) and then returned.
     * The value is supposed to reference a key in the "stuff" map. 
     * The fact that the key was used is registered in "evalTrace" 
     * If there is no namifiable attribute called "attribute", an 
     * EvalException is raised.
     */

    protected static PropertyName getStuffMapKey(Element elem, String path, EvalTrace evalTrace) {
        assert elem != null
        assert path != null
        assert evalTrace != null
        Attribute att = elem.getAttribute(ATTRIBUTE as String)
        if (!att) {
            throw new EvalException(path, "No attribute '${ATTRIBUTE}' containing a key into the 'stuff' map")
        }
        String stuffMapKey = PropertyName.namify(att.value)
        if (stuffMapKey == null) {
            throw new EvalException(path, "Could not properly namify value of attribute '${ATTRIBUTE}' which should be a key into the 'stuff' map")
        }
        PropertyName res = new PropertyName(stuffMapKey)
        evalTrace.addStuffMapKey(res)
        return res        
    }

    /**
     * Get what is given by the "value" attribute (not null but otherwise a generic String)
     */

    protected static String getValue(Element elem, String path, EvalTrace evalTrace) {
        assert elem != null
        assert path != null
        assert evalTrace != null
        Attribute att = elem.getAttribute(VALUE as String)
        if (!att) {
            throw new EvalException(path, "There is no attribute '${VALUE}'")
        }
        return att.value
    }

    /**
     * To be implemented
     */

    abstract boolean isAbout(Element elem)

    /**
     * To be implemented
     */

    abstract boolean handle(Element elem, String path, Map stuff, EvalTrace evalTrace)

}
