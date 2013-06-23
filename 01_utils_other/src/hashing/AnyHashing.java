package hashing;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;

import com.mplify.checkers.Check;
import com.mplify.helpers.ByteCoding;
import com.mplify.helpers.ReaderToString;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A functionality to do hashing. Functions of this class have to be
 * multithread-capable as there will be only one "canonical instance" of
 * an actual AnyHashing subclass.
 * 
 * 2011.06.23 - Created based on existing SHA1Hashing, modified and made into
 *              a subclass of this.
 ******************************************************************************/
 
public abstract class AnyHashing {

    private final String algoName;
    
    public AnyHashing(String algoName) {
        Check.notNullAndNotOnlyWhitespace(algoName, "algo name");
        this.algoName = algoName;
    }
    
    /**
     * Simple hash for rather short strings
     */
    
    public byte[] hashWithoutBuffer(String input) {
        Check.notNull(input,"input string");
        try {
            return MessageDigest.getInstance(algoName).digest(input.getBytes("UTF-8"));
        } catch (Exception exe) {
            Check.cannotHappen(exe);
            throw new Error(Check.NEVER_GET_HERE_BUT_NEEDED_FOR_KEEPING_COMPILER_HAPPY);
        }        
    }
    
    /**
     * Simple hash for byte strings
     */
    
    public byte[] hashWithoutBuffer(byte[] input) {
        Check.notNull(input,"input string");
        try {
            // TODO: Keep MessageDigest instances in a pool
            return MessageDigest.getInstance(algoName).digest(input);
        } catch (Exception exe) {
            Check.cannotHappen(exe);
            throw new Error(Check.NEVER_GET_HERE_BUT_NEEDED_FOR_KEEPING_COMPILER_HAPPY);
        }        
    }
    
    /**
     * Hash, then encode to hex and return the hex string
     */
    
    public String hashToHexWithoutBuffer(String input) {
        return ByteCoding.toHexString(hashWithoutBuffer(input));
    }
    
    /**
     * Hash, then encode to hex and return the hex string
     */
    
    public String hashToHexWithoutBuffer(byte[] input) {
        return ByteCoding.toHexString(hashWithoutBuffer(input));
    }

    /**
     * Hash, then encode to base64 and return the ASCII string
     */
    
    public String hashToBase64(String input) throws IOException {
        byte[] base64 = Base64.encodeBase64(hashWithoutBuffer(input));
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        Reader reader = new InputStreamReader(bais, "ASCII");
        try {
            return ReaderToString.slurp(reader,1024);
        } finally {
            reader.close(); // also closes "bais"
        }
    }
}
