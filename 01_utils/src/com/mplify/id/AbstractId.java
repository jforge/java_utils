package com.mplify.id;

import com.mplify.checkers.Check;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Generic superclass of 'Ids', which are typed surrogate numeric ids used
 * in the database in their untyped form and outside of the DAOs in their
 * typed form. Typeing these saved the day many times.
 * 
 * 2004.10.08 - Reviewed
 * 2004.12.06 - Added 'handle'
 * 2005.05.02 - Copied from Mobilux project to the newly created package
 *              com.mplify.store
 * 2006.02.15 - Slight modifications. This class is currently used by
 *              a number of projects.
 * 2007.01.02 - Review; all the code duplicated in sucblasses moved to here!
 * 2009.12.18 - Implements Comparable<AbstractId> instead of Comparable,
 *              compareTo now compares across various subtypes of AbstractId.
 * 2011.03.01 - Introduced Check()             
 * 2013.03.14 - Simplified, no longer dependes on org.w3c.dom.Node
 ******************************************************************************/

public abstract class AbstractId implements Comparable<AbstractId> {

    /**
     * Constraints on the id: ~ The value is > 0 and corresponds to the numeric id in the database. ~ Why it must be >
     * 0: MySQL starts counting from 0 in auto-increment columns (the id is generally in an auto-increment column) and
     * we disallow 0 for historical reasons. ~ There is no way to represent 'invalid id' using an AbstractId. In-code
     * "invalid" must be represented by the Java 'null'
     */

    private final int id;

    /**
     * Buffer for the text given out by "toString()". It is filled at the first call to "toString()"
     */

    private volatile String toStringBuf;

    /**
     * A buffer for the hash value. It is filled at the first call to "hashCode()" 
     */

    private volatile Integer hashBuf;

    /**
     * Construct from an integer id, which must be > 0
     */

    public AbstractId(int x) {
        Check.largerThanZero(x, "integer");
        this.id = x;
    }

    /**
     * Construct from an integer id, which must be > 0
     */

    public AbstractId(Integer x) {
        Check.notNull(x, "integer");
        Check.largerThanZero(x.intValue(), "integer");
        this.id = x.intValue();
    }

    /**
     * Access underlying value. This has been made "final".
     */

    public final int getValue() {
        return id;
    }

    /**
     * Access underlying value. This has been made "final".
     */

    public final Integer getValueAsInteger() {
        return Integer.valueOf(id);
    }

    /**
     * Textify to value (with buffer for efficiency). This has been made "final"
     */

    @Override
    public final String toString() {
        if (toStringBuf == null) {
            // parallel assignment to 'handle' by several threads should not be a problem
            toStringBuf = "[" + getClass().getSimpleName() + ":" + Integer.toString(id) + "]";
        }
        assert (toStringBuf != null);
        return toStringBuf;
    }

    /**
     * HashCode is directly computed from value and the actual subclass.
     * The hashCode depends on the actual numeric id but also on the actual class
     * so that two AbstractId of different actual type but equal id may yield
     * different values.
     */

    @Override
    public final int hashCode() {
        if (hashBuf == null) {
            hashBuf = Integer.valueOf(id ^ this.getClass().getName().hashCode());
        }
        return hashBuf.intValue();
    }

    /**
     * Two instances are equal if they have the same type and same "value". This has been made "final".
     */

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true; // quick guess
        }
        if (obj == null) {
            return false;
        }
        // the actual classes must **exactly** correspond (instead of 'obj' being just a subclass of 'this')
        if (!(obj.getClass().equals(this.getClass()))) {
            return false;
        }
        // values must be equal
        return (this.getValue() == ((AbstractId) obj).getValue());
    }

    /**
     * Used in sorting by actual type first, underlying type second. This has been made "final".
     * This defines the natual ordering of AbstractId. Must be "consistent with equals()"
     */

    @Override
    public final int compareTo(AbstractId obj) {
        if (this == obj) {
            return 0; // quick guess
        }
        Check.notNull(obj,"obj");
        //
        // Actual class equality?
        //
        int t1 = this.getClass().getName().compareTo(obj.getClass().getName());
        if (t1 != 0) {
            return t1;
        }
        //
        // Value equlity?
        //
        return (this.getValue() - obj.getValue());
    }

}
