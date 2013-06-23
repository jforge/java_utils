package com.mplify.logic;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2013, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * This exception is used to signal a problem in properly evaluation an
 * expression. Depending on the "mode" (evaluation or verification), this
 * may lead to an immediate stop of processing or to the logging of an error.
 * 
 * 2013.01.31 - Created
 ******************************************************************************/

@SuppressWarnings("serial")
public class EvalException extends Exception {

    private final String path; // may be null

    /**
     * Constrcut from "path" (an expression indicating the location in a JDOM
     * tree, or in an XML expression) and a message. Andy can be null 
     */
    
    public EvalException(String path, String msg) {
        super(msg);
        this.path = path;
    }

    /**
     * Get the possibly null path
     */
    
    public String getPath() {
        return path;
    }

}
