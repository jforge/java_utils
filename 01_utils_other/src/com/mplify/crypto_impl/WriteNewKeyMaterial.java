package com.mplify.crypto_impl;

import java.io.File;

import com.mplify.properties.ReferencedStuff;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Generate key material in /tmp
 * 
 * 2011.YY.XX - Created
 ******************************************************************************/

public class WriteNewKeyMaterial {

    private final static File file = new File("/tmp/keymaterial");
    private final static File file2 = new File("/tmp/keymaterial2");
    private final static int NUMBER_OF_KEYS = 1000;    
    
    public static void main(String[] argv) throws Exception {
        
        KeyManagerImpl kmgr = (KeyManagerImpl)(KeyManagerImpl.makeKeyManagerBasedOnNewKeys(NUMBER_OF_KEYS));
        kmgr.writeToFile(file);
        
        KeyManagerImpl kmgr2 = (KeyManagerImpl)(KeyManagerImpl.makeKeyManagerBasedOnStoredKeys(new ReferencedStuff(file)));
        kmgr2.writeToFile(file2);
        
    }
}
