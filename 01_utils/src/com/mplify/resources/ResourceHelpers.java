package com.mplify.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers.Check;
import com.mplify.io.ReaderToString;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Various stuff for JUnit and others
 * 
 * 2005.05.15 - Created for ILR project.
 * 2005.05.30 - Too useful to keep in in package JUnit - move to core.utils
 * 2007.03.15 - Moved exentListOfMap() to here
 * 2008.10.15 - Renamed from Helpers to ResourceHelpers
 * 2008.11.23 - Added "doesResourceExist()" and another signature for
 *              "getStreamFromResource()" 
 * 2009.03.17 - Added a makeFullyQualifiedResourceName() which takes a
 *              String as package name
 * 2010.09.01 - Review of some calls, some code removed.
 * 2011.05.17 - Updated utf8byteStreamToString(), utf8byteArrayToString()
 * 2011.06.14 - Introduced Check
 * 2011.06.16 - Slurping reviewed and made clearer
 * 2011.07.22 - loadStringFromResource() renamed to slurpResource()
 * 2011.09.07 - Added slurpBytesAndClose().
 * 2011.09.07 - Closing of streams made safe
 * 2011.12.07 - Removed use of BufferedInputStreams
 * 2012.12.28 - Added resource cleanups that the compiler complains about
 * 2013.01.24 - Some code clarifications
 ******************************************************************************/

public class ResourceHelpers {

    private final static String CLASS = ResourceHelpers.class.getName();
    private final static Logger LOGGER_launder = LoggerFactory.getLogger(CLASS + ".launder");
    private final static Logger LOGGER_getStreamFromResource = LoggerFactory.getLogger(CLASS + ".getStreamFromResource");
    private final static Logger LOGGER_existsResource = LoggerFactory.getLogger(CLASS + ".existsResource");
    private final static Logger LOGGER_slurpBytesAndClose = LoggerFactory.getLogger(CLASS + ".slurpBytesAndClose");
    private final static Logger LOGGER_slurpStreamAndClose = LoggerFactory.getLogger(CLASS + ".slurpStreamAndClose");
    private final static Logger LOGGER_slurpFile = LoggerFactory.getLogger(CLASS + ".slurpFile");
    private final static Logger LOGGER_slurpResource = LoggerFactory.getLogger(CLASS + ".slurpResource");

    private final static int SLURP_BUFFER_SIZE = 1024;
   
    /**
     * Unreachable constructor
     */

    private ResourceHelpers() {
        // Unreachable constructor
    }

    /**
     * Read a String from a Resource given as a "hook class" (possibly null) which is supposed to reside in the same
     * package as the resource and a non-qualified resource name. Throws if the resource could not be found.
     * A non-null encoding must be passed. 
     */

    static public String slurpResource(Class<?> hookClass, String nonQualifiedResourceName, String encoding) throws IOException {
        String fqResourceName = fullyQualifyResourceName(hookClass, nonQualifiedResourceName); 
        return slurpResource(fqResourceName, encoding);
    }

    /**
     * Read a String from a Resource given as a "package" (possibly null) and a non-qualified resource name. 
     * Throws if the resource could not be found.
     * A non-null encoding must be passed. 
     */

    static public String slurpResource(Package myPackage, String nonQualifiedResourceName, String encoding) throws IOException {
        String fqResourceName = fullyQualifyResourceName(myPackage, nonQualifiedResourceName);        
        return slurpResource(fqResourceName, encoding);
    }

    /**
     * Read a String from a Resource given as a fully qualified resource name. 
     * Throws if the resource could not be found.
     * A non-null encoding must be passed. 
     */

//    @SuppressWarnings("resource")
    static public String slurpResource(String fullyQualifiedResourceName, String encoding) throws IOException {
        Logger logger = LOGGER_slurpResource;
        Check.notNull(fullyQualifiedResourceName,"fullyQualifiedResourceName");
        Check.notNull(encoding,"encoding");        
        InputStream is = getStreamFromResource(fullyQualifiedResourceName);
        try {
            int maxChars = Integer.MAX_VALUE;
            return slurpStreamAndClose(is, encoding, maxChars);
        } finally {
            assert is != null;
            try {
                is.close();
            } catch (Exception ignore) {
                logger.warn("While closing input stream -- ignoring this!", ignore);
            }
        }
    }

    /**
     * Slurp text from a stream
     */
    
//    @SuppressWarnings("resource")
    static public String slurpStreamAndClose(InputStream is, String encoding, int maxChars) throws IOException {
        Logger logger = LOGGER_slurpStreamAndClose;
        Check.notNull(is, "input stream");
        Check.notNull(encoding, "encoding");
        Reader reader = new InputStreamReader(is, encoding);
        try {
            StringBuilder buf = new StringBuilder();
            char[] cbuf = new char[SLURP_BUFFER_SIZE];
            int count;
            while ((count = reader.read(cbuf)) >= 0 && buf.length() < maxChars) {
                buf.append(cbuf, 0, count);
            }
            assert count < 0;
            return buf.toString();
        } finally {
            assert reader != null;
            try {
                reader.close();
            } catch (Exception ignore) {
                logger.warn("While closing reader -- ignoring this!", ignore);
            }
        }
    }

    /**
     * Slurp bytes from a stream. Pass Integer.MAX_VALUE for "maxBytes" if you don't care about those.
     * 0 or negative maxBytes will result in nothing read!
     */

    static public byte[] slurpBytesAndClose(InputStream is, int maxBytes) throws IOException {
        Logger logger = LOGGER_slurpBytesAndClose;
        Check.notNull(is, "input stream");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bbuf = new byte[1024];
            int count;
            while ((count = is.read(bbuf)) >= 0 && baos.size() < maxBytes) {
                baos.write(bbuf, 0, count);
            }
            assert count < 0;
            return baos.toByteArray();
        } finally {
            try {
                is.close();
            } catch (Exception ignore) {
                logger.warn("While closing input stream -- ignoring this!", ignore);
            }
        }
    }

    /**
     * Slurp text from a file. A non-null encoding must be passed!
     */

//    @SuppressWarnings("resource")
    public static String slurpFile(File file, String encoding) throws IOException {
        Logger logger = LOGGER_slurpFile;
        Check.notNull(file, "file");
        Check.notNull(encoding, "encoding");
        Reader reader = new InputStreamReader(new FileInputStream(file), encoding);
        try {
            return ReaderToString.slurp(reader, 1024);
        } finally {
            try {
                reader.close();
            } catch (Exception ignore) {
                logger.warn("While closing reader -- ignoring this!", ignore);
            }
        }
    }

    /**
     * Get a binary input stream from a resource. Throws IllegalArgumentException if the resource could not be found. Never returns null.
     */

    static public InputStream getStreamFromResource(String fullyQualifiedResourceName) {
        Logger logger = LOGGER_getStreamFromResource;
        Check.notNullAndNotOnlyWhitespace(fullyQualifiedResourceName,"fullyQualifiedResourceName");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        //
        // the following call returns (null) if resource not found (instead of throwing)
        //
        InputStream is = classLoader.getResourceAsStream(fullyQualifiedResourceName.trim());
        //
        // Thus we check...
        //
        Check.isTrue(is != null, "Could not find (fully qualified) resource named '%s'", fullyQualifiedResourceName);
        //
        // If we are here, loading the resource was successful!
        //
        if (logger.isInfoEnabled()) {
            URL url = classLoader.getResource(fullyQualifiedResourceName);
            logger.info("Resource named '" + fullyQualifiedResourceName + "' accessed as '" + url + "'");
        }
        return is;
    }

    /**
     * Check whether a fully qualified resource can be found through the current Thread's classloader
     */

//    @SuppressWarnings("resource")
    static public boolean existsResource(String fullyQualifiedResourceName) {
        Logger logger = LOGGER_existsResource;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream is = classLoader.getResourceAsStream(fullyQualifiedResourceName.trim());
        // is is null if the resource could not be found...
        if (is != null) {
            try {
                is.close();
            } catch (Exception exe) {
                logger.warn("While closing stream obtained from resource '" + fullyQualifiedResourceName + "'", exe);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check whether a resource can be found. If "myPackage" is null, the default package is assumed.
     */

    static public boolean existsResource(Package myPackage, String nonQualifiedResourceName) {
        String fqResourceName = fullyQualifyResourceName(myPackage, nonQualifiedResourceName);
        Check.isTrue(fqResourceName != null, "The fully qualified resource name is (null)");
        return existsResource(fqResourceName);
    }

    /**
     * Check whether a resource can be found. If "myPackageStr" is null, the default package is assumed.
     */

    static public boolean existsResource(String myPackageStr, String nonQualifiedResourceName) {
        String fqResourceName = fullyQualifyResourceName(myPackageStr, nonQualifiedResourceName);
        Check.isTrue(fqResourceName != null, "The fully qualified resource name is (null)");
        return existsResource(fqResourceName);
    }

    /**
     * Helper: Getting a stream from a **mail** resource. Makes sure there is only ASCII in there and all lines are
     * correctly terminated with CR+LF. Needs to buffer the Stream though... The 'fullyQualifiedResourceName' is
     * trimmed! Is this right? It is in some cases, e.g. when too much whitespace comes in from the properties...
     */

//    @SuppressWarnings("resource")
    static public InputStream getMailStreamFromResource(String fullyQualifiedResourceName) throws IOException {
        Logger logger = LoggerFactory.getLogger(CLASS + ".getMailStreamFromResource");
        InputStream is = getStreamFromResource(fullyQualifiedResourceName);
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            {
                byte[] bytebuf = new byte[512];
                int count = is.read(bytebuf);
                while (count > 0) {
                    launder(count, bytebuf, bos);
                    count = is.read(bytebuf);
                }
            }
            return new ByteArrayInputStream(bos.toByteArray());
        } finally {
            try {
                is.close();
            } catch (Exception exe) {
                logger.warn("While closing stream obtained from resource '" + fullyQualifiedResourceName + "'", exe);
            }
        }
    }

    /**
     * Examine 'count' bytes in 'array', making sure that they are only ASCII (warn if not) and transforming line ends:
     * any LF -> CRLF any CR is suppressed This is a good heuristic and avois problems due to CR or LF at the buffer
     * start/end
     */

    static private void launder(int count, byte[] array, ByteArrayOutputStream bos) {
        Logger logger = LOGGER_launder;
        for (int i = 0; i < count; i++) {
            if ((array[i] & 0x80) != 0) {
                logger.warn("Non-ASCII value " + array[i] + " suppressed");
            } else if (array[i] == '\n') {
                bos.write('\r');
                bos.write('\n');
            } else if (array[i] == '\r') {
                // NOP
            } else {
                bos.write(array[i]);
            }
        }
    }

    /**
     * Helper function to stringify an UTF-8 byte array (i.e. the array contains UTF-8 bytes, and the output is a normal String)
     */

    public static String utf8byteArrayToString(byte[] byteArray) throws IOException {
        Check.notNull(byteArray, "byte array");
        return new String(byteArray, "UTF-8");
    }

    /**
     * Helper function to stringify an UTF-8 byte stream (i.e. the stream yields UTF-8 bytes, and the output is a normal String)
     * The input stream is not internally buffered, so depending on where it is coming from, you may want to buffer it externally.
     */

    public static String utf8byteStreamToString(InputStream is) throws IOException {
        Check.notNull(is, "input stream");
        InputStreamReader isr = new InputStreamReader(is, "UTF-8"); // not buffered
        StringBuilder buf = new StringBuilder();
        char[] buffer = new char[256];
        int actuallyRead = 0;
        while ((actuallyRead = isr.read(buffer)) == buffer.length) {
            buf.append(buffer);
        }
        if (actuallyRead > 0) {
            buf.append(buffer, 0, actuallyRead);
        }
        return buf.toString();
    }

    /**
     * This qualifies the resource name by the package of the Class "clazz". 
     * If "clazz" is null, default package is assumed. 
     * If the "nonQualifiedResourceNameIn" is null, null will be returned.
     */

    public static String fullyQualifyResourceName(Class<?> clazz, String nonQualifiedResourceNameIn) {
        Package myPackage = (clazz != null) ? clazz.getPackage() : null;
        return fullyQualifyResourceName(myPackage, nonQualifiedResourceNameIn);
    }

    /**
     * This qualifies the resource name by the package of the Package "myPackage". 
     * If "myPackage" is null, default package is assumed. 
     * If the "nonQualifiedResourceNameIn" is null, null will be returned.
     */

    static public String fullyQualifyResourceName(Package myPackage, String nonQualifiedResourceNameIn) {
        String myPackageString = (myPackage != null) ? myPackage.getName() : null;
        return fullyQualifyResourceName(myPackageString, nonQualifiedResourceNameIn);
    }

    /**
     * This qualifies the resource name by the package given as String, whereby "." are replaced by "/" 
     * If "myPackage" is null, default package is assumed. 
     * If the "nonQualifiedResourceNameIn" is null, null will be returned.
     */

    static public String fullyQualifyResourceName(String myPackageStr, String nonQualifiedResourceNameIn) {
        if (nonQualifiedResourceNameIn == null) {
            return null;
        } else {
            String nonQualifiedResourceName = nonQualifiedResourceNameIn.trim();
            if (nonQualifiedResourceName.isEmpty()) {
                throw new IllegalArgumentException("The passed 'non-qualified resource name' is empty");
            }
            if (myPackageStr != null) {
                String packagePath = myPackageStr.replace('.', '/');
                String qualifiedResourceName = packagePath + "/" + nonQualifiedResourceName;
                return qualifiedResourceName;
            } else {
                return nonQualifiedResourceName;
            }
        }
    }
}
