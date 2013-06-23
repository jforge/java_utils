package com.mplify.properties;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers.Check;
import com.mplify.linuxaria.GetAsPort;
import com.mplify.parsing.Parsing;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * PropertiesReader allows to read configuration information from a file whose
 * name is passed at construction time and to present the configuration info
 * to another class. PropertiesReader is used by both server and client modules
 * and may be sublcassed appropriately for the definition of the actual key
 * values. It is largely based on java.util.Properties with some additional
 * checks added.
 *
 * Some Saturday Night Special:
 *
 * 1) If you do have not 'real' properties but constants used by client and
 *    server that do not need to be read from a properties list, set them up as
 *    static values in subclasses and override the getter methods appropriately.
 *
 * 2) The idea is to read properties from a file at startup. However, suppose
 *    the user wants to change some properties later on - while the program runs.
 *    You can test the modification date and reload the properties file with a
 *    call to refreshFromPropertiesFile() from inside a loop, thus giving 
 *    (limited) dynamic reconfiguration.
 *
 *    This is actually implemented as such:
 *
 *    1) A 'properties consumer' is interested in obtaining regular updates
 *       on the value of some of the properties
 *    2) The consumer registers itself using consumerAdd(String), where the
 *       String is the name of the consumer (best would be the FQN of the
 *       class)
 *    3) If the properties file is changed and the main thread calls
 *       refreshFromPropertiesFile() regularly, the change will be noticed:
 *       Inside of refreshFromPropertiesFile(), all the registered consumers
 *       are "flagged"
 *    4) The consumer synchronously (in its thread) regularly calls 
 *       consumerCheck(String) to see whether it has been flagged.
 *       The function will return 'true' after refreshFromPropertiesFile noticed
 *       the change in the properties file.
 *    5) The consumer fetched the values
 *    6) The consumer clears the flag by calling consumerReset().
 *
 *    This approach is the synchronous pendant to the asynchronous update through
 *    listeners.
 *    Advantage:    no headache through asynchronous updates
 *    Disadvantage: polling by consumers
 *
 * Notes:
 * 
 * - The constructor complains if the properties file does not exist or is
 *   unreadable. This may not necessarily be a good idea: in some cases, default
 *   values should be used instead. Is this a bug?
 * - Keys are LOWERCASE only. Keys read in are LOWERCASED. This is probably a
 *   bug. What should happen is that comparisons should be case-insensitive (i.e.
 *   there should be a canonical lowercased comparison value) but the actual
 *   key value, as retained internally, should keep the case.
 * - (null) cannot be used as key value, and cannot be returned as value.
 * 
 * 2000.12.12 - Created by Hobbes as SMSInterfaceProperties.java
 * 2001.01.02 - More additions; tested; works
 * 2001.01.02 - Added the 'use' parameter
 * 2001.01.07 - Added the 'smtppassword' parameter
 * 2001.01.08 - Moved to package 'properties' from 'tech' so that it can be used
 *              by the client, too
 * 2001.01.11 - Added definitions of 'STATISTICS_PORT' and 'WATCHDOG_PORT' here
 * 2001.01.11 - Server-side split off into SMSInterfacePropertiesOnServer
 * 2001.10.08 - Several modifications leads to the PropertiesReader class for
 *              the m-plify server and client units.
 * 2001.10.08 - 'get' made an abstract function of this class which becomes
 *              abstract
 * 2001.10.10 - Nearly all of the code earlier on found in subclasses is now
 *              here. Large reorganization but the code gives a better feeling.
 * 2001.10.12 - Added the concept of 'internal keys'
 * 2001.10.14 - Added 'existsKeyP()'
 * 2001.10.22 - Added 'getAsFloat()'
 * 2001.11.18 - Added the consumer functions that give the possibility to
 *              synchronously get new property values from other threads
 * 2002.01.18 - Added a function which returns a HashSet of all the
 *              properties that have a given root.
 * 2002.12.13 - Removed isInternalKey(), which is useless.
 * 2003.06.13 - Review. This code is really old. Maybe one should move to
 *              XML properties? Also, one should find some other model
 *              than the 'consumer registration' - what about callbacks?
 * 2003.07.02 - Added reading from /etc/services directly. 
 * 2004.06.29 - consumerCheck no longer throws if consumer is missing
 *              Added constructor takinhg stream in case one wants to
 *              initialize from a resource instead of a file
 * 2004.08.05 - Port scanning moved to Utilities
 * 2005.06.03 - Added parseToStringArray()
 * 2009.08.26 - Removed special handling for log4j (moved to 
 *              Log4JStarter)
 * 2010.08.05 - Added special functions to store/retrieve PoolFrontend
 *              and TupleSpaces
 * 2010.08.17 - Removed special functions to store/retrieve TupleSpaces as
 *              these this class and everything else becomes dependent on
 *              TupleSpaces implementation - not good. 
 * 2010.10.21 - Added retrieveInteger()
 * 2010.11.02 - Replaced readPropertiesFile() by readFromReader()
 *              Constructors rewritten and streamlined.
 * 2011.01.17 - getAsPort() now uses existsNonemptyKeyP()              
 *              Error messages with double quotes generally replaced with 
 *              single quotes.
 * 2011.09.05 - existsNonemptyKeyP() did not work for non-String values.
 *              Fixed.
 * 2012.12.10 - Code inspection reveals bug in condition, made a function static,
 *              correctly closed Reader.
 *                                          
 * TODO: Unclarity: Atomicity of change, erasing keys, what happens to
 *                  empty keys...
 ******************************************************************************/

public class PropertiesReader {

    private static final String CLASS = PropertiesReader.class.getName();
    private final static Logger LOGGER_consumerCheck = LoggerFactory.getLogger(CLASS + ".consumerCheck");
    private final static Logger LOGGER_readFromReader = LoggerFactory.getLogger(CLASS + ".readPropertiesFile");
//    private final static Logger LOGGER_readFromFile = LoggerFactory.getLogger(CLASS + ".readFromFile");
//    private final static Logger LOGGER_refreshFromPropertiesFile = LoggerFactory.getLogger(CLASS + ".refreshFromPropertiesFile");
    private final static Logger LOGGER_init = LoggerFactory.getLogger(CLASS + ".<init>");
//    private final static Logger LOGGER_checkPropertiesFileWithoutException = LoggerFactory.getLogger(CLASS + ".checkPropertiesFileWithoutException");
    private final static Logger LOGGER_initializeProperties = LoggerFactory.getLogger(CLASS + ".initializeProperties");
    private final static Logger LOGGER_refreshFromFile = LoggerFactory.getLogger(CLASS + ".refreshFromFile");
    
    private Hashtable<String,Integer> consumerFlags = new Hashtable<String,Integer>(); // consumers register themselves here
    private Properties props; // the actual properties, read from file or an input stream, not null after initialization    
    private FileSource fileSource; // information about the file used as properties source
    
    /**
     * Properties' default values, stored in a Hashtable, as well as the (higher-priority) overrides. On retrieval,
     * methods read first the overrides, then the properties and finally the defaults. A similar functionality is
     * provided by java.util.Properties using chained Properties instances.
     */

    private Hashtable<String,Object> overridesHash; // read this first
    private Hashtable<String,Object> propertiesHash; // ...then this
    private Hashtable<String,Object> defaultsHash; // hashtable containing 'default values'

    /**
     * Constructor does trivial initialization from a filename. Do not pass a null or empty filename! The constructor
     * complains if the properties file does not exist or is unreadable.
     */

    public PropertiesReader(String filename,String charsetNameIn) throws Exception {
//        Logger logger = LOGGER_init;
        Check.notNull(filename,"filename");
        Check.isTrue(!filename.trim().isEmpty(), "Passed filename is empty");
        this.fileSource = new FileSource(filename,charsetNameIn);
        this.props = this.fileSource.refreshFromFile();
        assert this.props != null : "Must have refreshed properties";
        defaultsHash = initializeDefaults(); // the 'defaultsHash' is set up by the (overriden) special function
        propertiesHash = initializeProperties(); // the 'propertiesHash' is set up from the 'props' read earlier
        overridesHash = initializeOverrides(); // the overrides are empty
    }

    /**
     * Constructor does trivial initialization from a File. Do not pass a null! The constructor
     * complains if the properties file does not exist or is unreadable.
     */

    public PropertiesReader(File file,String charsetNameIn) throws Exception {
//        Logger logger = LOGGER_init;
        Check.notNull(file,"file");
        this.fileSource = new FileSource(file,charsetNameIn);
        this.props = this.fileSource.refreshFromFile();
        assert this.props != null : "Must have refreshed properties";
        defaultsHash = initializeDefaults(); // the 'defaultsHash' is set up by the (overriden) special function
        propertiesHash = initializeProperties(); // the 'propertiesHash' is set up from the 'props' read earlier
        overridesHash = initializeOverrides(); // the overrides are empty
    }

    /**
     * Constructor does trivial initialization from an InputStream, generally from a resource. Do not pass (null).
     * The caller is responsible for closing the stream.
     */

    public PropertiesReader(InputStream stream,String charsetNameIn) throws Exception {
        Logger logger = LOGGER_init;
        Check.notNull(stream,"stream");
        String charsetName = charsetNameIn;
        if (charsetName==null) {
            logger.info("Charset unspecified, assuming UTF-8");
            charsetName = "UTF-8";
        }
        {
            Reader reader = new InputStreamReader(stream,charsetName);
            try {
                readFromReader(reader);
            }
            finally {
                reader.close();
            }
        }
        defaultsHash = initializeDefaults(); // the 'defaultsHash' is set up by the (overriden) special function
        propertiesHash = initializeProperties(); // the 'propertiesHash' is set up from the 'props' read earlier
        overridesHash = initializeOverrides(); // the overrides are empty        
    }

    /**
     * Constructor does trivial initialization from a Reader. Do not pass (null).
     * The caller is responsible for closing the reader.
     */

    public PropertiesReader(Reader reader) throws Exception {
//        Logger logger = LOGGER_init;
        Check.notNull(reader,"reader");
        readFromReader(reader);
        defaultsHash = initializeDefaults(); // the 'defaultsHash' is set up by the (overriden) special function
        propertiesHash = initializeProperties(); // the 'propertiesHash' is set up from the 'props' read earlier
        overridesHash = initializeOverrides(); // the overrides are empty
    }

    /**
     * Constructor does trivial initialization, setting up empty properties
     */

    public PropertiesReader() {
        props = new Properties(); // thus the properties read are empty but not null
        defaultsHash = initializeDefaults(); // the 'defaultsHash' is set up by the (overriden) special function
        propertiesHash = initializeProperties(); // the 'propertiesHash' is set up from the 'props' read earlier
        overridesHash = initializeOverrides(); // the overrides are empty
    }

    /**
     * Read unconditionally from a Reader (which gives out chars, as opposed to bytes)
     */

    private void readFromReader(Reader reader) throws Exception {
        Logger logger = LOGGER_readFromReader;
        assert reader!=null;
        //     
        // read properties into a temporary variable 
        // ...this *may* throw if the properties file has changed etc..
        //
        logger.info("Now reading properties from reader");
        Properties tmp_props = new Properties();
        tmp_props.load(reader);
        logger.info("Successfully read properties from reader");
        //
        // loading succeeded, assign variables for good
        //
        this.props = tmp_props;
    }

    /**
     * Fill in default values into the lowest-priority 'defaults' hashtable. Change this function whenever you want to
     * change a default by overriding it in subclasses. The default initializes an empty hash.
     */

    protected Hashtable<String,Object> initializeDefaults() {
        Hashtable<String,Object> newDefaultsHash = new Hashtable<String,Object>();
        return newDefaultsHash;
    }

    /**
     * Create properties out of the strings read in. We eschew checking the values here - its cumbersome and unnecessary
     * and removes the checking code from the place where checking should really take place - the constructors. Of
     * course you may get weirder program behaviour that way as a wrong value may take a long time until it's being used
     * but hey, you just can make sure that the values are correct in the first place. ** NOTE ** The keys are
     * lowercased and trimmed in here, so retrieval won't depend on case!!
     */

    private Hashtable<String,Object> initializeProperties() {
        Logger logger = LOGGER_initializeProperties;
        Hashtable<String,Object> newPropertiesHash = new Hashtable<String,Object>();
        Enumeration<?> iter = props.propertyNames();
        //
        // loop over all of the properties read
        //
        while (iter.hasMoreElements()) {
            //
            // extract key and lowercase key and value
            //
            String key = (String) (iter.nextElement());
            String lkey = key.toLowerCase(); // KILL CASE
            String value = props.getProperty(key).trim();
            //
            // empty key and empty value? Must have been an empty line
            //
            if (lkey.isEmpty() && value.isEmpty()) {
                continue;
            }
            //
            // ...otherwise its one of our own keys...
            // check the key (this is implemented in the subclasses)
            //
            if (!isValidKey(key)) {
                logger.warn("Property with invalid key '" + key + "'found");
                continue;
            }
            //
            // the stuff below never happens - duplicates are silently overwritten on
            // load of properties by Properties
            // if (newPropertiesHash.containsKey(lkey)) {
            //  logCat.warn("Property '" + lkey + "' already found earlier - overwriting it with '" + value + "'");
            // }
            //
            /*
             * if (logCat.isDebugEnabled()) { logCat.debug("Property '" + lkey + "' == '" + value + "'"); }
             */
            //
            // note that we always store under the lowercased key 
            //
            newPropertiesHash.put(lkey, value);
        }
        return newPropertiesHash;
    }

    /**
     * Set up the 'overrides' hashtable; this hashtable can be filled by calls to override()
     */

    private static Hashtable<String,Object> initializeOverrides() {
        Hashtable<String,Object> newOverridesHash = new Hashtable<String,Object>();
        return newOverridesHash;
    }

    /**
     * Register a consumer. No check is made to see if the consumer already exists. Thus calls to this function are
     * idempotent. The next call to consumerCheck() should return 'false' (except if another thread noticed that the
     * properties file changed and decided to flag the consumer again)
     */

    public void consumerAdd(String consumer) {
        consumerFlags.put(consumer, new Integer(0));
    }

    /**
     * Remove a consumer. No check is made to see if the consumer already exists.
     */

    public void consumerRemove(String consumer) {
        consumerFlags.remove(consumer);
    }

    /**
     * To check whether new properties should be loaded, a consumer calls this If the consumer has not been registered,
     * returns 'true' to do the update. Returns true if there might be new stuff to load (i.e. the properties file has
     * changed but maybe not in the values that interest the consumer) Calling this repeatedly after a long wait may
     * yield several 'trues' - as many 'trues' in fact as the thread reloading the properties file noticed the
     * properties file changing while the thread calling 'consumerCheck' was sleeping.
     */

    public boolean consumerCheck(String consumer) {
        Logger logger = LOGGER_consumerCheck;
        Integer res = consumerFlags.get(consumer);
        if (res == null) {
            if (logger.isInfoEnabled()) {
                // formerly we 'threw' here
                logger.info("Consumer '" + consumer + "' was not found");
            }
            return true;
        } else {
            return (res.intValue() > 0);
        }
    }

    /**
     * Check whether the properties file has been updated & if so, re-read it in full. This
     * may be called from a main loop. Returns true if the properties were, indeed, refreshed. If there was trouble
     * reading the file, throws an Exception. If the propsFile is null, returns 'false' immediately.
     */

    public boolean refreshFromFile() throws Exception {
        Logger logger = LOGGER_refreshFromFile; 
        if (this.fileSource == null) {
            logger.debug("Non-file properties; can't refresh");
            return false;
        }
        Properties props = this.fileSource.refreshFromFile();
        if (props!=null) {
            logger.debug("Refreshed from properties file");
            this.props = props;
            this.propertiesHash = initializeProperties();
            return true;
        }
        else {
            logger.debug("Not refreshed from properties file");
            return false;
        }
    }

    /**
     * Get a set of all the keys currently stored in the 'properties' that match the passed 'root' (i.e. whose
     * start-of-name matches the passed string) This operation traverses all of the properties and may be expensive. (It
     * should synchronizes on this class, too -- but this would mean synchronizing all the other methods; it would be
     * better to obtain a 'write lock'; if we have time...)
     */

    public Set<String> getKeysByRoot(String rootIn) {
        String root = rootIn;
        if (root == null) {
            throw new IllegalArgumentException("The passed 'root' is (null)");
        }
        root = root.trim().toLowerCase(); // 2006-02-23 lowercase for matching...ok?
        Set<String> res = new HashSet<String>();
        addKeysToSet(root, overridesHash.keySet(), res);
        addKeysToSet(root, propertiesHash.keySet(), res);
        addKeysToSet(root, defaultsHash.keySet(), res);
        return res;
    }

    /**
     * Helper functions to copy keys matching 'root' form 'setIn' to 'setOut'
     */

    private static void addKeysToSet(String root, Set<String> setIn, Set<String> setOut) {
        for (String key : setIn) {
            // no need to ignore case, everything is lowercase
            if (key.startsWith(root)) {
                setOut.add(key);
            }
        }
    }

    /**
     * Retrieve a value so that an overriden value is correctly retrieved If no value has been set up for the key,
     * (null) is returned. Furthermore, if the passed key is invalid, an Exception is thrown. This function does not
     * need synchronization.
     */

    private Object get(String key) {
        if (!isValidKey(key)) {
            // also tests for (null)
            throw new IllegalArgumentException("Illegal key '" + key + "' passed");
        }
        // ** NOTE ** Kill the case!! 
        String lkey = key.toLowerCase();
        {
            Object x = (overridesHash.get(lkey));
            if (x != null) {
                return x;
            }
        }
        {
            Object x = (propertiesHash.get(lkey));
            if (x != null) {
                return x;
            }
        }
        {
            Object x = (defaultsHash.get(lkey));
            return x;
        }
    }

    /**
     * Check whether there is key 'key' (actually whether there is a value different from (null), the assigned value of
     * 'key' may be (null) - it should not of course - in which case this function will return (null)) Passing (null)
     * here will result in an exception.
     */

    @Deprecated
    public boolean existsKeyP(String key) {
        // the hashtable will barf on 'key==null'
        return (get(key) != null);
    }

    /**
     * A weaker condition. This allows people to leave assignments that are not fully commented out...
     */
    
    public boolean existsNonemptyKeyP(String key) {
        Object obj = get(key);
        if (obj==null) {
            return false;            
        }
        if ((obj instanceof String) && ((String)obj).trim().isEmpty()) {
            return false;
        }
        return true;
    }
    
    /**
     * Get a value as a boolean. Throws an exception if the thing could not be correctly parsed or does not exist.
     */

    public boolean getAsBoolean(String key) {
        Object value = get(key);
        if (value == null) {
            throw new IllegalArgumentException("The value for key '" + key + "' is (null)");
        }
        if (value instanceof Boolean) {
            return ((Boolean) value).booleanValue();
        }
        if (value instanceof String) {
            try {
                return Parsing.parseBoolean((String) value);
            } catch (Exception exe) {
                throw new IllegalArgumentException("The value for key '" + key + "' is not a boolean but '" + value + "'");
            }
        }
        throw new IllegalArgumentException("The value for key '" + key + "' is of type " + value.getClass().getName() + " and cannot be interpreted");
    }

    /**
     * Get a value as a String. Throws an exception if the thing could not be correctly parsed or does not exist TODO:
     * passed "key" should be of type PropertyName The empty String is not reduced to "null"!
     */

    public String getAsString(String key) {
        Object value = get(key);
        if (value == null) {
            throw new IllegalArgumentException("The value for key '" + key + "' is (null)");
        }
        if (value instanceof String) {
            return (String) value;
        }
        throw new IllegalArgumentException("The value for key '" + key + "' cannot be interpreted");
    }

    /**
     * Get a value as a integer. Throws an exception if the thing could not be correctly parsed or does not exist
     */
    public int getAsInt(String key) {
        Object value = get(key);
        if (value == null) {
            throw new IllegalArgumentException("The value for key '" + key + "' is (null)");
        }
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException exe) {
                throw new IllegalArgumentException("The value for key '" + key + "' is not an integer but '" + value + "'");
            }
        }
        throw new IllegalArgumentException("The value for key '" + key + "' cannot be interpreted");
    }

    /**
     * Get a value as a float. Throws an exception if the thing could not be correctly parsed or does not exist
     */
    public float getAsFloat(String key) {
        Object value = get(key);
        if (value == null) {
            throw new IllegalArgumentException("The value for key '" + key + "' is (null)");
        }
        if (value instanceof Float) {
            return ((Float) value).floatValue();
        }
        if (value instanceof String) {
            try {
                return Float.parseFloat((String) value);
            } catch (NumberFormatException exe) {
                throw new IllegalArgumentException("The value for key '" + key + "' is not a float but '" + value + "'");
            }
        }
        throw new IllegalArgumentException("The value for key '" + key + "' cannot be interpreted");
    }

    /**
     * Be radical, just retrieve as Object. Throws an exception if the thing could not be correctly parsed or does not
     * exist. For values gotten from the read-in file, the 'Object' will always be a String. For values set
     * programatically under internally-used keys, the Object may be anything. The caller had better implictly know
     * what.
     */
    public Object getAsObject(String key) {
        Object value = get(key);
        if (value == null) {
            throw new IllegalArgumentException("The value for key '" + key + "' is (null)");
        }
        return value;
    }

    /**
     * Override a value with another one. If the passed value is (null), this is reduced to a NOP. The passed key must
     * be one of the valid keys (of course). The value's goodness is not checked (we assume that the caller knows what
     * he does).
     */
    public void override(String key, Object value) {
        if (value == null)
            return;
        if (!isValidKey(key)) {
            throw new IllegalArgumentException("Illegal key '" + key + "' passed");
        }
        String lkey = key.toLowerCase(); // KILL CASE
        overridesHash.put(lkey, value);
    }

    /**
     * Check whether x is a valid key; this check should check while ignoring case. Must be implemented in subclasses
     * which know about actual keys. This function does not check whether x is a valid *internal* key. The default
     * implementation accepts everything.
     */
    public boolean isValidKey(String xIn) {
        String x = xIn;
        if (x == null) {
            return false;
        }
        x = x.trim().toLowerCase();
        if (x.equals("")) {
            return false;
        }
        return true; // accept everything...for now
    }

    /**
     * Write out the status of all the properties as a Vector-Of-Strings Never returns null. Calling this function is
     * annoying as a mass of data will flood your logfiles.
     */
    public Vector<Object> toPolyline() {
        Hashtable<String,Object> map = new Hashtable<String,Object>();
        Vector<Object> res = new Vector<Object>();
        map.putAll(defaultsHash);
        map.putAll(propertiesHash);
        map.putAll(overridesHash);
        {
            Enumeration<String> iter = map.keys();
            while (iter.hasMoreElements()) {
                String key = iter.nextElement();
                res.add(key + " : " + map.get(key));
            }
        }
        return res;
    }

    /**
     * Reset the consumer's flag to 'false'. An exception is thrown if the consumer does not exist. This function is
     * called by the consumer once it has fetched its values.
     */
    public void consumerReset(String consumer) {
        synchronized (consumerFlags) {
            Integer valObj = consumerFlags.get(consumer);
            if (valObj == null) {
                throw new IllegalArgumentException("Consumer '" + consumer + "' was not found");
            } else {
                int val = valObj.intValue();
                int rev = Math.max(0, val - 1);
                consumerFlags.put(consumer, new Integer(rev));
            }
        }
    }

    /**
     * Flag all the consumer, thus telling them that the properties file changed. This is an operation synchronized on
     * the the consumer hashtable, and thus 'atomic' regarding consumer operations.
     */
    private void consumersFlagThemAll() {
        synchronized (consumerFlags) {
            Enumeration<String> iter = consumerFlags.keys();
            while (iter.hasMoreElements()) {
                String consumer = iter.nextElement();
                Integer valObj = consumerFlags.get(consumer);
                int rev = valObj.intValue() + 1;
                consumerFlags.put(consumer, new Integer(rev));
            }
        }
    }

    /**
     * Underride a value with another default one. If the passed value is (null), this is reduced to a NOP. The passed
     * key must be one of the valid keys (of course). The value's goodness is not checked (we assume that the caller
     * knows what he does).
     */
    public void underride(String key, Object value) {
        if (value == null) {
            return;
        }
        if (!isValidKey(key)) {
            throw new IllegalArgumentException("Illegal key '" + key + "' passed");
        }
        String lkey = key.toLowerCase();
        defaultsHash.put(lkey, value);
    }

    /**
     * Get a value as a double. Throws an exception if the thing could not be correctly parsed or does not exist
     */
    public double getAsDouble(String key) {
        Object value = get(key);
        if (value == null) {
            throw new IllegalArgumentException("The value for key '" + key + "' is (null)");
        }
        if (value instanceof Double) {
            return ((Double) value).doubleValue();
        }
        if (value instanceof Float) {
            return ((Float) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException exe) {
                throw new IllegalArgumentException("The value for key '" + key + "' is not a double but '" + value + "'");
            }
        }
        throw new IllegalArgumentException("The value for key '" + key + "' cannot be interpreted");
    }

    /**
     * Get a value as a long. Throws an exception if the thing could not be correctly parsed or does not exist
     */
    public long getAsLong(String key) {
        Object value = get(key);
        if (value == null) {
            throw new IllegalArgumentException("The value for key '" + key + "' is (null)");
        }
        if (value instanceof Long) {
            return ((Long) value).longValue();
        }
        if (value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException exe) {
                throw new IllegalArgumentException("The value for key '" + key + "' is not a long but '" + value + "'");
            }
        }
        throw new IllegalArgumentException("The value for key '" + key + "' cannot be interpreted");
    }

    /**
     * Get a value and interpret it as a port (0..0xFFFF). This means that if the value is found in the properties and it is an
     * integer, it is simply returned. If the value is found in the properties but it is not an integer it is
     * interpreted as a service tag which, together with 'type' (which is either 'udp' or 'tcp') is used to retrieve the
     * port number from /etc/services. An exception is thrown in case no corresponding line is found. Note that 'type'
     * is not used if the port number is retrieved directly from the properties file.
     */

    public int getAsPort(String key, String type) {
        if (!existsNonemptyKeyP(key)) {
            throw new IllegalArgumentException("The value for key '" + key + "' is unset");
        }
        Object value = get(key);
        if (value instanceof Integer) {
            return GetAsPort.getAsPort(value.toString(), type);
        } else if (value instanceof String) {
            String raw = (String)value;
            if (raw.trim().isEmpty()) {
                throw new IllegalArgumentException("The value for key '" + key + "' is the empty string");
            }
            return GetAsPort.getAsPort(value.toString(), type);
        } else {
            throw new IllegalArgumentException("The value for key '" + key + "' cannot be interpreted");
        }
    }

    /**
     * Helper
     */

    public static String[] parseToStringArray(String key, String val, Logger logger) {
        boolean warnIfDuplicates;
        List<String> list = Parsing.parseCommaSeparatedAtoms(val, key, logger, warnIfDuplicates = true);
        String[] array = new String[list.size()];
        int i = 0;
        for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
            array[i++] = iter.next();
        }
        return array;
    }

    private final static String POOL_FRONTEND_KEY = "232bcaf62ea9827c300c8970fbf27cff";

    // TODO: MOVE THESE "UPWARDS" AS HAPPENED WITH WAITERCONTAINER

    public final static String DEALER_BOARD_KEY = "621ce525c4f334309e53513e32407bc";
    public final static String DEAL_SILO_KEY = "1e23569c4502112f6597e9c3ab1f008f";

    /**
     * A function that retrieves an Integer using a specific and default key; also does range control Note that if
     * "hardDefault" is null, null will be naturally returned.
     */
    
    public static Integer retrieveInteger(String key, String keyDefault, Integer hardDefault, Integer lowestAcceptable, Integer highestAcceptable, PropertiesReader properties, Logger logger, boolean nullAcceptable) {
        Integer x;
        String chosenKey;
        assert hardDefault == null || (lowestAcceptable == null || lowestAcceptable.intValue() <= hardDefault.intValue());
        assert hardDefault == null || (highestAcceptable == null || hardDefault.intValue() <= highestAcceptable.intValue());
        if (properties.existsNonemptyKeyP(key)) {
            x = Integer.valueOf(properties.getAsInt(key));
            chosenKey = key;
        } else if (properties.existsNonemptyKeyP(keyDefault)) {
            x = Integer.valueOf(properties.getAsInt(keyDefault));
            chosenKey = keyDefault;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("No value found for key '" + key + "' or key '" + keyDefault + " -- returning hardcoded default of '" + hardDefault + "' instead.");
            }
            x = hardDefault;
            chosenKey = null;
        }
        // x may have taken on the value of "hardDefault" and will this be checked against lowest/highest and the "null allowed" flag
        // x may still be (null) if "hardDefault" is (null)
        if (x != null) {
            // check ranges leniently, and use hardDefault if out of range
            if (lowestAcceptable != null && x.intValue() < lowestAcceptable.intValue()) {
                logger.warn("The value for '" + chosenKey + "' set up in the properties is out of range - returning default of '" + hardDefault + "' instead.");
                x = hardDefault;
            }
            else if (highestAcceptable != null && highestAcceptable.intValue() < x.intValue()) {
                logger.warn("The value for '" + chosenKey + "' set up in the properties is out of range - returning default of '" + hardDefault + "' instead.");
                x = hardDefault;
            }
        }
        if (x==null && !nullAcceptable) {
            throw new IllegalArgumentException("Did not find any value for key '" + key + "' nor key '" + keyDefault + "'");
        }        
        return x; // may be null if hard default is null
    }

    public static Boolean retrieveBoolean(String key, String keyDefault, Boolean hardDefault, PropertiesReader properties, Logger logger, boolean nullAcceptable) {
        Boolean x;
        String chosenKey;
        if (properties.existsNonemptyKeyP(key)) {
            x = Boolean.valueOf(properties.getAsBoolean(key));
            chosenKey = key;
        } else if (properties.existsNonemptyKeyP(keyDefault)) {
            x = Boolean.valueOf(properties.getAsBoolean(keyDefault));
            chosenKey = keyDefault;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("No value found for key '" + key + "' or key '" + keyDefault + " -- returning hardcoded default of '" + hardDefault + "' instead.");
            }
            x = hardDefault;
            chosenKey = null;
        }
        if (x==null && !nullAcceptable) {
            throw new IllegalArgumentException("Did not find any value for key '" + key + "' nor key '" + keyDefault + "'");
        }
        return x; // may be null if hard default is null
    }

    public static String retrieveString(String key, String keyDefault, String hardDefault, PropertiesReader properties, Logger logger, boolean nullAcceptable) {
        String x;
        String chosenKey;        
        if (properties.existsNonemptyKeyP(key)) {
            x = properties.getAsString(key);
            chosenKey = key;
        } else if (properties.existsNonemptyKeyP(keyDefault)) {
            x = properties.getAsString(keyDefault);
            chosenKey = keyDefault;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("No value found for key '" + key + "' or key '" + keyDefault + " -- returning hardcoded default of '" + hardDefault + "' instead.");
            }
            x = hardDefault;
            chosenKey = null;
        }
        if (x==null && !nullAcceptable) {
            throw new IllegalArgumentException("Did not find any value for key '" + key + "' nor key '" + keyDefault + "'");
        }
        return x; // may be null if hard default is null
    }

}
