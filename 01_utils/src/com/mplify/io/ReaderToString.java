package com.mplify.io;

import java.io.IOException;
import java.io.Reader;

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
 * Copy from a reader into a string until EOL is reached. Be sure that EOL is
 * indeed assured to be reached. Basically, "slurp"
 * 
 * TODO: Fuse with StringMangler and ResourceHelpers functions....
 * 
 * 2011.06.24 - Created by collecting common code
 ******************************************************************************/

public class ReaderToString {

    /**
     * Cannot be instantiated
     */
    
    private ReaderToString() {
        // NOP
    }
    
    /**
     * Helper. Same function as in StringMangler. One might want to fuse...
     */

    public static String slurp(Reader reader, int bufferSize) throws IOException {
        Check.notNull(reader,"reader");
        Check.largerThanZero(bufferSize,"buffer size");
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[bufferSize]; // TODO: Allocate from pool
        int n;
        while ((n = reader.read(buffer)) >= 0) {
            sb.append(buffer, 0, n);
        }
        return sb.toString();
    }
}
