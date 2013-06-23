package com.mplify.crypto;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Exception that can be raised during KeyManager construction
 * 
 * 2011.06.19 - Created
 ******************************************************************************/

@SuppressWarnings("serial")
public class KeyManagerConstructionException extends Exception {

    public KeyManagerConstructionException() {
        super();
    }

    public KeyManagerConstructionException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyManagerConstructionException(String message) {
        super(message);
    }

    public KeyManagerConstructionException(Throwable cause) {
        super(cause);
    }

}
