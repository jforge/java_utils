package com.mplify.enums;

import com.mplify.names.AbstractName;
import com.mplify.properties.PropertyName;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2012, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Simple enum indicating what provider is on the far end of the SMPP
 * connection. This determines how some fields are handled.
 * 
 * The corresponding field in the database is of length 30 chars!
 * 
 * 2012.04.16 - Created
 * 2012.04.19 - Now also includes VOXEO, ANY_MOBILE_NETWORK
 * 2012.04.19 - Moved to project 04_core_low, but maybe it should be
 *              in the 10_msgserver_low project...
 * 2012.04.26 - Additional providers added          
 * 2012.08.03 - Added CLX
 ******************************************************************************/

public enum ProviderVariant implements EnumBasedOnString {

    TYNTEC("tyntec"), NETSIZE("netsize"), VOXEO("voxeo"), ANY_MOBILE_NETWORK("any_mobile_network"), ANY_SMTP_SERVER("any_smtp_server"), POPFAX("popfax"), CLX("clx");

    private final PropertyName name;

    private final static String LIST_STR; // all the ProviderVariant instances, listed for the user

    static {
        String listStr = "";
        boolean addComma = false;
        for (ProviderVariant pv : ProviderVariant.values()) {
            if (addComma) {
                listStr += ",";
            }
            listStr += pv;
            addComma = true;
        }
        LIST_STR = listStr;
    }

    /**
     * Constructor
     */

    ProviderVariant(String name) {
        this.name = new PropertyName(name);
    }

    /**
     * Accessor
     */

    @Override
    public PropertyName getName() {
        return name;
    }

    /**
     * Stringify
     */

    @Override
    public String toString() {
        return name.toString();
    }

    /**
     * Given a string, find acceptable instance. Throws if not found on demand.
     * The passed "x" can be (null).
     */

    public static ProviderVariant obtain(String x, boolean throwIfNotFound) {
        //
        // "xx" will be (null) if not namifyable, in particular, if "x" is (null)
        //
        String xx = AbstractName.namify(x);
        //
        // This should be done using a map, but I'm lazy
        //
        ProviderVariant res = null;
        if (TYNTEC.name.toString().equals(xx)) {
            res = TYNTEC;
        } else if (NETSIZE.name.toString().equals(xx)) {
            res = NETSIZE;
        } else if (VOXEO.name.toString().equals(xx)) {
            res = VOXEO;
        } else if (ANY_MOBILE_NETWORK.name.toString().equals(xx)) {
            res = ANY_MOBILE_NETWORK;
        } else if (ANY_SMTP_SERVER.name.toString().equals(xx)) {
            res = ANY_SMTP_SERVER;
        } else if (POPFAX.name.toString().equals(xx)) {
            res = POPFAX;
        } else if (CLX.name.toString().equals(xx)) {
            res = CLX;
        } else {
            // not found --- problem will be caught directly below
        }
        //
        // Was "res" set?
        //
        if (res == null && throwIfNotFound) {
            throw new IllegalArgumentException("No " + ProviderVariant.class.getName() + " instance corresponding to value '" + x + "' exists. Allowed are: " + LIST_STR);
        } else {
            return res; // result may be null
        }
    }
}