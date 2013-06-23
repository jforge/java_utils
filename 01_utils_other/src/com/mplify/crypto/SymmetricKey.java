package com.mplify.crypto;

import com.mplify.stringmangling.DecryptException;
import com.mplify.stringmangling.EncryptException;


/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Simple facility for encrypt/decrypt. Key data is supposed to be stored in
 * the instance.
 * 
 * 2011.06.15 - Created
 * 2011.11.08 - Removed the "keyIndex()"
 ******************************************************************************/

public interface SymmetricKey {

    public byte[] encrypt(byte[] data) throws EncryptException;
    public byte[] decrypt(byte[] data) throws DecryptException;
    
}
