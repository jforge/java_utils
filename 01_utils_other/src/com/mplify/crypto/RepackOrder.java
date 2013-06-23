package com.mplify.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers._check;
import com.mplify.stringmangling.CompressBehaviour;
import com.mplify.stringmangling.EncryptBehaviour;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * All the data needed for actual "repacking" of a record...
 * 
 * Currently there is no indication about what algorithm to use.
 * 
 * 2011.12.30 - Redesigned a second time
 * 2012.12.28 - Throwing Error after _check.cannotHappen() to keep compiler 
 *              happy
 ******************************************************************************/

public class RepackOrder {

    private final static String CLASS = RepackOrder.class.getName();
    private final static Logger LOGGER_init = LoggerFactory.getLogger(CLASS + ".<init>");

    public final CompressBehaviour compressBehaviour; // compress? never null
    public final EncryptBehaviour encryptBehaviour; // encrypt? never null
    public final SymmetricKeyInfo symKeyInfo; // if encryption demanded, this is not null

    /**
     * Create a "repack order" that does not do encryption
     */

    public RepackOrder(CompressBehaviour compressBehaviour) {
        this(compressBehaviour, EncryptBehaviour.DONT_ENCRYPT, null);
    }

    /**
     * Create a "repack order".
     */

    public RepackOrder(CompressBehaviour compressBehaviour, EncryptBehaviour encryptBehaviour, SymmetricKeyInfo symKeyInfo) {
        _check.notNull(compressBehaviour, "compressBehaviour");
        _check.notNull(encryptBehaviour, "encryptBehaviour");
        this.compressBehaviour = compressBehaviour;
        this.encryptBehaviour = encryptBehaviour;
        if (encryptBehaviour == EncryptBehaviour.DO_ENCRYPT) {
            _check.notNull(symKeyInfo, "symmetric key info");
            this.symKeyInfo = symKeyInfo;
        } else if (encryptBehaviour == EncryptBehaviour.DONT_ENCRYPT) {
            if (symKeyInfo != null) {
                LOGGER_init.warn("Encrypt behaviour is " + encryptBehaviour + " but a non-null symmetric key info has been passed; discarding it!");
            }
            this.symKeyInfo = null;
        } else {
            _check.cannotHappen("Unknown encrypt behavior " + encryptBehaviour); 
            throw new Error(_check.NEVER_GET_HERE_BUT_NEEDED_FOR_KEEPING_COMPILER_HAPPY);
        }
    }
}