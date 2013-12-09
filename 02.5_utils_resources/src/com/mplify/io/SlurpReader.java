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
 * Copy from a "reader" into a "string" until EOF is reached.
 * Basically "slurp" the contents from the reader.
 * 
 * In Groovy, one would write:
 * 
 *    String fileContents = new File('/path/to/file').getText('UTF-8')
 * 
 * But see also:
 * 
 *    http://stackoverflow.com/questions/326390/how-to-create-a-java-string-from-the-contents-of-a-file
 * 
 * 2011.06.24 - Created by collecting common code
 ******************************************************************************/

public class SlurpReader {
    
    private SlurpReader() {
        // NOP - cannot be instantiated
    }
    
    public static String slurp(Reader reader, int bufferSize) throws IOException {
        Check.notNull(reader,"reader");
        Check.largerThanZero(bufferSize, "buffer size");
        StringBuilder res = new StringBuilder();
        // The char[] buffer should be allocated from a pool if this is used often... maybe
        char[] buffer = new char[bufferSize]; 
        int n;
        while ((n = reader.read(buffer)) >= 0) {
            res.append(buffer, 0, n);
        }
        return res.toString();
    }
}
