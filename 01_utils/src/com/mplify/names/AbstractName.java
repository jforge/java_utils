package com.mplify.names;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.mplify.checkers.Check;
import com.mplify.properties.PropertyName;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A specially typed String that names something. This is used mainly to
 * retrieve properties. It is easier to use than 'String' because:
 * 
 * 1) It's typed - if you subtype correctly you cannot confuse or pass or
 *    return or assign the name for an object of type X with the name for
 *    an object of type Y
 *    
 * 2) You can be sure that the underlying string is ** never null **
 *    ** always trimmed **, ** never empty ** and ** always lowercase **. 
 *    Checking passed parameters then reduces to verifying the the passed 
 *    AbstractName subclass instance is different from null
 * 
 * Compare with "TrimmedString", which can be mixed case.
 * 
 * 2007.01.22 - Created
 * 2008.11.30 - Made comparable by adding code from "comm"
 * 2010.02.10 - Added length()
 * 2010.12.21 - Print of set added here, calling _check in constructor
 *              "setToString(Set<? extends AbstractName> set)"
 * 2012.02.19 - namify() added and used in constructor for great clarity!
 *              Other functions can then use this to get an "abstract 
 *              name" in a String w/o having to call "new AbstractName"
 * 2013.02.01 - Added lenientEquals()              
 ******************************************************************************/

public abstract class AbstractName implements Cloneable, Comparable<Object> {

    private String name; // not null, not empty, trimmed, lowercased

    /**
     * Constructor. You must not pass null or the empty string
     */

    public AbstractName(String name) {
        Check.notNull(name, "name");
        this.name = namify(name);
        Check.isTrue(this.name != null, "The passed string '%s' could not be transformed into a proper 'name'", name);
    }

    /**
     * Helper that "namifies" a string (trims, lowercases). Returns (null) if the string could not be namified, in particular if (null) was passed in.
     */

    public static String namify(String x) {
        if (x == null) {
            return null;
        } else {
            String y = x.trim().toLowerCase();
            if (y.isEmpty()) {
                return null;
            } else {
                return y;
            }
        }
    }

    /**
     * Get the string, which is actually the underlying "name" as such. Never null, never empty, always lowercase.
     */

    @Override
    public String toString() {
        return name;
    }

    /**
     * Hash it!
     */

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Equality
     */

    @Override
    public boolean equals(Object obj) {
        // quick guess
        if (this == obj) {
            return true;
        }
        // look deeper
        if (obj == null) {
            return false;
        }
        // must be **same AbstractName subclass** (so no instanceof here)
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        AbstractName other = (AbstractName) obj;
        return this.name.equals(other.name);
    }

    /**
     * Used in sorting by underlying numeric value. Compatible with equals()
     */

    @Override
    public int compareTo(Object obj) {
        if (this == obj) {
            return 0; // fast guess
        }
        Check.notNull(obj, "obj");
        if (!(obj instanceof AbstractName)) {
            throw new ClassCastException("The passed 'obj' is of class " + obj.getClass().getName() + " instead of class " + AbstractName.class.getName());
        }
        AbstractName other = (AbstractName) obj;
        return (this.name.compareTo(other.name));
    }

    /**
     * Length of underlying string
     */

    public int getLength() {
        return name.length();
    }

    /**
     * Transform a set of AbstractName into a string of comma-separated-atoms. Originally from "MsgMedium".
     * (null) is not accepted.
     */

    public static String setToString(Set<? extends AbstractName> set) {
        Check.notNull(set, "set");
        StringBuilder buf = new StringBuilder();
        // sort alphabetically
        List<AbstractName> list = new ArrayList(set);
        Collections.sort(list);
        boolean appendComma = false;
        for (AbstractName rn : list) {
            if (appendComma) {
                buf.append(",");
            }
            buf.append(rn);
            appendComma = true;
        }
        return buf.toString();
    }

    /**
     * Comparison sugar
     */

    public boolean lenientEquals(String other) {
        return other != null && this.toString().equals(PropertyName.namify(other));
    }

    /**
     * Comparison sugar
     */

    public boolean lenientEquals(AbstractName other) {
        return other != null && this.equals(other);
    }

}
