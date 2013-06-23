package com.mplify.properties;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Helper class for reading properties. Package visibility should be enough
 * 
 * 2010.11.02 - Create during refactoring of PropertiesReader
 ******************************************************************************/

class FileSource {

    private final static String CLASS = FileSource.class.getName();
    private final static Logger LOGGER_readFromFile = LoggerFactory.getLogger(CLASS + ".readFromFile");
    private final static Logger LOGGER_checkPropertiesFileWithoutException = LoggerFactory.getLogger(CLASS + ".checkPropertiesFileWithoutException");
//    private final static Logger LOGGER_refreshFromFile = LoggerFactory.getLogger(CLASS + ".refreshFromFile");

    private final File propsFile; // the file holding the properties , not null
    private Date lastModified = new Date(0); // last modification date of the properties, not null
    private final String charsetName; // encoding of the propsFile

    /**
     * Constructor..
     */

    public FileSource(File propsFile, String charsetName) {
        assert propsFile != null;
        this.propsFile = propsFile;
        this.charsetName = (charsetName == null) ? "UTF-8" : charsetName;
    }

    /**
     * Constructor..
     */

    public FileSource(String propsFileName, String charsetName) {
        assert propsFileName != null;
        this.propsFile = new File(propsFileName);
        this.charsetName = (charsetName == null) ? "UTF-8" : charsetName;
    }

    /**
     * Check for file presence & readibility, throwing an exception if there is trouble.
     */

    private void checkPropertiesFileWithException() throws Exception {
        if (!(propsFile.exists() && propsFile.isFile() && propsFile.canRead())) {
            String aPath = propsFile.getAbsolutePath();
            throw new IllegalArgumentException("Properties file '" + aPath + "' does not exist or is unreadable");
        }
    }

    /**
     * Check for file presence & readibility, return false if there is trouble.
     */

    @SuppressWarnings("unused")
    private boolean checkPropertiesFileWithoutException() {
        Logger logger = LOGGER_checkPropertiesFileWithoutException;
        String aPath = propsFile.getAbsolutePath();
        if (!propsFile.exists()) {
            logger.error("Properties file '" + aPath + "' does not exist");
            return false;
        }
        if (!propsFile.isFile()) {
            logger.error("Properties file '" + aPath + "' is not a file");
            return false;
        }
        if (!propsFile.canRead()) {
            logger.error("Properties file '" + aPath + "' is unreadable");
            return false;
        }
        return true;
    }

    /**
     * Read in the properties file and parse it. If there is a problem reading the properties file, an exception is
     * thrown, in which case the existing 'properties' (if any) have *not* been changed. If reading the properties file
     * was successful, the stored Properties and the 'last modified' timestamp have been updated. This function does not
     * modifiy the properties in the Hashtable, only the 'props' member, i.e. the Properties instance that actually
     * holds the stuff read in.
     */

    private Properties readFromFile() throws IOException {
        Logger logger = LOGGER_readFromFile;
        Properties props = new Properties();
        Date tmp_lastModified = new Date(propsFile.lastModified());
        FileInputStream fis = new FileInputStream(propsFile);
        String aPath = propsFile.getAbsolutePath();
        try {
            logger.info("Now reading properties from file '" + aPath + "'");
            BufferedInputStream bis = new BufferedInputStream(fis);
            InputStreamReader reader = new InputStreamReader(bis, charsetName);
            props.load(reader);
            logger.info("Done reading properties from file '" + aPath + "'");
            // success reading; on problem an exception would have been thrown
            this.lastModified = tmp_lastModified;
            return props;
        } finally {
            fis.close();
        }
    }

    /**
     * Check whether the properties file has been updated & if so, re-read it in full. This may be called from a main
     * loop. Returns the properties if they were, indeed, refreshed. If there was trouble reading the file, returns
     * false or throws an Exception - depending on the 'throwOnProblem' flag. If the propsFile is null, returns 'false'
     * immediately.
     */

    public Properties refreshFromFile() throws Exception {
//        Logger logger = LOGGER_refreshFromFile;
        //
        // first check to see whether file is good
        //
        checkPropertiesFileWithException();
        //
        // load only if needed
        //
        Date cmp_lastModified = new Date(propsFile.lastModified());
        if (cmp_lastModified.after(lastModified)) {
            Properties props = readFromFile();
            // everything worked
            this.lastModified = cmp_lastModified;
            return props;
        } else {
            // nothing to do yet
            return null;
        }
    }
}
