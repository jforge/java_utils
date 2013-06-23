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
 * A functionality to do SHA1 hashing
 * 
 * 2011.06.06 - Created
 * 2011.06.26 - Actual code moved to AnyHashing
 ******************************************************************************/
 
public class SHA1Hashing extends AnyHashing {

    /**
     * Canonical instance to be used by all
     */
    
    public final static SHA1Hashing CI = new SHA1Hashing();
    
    /**
     * Ureachable constructor
     */
    
    private SHA1Hashing() {
        super("SHA-1");
    }
}