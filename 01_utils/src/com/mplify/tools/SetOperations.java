package com.mplify.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Set-based operations
 *
 ******************************************************************************/

public class SetOperations {

    /**
     * Helper: Compute the intersection of two sets
     */

    public static Set<String> intersect(Set<String> a, Set<String> b) {
        Set<String> c = new HashSet<String>();
        for (String element : a) {
            if (b.contains(element)) {
                c.add(element);
            }
        }
        return c;
    }

    /**
     * Helper
     */

    public static String[] transformSetOfStringsToSortedArray(Set<String> set) {
        List<String> list = new ArrayList<String>(set);
        Collections.sort(list);
        String[] res = new String[set.size()];
        int i = 0;
        for (String string : set) {
            res[i++] = string;
        }
        return res;
    }

    /**
     * Helper: Compute the complement of two sets, i.a. A / B
     */

    public static Set<String> complement(Set<String> a, Set<String> b) {
        Set<String> c = new HashSet<String>(a);
        for (String object : b) {
            if (a.contains(object)) {
                c.remove(object);
            }
        }
        return c;
    }

    /**
     * Helper
     */

    public static Set<String> transformArrayToSet(String[] array) {
        HashSet<String> set = new HashSet<String>();
        for (int i = 0; i < array.length; i++) {
            set.add(array[i]);
        }
        return set;
    }
}