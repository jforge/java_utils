package com.mplify.checkers;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * Distributed under the MIT License (http://opensource.org/licenses/MIT)
 *******************************************************************************
 *******************************************************************************
 * Runtime Exception (i.e. no need to declare it) thrown by the "checks"
 ******************************************************************************/

@SuppressWarnings("serial")
public class UnexpectedDataException extends RuntimeException {

    public UnexpectedDataException() {
        super();
    }

    public UnexpectedDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnexpectedDataException(String message) {
        super(message);
    }

    public UnexpectedDataException(Throwable cause) {
        super(cause);
    }
    
}