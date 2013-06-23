package com.mplify.enums;

import com.mplify.checkers.Check;
import com.mplify.id.AbstractId;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A class that is used to indicate versioning state of an object in the store
 * 
 * State transitions are:
 * 
 *     +------------------------------------+
 *     |                                    |
 *     |                                    V
 * EDITABLE ---> ACTIVE ------------> DEACTIVATED
 *                  ^                       |
 *                  |                       |
 *                  +-----------------------+
 * 
 * Once out of "EDITABLE", the object cannot get back to that state.
 * 
 * 2008.11.19 - Created
 * 2008.11.22 - Renamed from AssignmentTreeState to VersioningState
 * 2012.06.07 - Restructured to a simple "enum" with values
 *              EDITABLE, ACTIVE, DEACTIVATED as opposed to ON, OFF
 * 2013.03.01 - checkStateTransitionAllowed() moved to here              
 ******************************************************************************/

public enum VersioningState {

    EDITABLE(1), ACTIVE(2), DEACTIVATED(3);

    private final int value;

    VersioningState(int x) {
        assert x > 0; // this is checked in the database
        value = x;
    }

    public int getValue() {
        return value;
    }

    public static VersioningState obtain(int x, boolean throwIfNotFound) {
        switch (x) {
        case 1:
            return EDITABLE;
        case 2:
            return ACTIVE;
        case 3:
            return DEACTIVATED;
        default:
            if (throwIfNotFound) {
                throw new IllegalArgumentException("The passed integer " + x + " does not map to a valid " + VersioningState.class.getName());
            } else {
                return null;
            }
        }
    }

    /**
     * Check state transition. If not allowed, throw CheckException
     */

    public static void checkStateTransitionAllowed(AbstractId id, VersioningState cur, VersioningState next) {
        Check.notNull(id, "id");
        Check.notNull(cur, "current state");
        Check.notNull(next, "next state");
        boolean allOk = true;
        if (cur == VersioningState.ACTIVE) {
            if (next == VersioningState.DEACTIVATED) {
                // acceptable change!
            } else if (next == VersioningState.EDITABLE) {
                allOk = false;
            } else {
                Check.cannotHappen("Unknown state to set to: " + next);
            }
        } else if (cur == VersioningState.DEACTIVATED) {
            if (next == VersioningState.ACTIVE) {
                // acceptable change!
            } else if (next == VersioningState.EDITABLE) {
                allOk = false;
                // the above throws
            } else {
                Check.cannotHappen("Unknown state to set to: " + next);
            }
        } else if (cur == VersioningState.EDITABLE) {
            if (next == VersioningState.ACTIVE) {
                // acceptable change!
            } else if (next == VersioningState.DEACTIVATED) {
                // acceptable change!
            } else {
                Check.cannotHappen("Unknown state to set to: " + next);
            }
        } else {
            Check.cannotHappen("Unknown previous state: " + cur);
        }
        if (!allOk) {
            Check.fail("Cannot change %s from state %s to %s", id, cur, next);
        }
    }

}
