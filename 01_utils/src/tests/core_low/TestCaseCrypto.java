package tests.core_low;

import static org.junit.Assert.fail;

import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.crypto.SymmetricKey;
import com.mplify.crypto_impl.SymmetricKeyImpl;
import com.mplify.crypto_impl.SymmetricKeyImpl.KEY_SIZE;
import com.mplify.helpers.ByteCoding;
import com.mplify.junit.HelperForTestCases;
/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Testing cryptography
 * 
 *******************************************************************************/
import com.mplify.junit.TestStarter;

//@SuppressWarnings("static-method")
public class TestCaseCrypto extends TestStarter {

    private final static String CLASS = TestCaseCrypto.class.getName();

    @Test
    public void testVariousKeySizes() throws Exception {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testVariousKeySizes");
        KEY_SIZE[] keySizes = new KEY_SIZE[3];
        keySizes[0] = KEY_SIZE.KEY_128;
        keySizes[1] = KEY_SIZE.KEY_192;
        keySizes[2] = KEY_SIZE.KEY_256;
        // getting various-sized keys should not yield errors
        for (int i = 0; i < keySizes.length; i++) {
            SecretKeySpec sks = SymmetricKeyImpl.newKey(SymmetricKeyImpl.createAESKeyGenerator(keySizes[i]));
            byte[] raw = sks.getEncoded();
            String hex = ByteCoding.toHexString(raw);
            logger.info(keySizes[i] + ": " + hex + " - length = " + hex.length());
        }
    }

    private void _testFailureXBit(KEY_SIZE size) {
        Logger logger = LoggerFactory.getLogger(CLASS + "._testFailureXBit");
        try {
            SecretKeySpec sks = SymmetricKeyImpl.newKey(SymmetricKeyImpl.createAESKeyGenerator(size));
            SymmetricKey crypto = new SymmetricKeyImpl(sks);
            byte[] input = HelperForTestCases.createFullyRandomByteStream(100);
            byte[] output = crypto.encrypt(input);
            fail("Expected failure at " + size);
        } catch (Exception exe) {
            logger.info("Failure at " + size, exe);
        }
    }

    @Test
    public void testFailure192Bit() {
        _testFailureXBit(KEY_SIZE.KEY_192);
    }

    @Test
    public void testFailure256Bit() {
        _testFailureXBit(KEY_SIZE.KEY_256);
    }

    @Test
    public void testCryptUncrypt() throws Exception {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testCryptUncrypt");
        // Default Java Provider only knows 128-bit AES
        SecretKeySpec sks = SymmetricKeyImpl.newKey(SymmetricKeyImpl.createAESKeyGenerator(KEY_SIZE.KEY_128));
        SymmetricKey crypto = new SymmetricKeyImpl(sks);
        for (int i = 0; i < 10000; i++) {
            byte[] input = HelperForTestCases.createFullyRandomByteStream(i);
            byte[] output = crypto.encrypt(input);
            byte[] reinput = crypto.decrypt(output);
            logger.info("Input: " + input.length + ", output: " + output.length + ", delta: " + (output.length - input.length));
            HelperForTestCases.compareByteArrays(input, reinput, logger);
        }
    }
}
