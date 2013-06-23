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
 * Minimal interface for something which can manage symmetric keys
 * 
 * 2011.06.16 - Created
 * 2011.11.08 - Methods renamed
 * 2011.11.09 - Added isAcceptableKeyIndex()
 * 2011.12.29 - Added getKey(Integer) in addition to getKey(int)
 ******************************************************************************/

public interface KeyManager {
 
    public SymmetricKey getKey(int symKeyIndex);
    public SymmetricKey getKey(Integer symKeyIndex);
    public Integer randomKeyIndex();
    public boolean isAcceptableKeyIndex(int x);
}