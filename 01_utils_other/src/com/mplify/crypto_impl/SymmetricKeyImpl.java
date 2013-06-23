package com.mplify.crypto_impl;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.mplify.checkers._check;
import com.mplify.crypto.SymmetricKey;
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
 * Simple facility for AES encrypt/decrypt
 * 
 * TODO: Make sure this becomes AES 256 CBC
 * 
 * 2011.06.15 - Created for tests
 * 2011.06.16 - Good enough for non-tests
 * 2011.11.08 - Removed the "key index"
 ******************************************************************************/

public class SymmetricKeyImpl implements SymmetricKey {

    // Possible key sizes for AES. Default Sun implementation only accepts 128 bit....
    
    public static enum KEY_SIZE {
        KEY_128, KEY_192, KEY_256
    }

    // The SecretKeySpec is basically the key binary array, as provided by javax.crypto
    
    private final SecretKeySpec key;
    
    /**
     * Constructor
     */
    
    public SymmetricKeyImpl(SecretKeySpec key) {
        _check.notNull(key,"key");
        this.key = key;
    }

    /**
     * Create an AES key generator
     * See: http://download.oracle.com/javase/6/docs/api/javax/crypto/KeyGenerator.html
     */

    public static KeyGenerator createAESKeyGenerator(KEY_SIZE keySize) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        switch (keySize) {
        case KEY_128:
            keyGen.init(128);
            break;
        case KEY_192:
            keyGen.init(192);
            break;
        case KEY_256:
            keyGen.init(256);
            break;
        default:
            _check.cannotHappen("Unknown key size " + keySize);
        }
        return keyGen;
    }

    /**
     * Create secret key spec based on a Key Generator
     */

    public static SecretKeySpec newKey(KeyGenerator keyGen) {
        _check.notNull(keyGen,"key generator");
        SecretKey skey = keyGen.generateKey();
        return new SecretKeySpec(skey.getEncoded(), keyGen.getAlgorithm());
    }
    
    /**
     * Create a Secret Key Spec matching the expectations of this implementation (AES 128 bit)
     */
    
    @SuppressWarnings("boxing")
    public static SecretKeySpec newAES128Key(byte[] raw) {
        _check.notNull(raw,"raw byte array");
        _check.isTrue(raw.length * 8 == 128, "The 'raw byte array' must be of length 128 bit; it is %s", raw.length * 8);
        return new SecretKeySpec(raw, "AES");
    }

    /** 
     * Encrypt an array of byte using AES 128
     */

    @Override
    public byte[] encrypt(byte[] data) throws EncryptException {
        _check.notNull(data,"data array");
        try {
            Cipher cipher = Cipher.getInstance("AES"); // Todo: use Cipher pool
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(data); // at this point, the Cipher can be reused!!
            return encrypted;
        } catch (Exception exe) {
            throw new EncryptException(exe);
        }
    }

    /**
     * Decrypt an array of byte using AES 128
     */

    @Override
    public byte[] decrypt(byte[] data) throws DecryptException {
        _check.notNull(data,"data array");
        try {
            Cipher cipher = Cipher.getInstance("AES"); // Todo: use Cipher pool
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decrypted = cipher.doFinal(data); // at this point, the Cipher can be reused!!
            return decrypted;
        } catch (Exception exe) {
            throw new DecryptException(exe);
        }
    }

    /**
     * Get the underlying bytes
     */
    
    public byte[] getEncoded() {
        return this.key.getEncoded();
    }
}
