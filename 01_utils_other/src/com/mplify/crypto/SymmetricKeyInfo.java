package com.mplify.crypto;

import com.mplify.checkers.Check;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * All the data needed for key identification
 * 
 * 2011.12.30 - Redesigned a second time
 ******************************************************************************/

public class SymmetricKeyInfo {

    public final int symKeyIndex; 
    public final SymmetricKey symKey; // the symmetric key corresponding to "keyIndex"; opaque interface, not null

    public SymmetricKeyInfo(int symKeyIndex, SymmetricKey symKey) {
        _check.largerOrEqualToZero(symKeyIndex, "symmetric key index");
        _check.notNull(symKey, "symmetric key");
        this.symKeyIndex = symKeyIndex;
        this.symKey = symKey;
    }
}