package com.mplify.names;

import com.mplify.checkers.Check;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Created to replace the bog-standard "String" by something with is typed, 
 * never null, never empty and always lowercased.
 * 
 * The InspectionName may have an inner "hierarchical" structure by
 * having strings separated by "." 
 * 
 * 2010.11.17 - Created
 * 2011.08.24 - Removed 2-level hierarchy of names. Instead added a constructor 
 *              taking parent and child to build a new InspectionName.
 * 2011.08.30 - Strengthened checks.
 ******************************************************************************/

public abstract class DottedAbstractName extends AbstractName {

    /**
     * Construct from a pure string, which can be of the structure "A.B.C.D" were
     * A, B, C, D must be substrings without '.' that can be passed to an AbstractName
     * constructor. 
     */

    public DottedAbstractName(String name) {
        super(verifyStringStructure(name));
    }

    /**
     * Construct from a series of string, concatenating with '.'
     * Empty or null children are just ignored though at least one must be set
     */

    public DottedAbstractName(String... children) {
        super(join(null, null, null, children));
    }

    /**
     * Construct from a series of string, concatenating with '.'
     * Empty or null children are just ignored. Even the "parent" can be null,
     * if the compiler can disambiguate the constructor used.
     */

    public DottedAbstractName(DottedAbstractName parent, String... children) {
        super(join(parent, null, null, children));
    }

    /**
     * Construct from a series of string, concatenating with '.'
     * Empty or null children are just ignored. Even the "parent" can be null,
     * if the compiler can disambiguate the constructor used.
     */

    public DottedAbstractName(DottedAbstractName parent, DottedAbstractName firstChild, String... children) {
        super(join(parent, firstChild, null, children));
    }

    /**
     * Construct from a series of string, concatenating with '.'
     * Empty or null children are just ignored. Even the "parent" can be null,
     * if the compiler can disambiguate the constructor used.
     */

    public DottedAbstractName(DottedAbstractName parent, DottedAbstractName firstChild, DottedAbstractName secondChild, String... children) {
        super(join(parent, firstChild, secondChild, children));
    }

    /**
     * Reconstructs the string of the name, checking the substrings.
     */

    protected static String join(DottedAbstractName parent, DottedAbstractName firstChild, DottedAbstractName secondChild, String... children) {
        assert children != null;
        StringBuilder buf = new StringBuilder();
        boolean addSep = false;
        if (parent != null) {
            if (addSep) {
                buf.append(".");
            }
            buf.append(parent.toString()); // no need to verify string structure as this already is a DottedAbstractName
            addSep = true;
        }
        if (firstChild != null) {
            if (addSep) {
                buf.append(".");
            }
            buf.append(firstChild.toString()); // no need to verify string structure as this already is a DottedAbstractName
            addSep = true;
        }
        if (secondChild != null) {
            if (addSep) {
                buf.append(".");
            }
            buf.append(secondChild.toString()); // no need to verify string structure as this already is a DottedAbstractName
            addSep = true;
        }
        // there may be no children at all (children is String[0]), but that's ok
        for (String cur : children) {
            if (cur == null) {
                // special case: cur is null, do nothing
            }
            else {
                cur = cur.trim();
                if (cur.isEmpty()) {
                    // special case: trimmed cur is full empty; do nothing
                }
                else {
                    if (addSep) {
                        buf.append(".");
                    }
                    buf.append(verifyStringStructure(cur)); // check the child string for uglyness, e.g. "a . b .. c"
                    addSep = true;
                }
            }
        }
        String res = buf.toString();
        Check.notNullAndNotOnlyWhitespace(res, "join result");
        return res;
    }

    /**
     * Checking helper which also reconstructs the string of the name.
     * Improvement: Check x against a map of known values and return it at once if good.
     */

    private static String verifyStringStructure(String x) {
        Check.notNull(x);
        String[] splitted = x.split("\\.", -1);
        Check.isTrue(splitted.length > 0, "Empty string passed");
        StringBuilder buf = new StringBuilder();
        boolean addSep = false;
        for (int i = 0; i < splitted.length; i++) {
            if (addSep) {
                buf.append(".");
            }            
            String trimmed = splitted[i].trim();
            Check.isFalse(trimmed.isEmpty(), "There is a fully empty sub element");
            buf.append(trimmed);
            addSep = true;
        }
        return buf.toString();
    }
}
