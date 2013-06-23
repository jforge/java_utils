package com.mplify.threads;

import java.util.HashSet;
import java.util.Set;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Facility to give "roles" to thread and to assert at selected places
 * the role of the executing thread.
 * 
 * 2010.09.15 - Created
 * 2010.09.22 - Still changing
 ******************************************************************************/

public class ThreadColor {

    public static enum Role { 

        MAIN, // the thread animating main()       
        SERVER_SOCKET_HANDLER, // any thread animating a server socket handler

        DEALER_SERVICE, // the thread animating the topmost runnable of the dealer process
        
        DEALER_DEVICE,
        
        DEALER_BOARD_CLIENT__LIVE_SOCKET_READER, // the thread reading the socket on the client side
        DEALER_BOARD_CLIENT__LIVE_SOCKET_WRITER, // the thread writing the socket and doing visits on the server side
        
        DEALER_BOARD_SERVER__LIVE_SOCKET_READER, // the thread reading the socket on the client side        
        DEALER_BOARD_SERVER__LIVE_SOCKET_WRITER, // the thread writing the socket and doing visits on the server side
        DEALER_BOARD_SERVER__DISPATCHER          // the thread matching deals and dealers
    }
        
    private static ThreadLocal<Set<Role>> myRoles = new ThreadLocal<Set<Role>>();
       
    // to be used in assert texts
            
    private static String getAssertionText(Role[] expectedRoles,Set<Role> rolesOfThisThread) {
        StringBuilder buf = new StringBuilder("The current's thread roles are: ");
        {            
            boolean addComma = false;
            for (Role r : rolesOfThisThread) {
                if (addComma) {
                    buf.append(", ");                    
                }
                buf.append(r);
                addComma = true;
            }
        }
        buf.append("; expected one of ");
        {
            boolean addComma = false;
            for (Role r : expectedRoles) {
                if (addComma) {
                    buf.append(", ");               
                }
                buf.append(r);
                addComma = true;
            }
        }
        return buf.toString();
    }

    /**
     * To be used at Thread initialization time 
     */
    
    public static void addRole(Role newRole) {
        assert newRole != null;
        if (myRoles.get()==null) {
            myRoles.set(new HashSet<Role>());
        }
        myRoles.get().add(newRole);
    }
    
    /**
     * Helper for set membership check
     */
    
    private static boolean isIntersectionNonEmpty(Role[] expectedRoles,Set<Role> rolesOfThisThread) {
        assert expectedRoles!=null;
        assert rolesOfThisThread!=null;
        for (Role expectedRole : expectedRoles) {
            if (rolesOfThisThread.contains(expectedRole)) {
                return true;
            }
        }        
        return false;
    }
    
    /**
     * Checking whether the current thread has one of the given roles. 
     * Special case: If the current Thread has no role, fail with an AssertionError.
     * Special case: If the passed array is empty or null, fail with an AssertionError.
     * For easier reading of code, this function returns true so that it can be
     * called through "assert ThreadColor.assertRole(Role.X,Role.Y)"
     */
    
    public static boolean assertRole(Role... roles) {
        assert roles != null : "The passed array-of-roles is (null)";
        assert roles.length > 0 : "The passed array-of-roles is empty";
        Set<Role> rolesOfThisThread = myRoles.get();
        assert rolesOfThisThread !=null : "The current thread has no roles set";
        assert isIntersectionNonEmpty(roles,rolesOfThisThread) : getAssertionText(roles,rolesOfThisThread);
        return true;
    }
}

