package com.mplify.crypto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * All the data needed for encryption, includes the secret (symmetric) key
 * 
 * 2011.12.30 - Redesigned a second time
 ******************************************************************************/

public class EncryptionInfo {

    private final static String CLASS = EncryptionInfo.class.getName();
    private final static Logger LOGGER_init = LoggerFactory.getLogger(CLASS + ".<init>");

    private final EncryptionStatus encryptionStatus; // NOT NULL
    private final SymmetricKeyInfo symKeyInfo; // Null exactly if status is "not encrypted"

    /**
     * Constructor can take null symmetricKeyInfo exactly if encryptionStatus is non-null
     */
    
    public EncryptionInfo(EncryptionStatus es, SymmetricKeyInfo symKeyInfo) {
        _check.notNull(es, "encryption status");
        this.encryptionStatus = es;
        SymmetricKeyInfo setTo = null;
        switch (es) {
        case ENCRYPTED:
            _check.isTrue(symKeyInfo != null, "Encryption status is %s but the symmetric key info is (null)", es);
            setTo = symKeyInfo;
            break;
        case UNENCRYPTED:
            if (symKeyInfo != null) {
                LOGGER_init.warn("Encryption status is %s but the symmetric key info is not (null)", es);
            }
            break;
        }
        this.symKeyInfo = setTo;
    }

    /**
     * A call that returns null if the encryptionStatus is UNENCRYPTED and the SymmetricKey otherwise
     */
    
    public SymmetricKey getSymKey() {
        if (symKeyInfo!=null) {
            return symKeyInfo.symKey;
        }
        else {
            return null;
        }
    }

    /**
     * A call that returns null if the encryptionStatus is UNENCRYPTED and the SymmetricKeyIndex otherwise
     */
    
    public Integer getSymKeyIndex() {
        if (symKeyInfo!=null) {
            return Integer.valueOf(symKeyInfo.symKeyIndex);
        }
        else {
            return null;
        }
    }

    /**
     * Never returns null
     */
    
    public EncryptionStatus getEncryptionStatus() {
        return encryptionStatus;
    }

    /**
     * Returns null iff encryption status is UNENCRYPTED
     */

    public SymmetricKeyInfo getSymKeyInfo() {
        return symKeyInfo;
    }
    
    
}
