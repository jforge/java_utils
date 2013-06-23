package hashing;



/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A functionality to do MD5 hashing.
 * 
 * MD5 hashes are considered weak!
 * 
 * 2011.06.23 - Created as subclass of AnyHashing
 ******************************************************************************/
 
public class MD5Hashing extends AnyHashing {

    /**
     * Canonical instance to be used by all
     */
    
    public final static MD5Hashing CI = new MD5Hashing();
    
    /**
     * Ureachable constructor
     */
    
    private MD5Hashing() {
        super("MD5");
    }
    
}