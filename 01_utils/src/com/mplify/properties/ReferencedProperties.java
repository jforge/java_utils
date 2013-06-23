package com.mplify.properties;

import java.io.File;
import java.util.Set;

import com.mplify.checkers.Check;
import com.mplify.parsing.Parsing;
import com.mplify.resources.ResourceHelpers;
import com.mplify.sysprop.SystemProperties;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Helper to extract more info from annex property files that are referenced
 * from the main property file.
 *
 * Example for the property syntax expected:
 * 
 * No separate database properties:
 * 
 *    use.database = yes
 * 
 * Separate database properties in secondary file:
 * 
 *    use.database = yes:file=/etc/mplify/db.properties
 * 
 * Separate database properties in resource:
 * 
 *    use.database = yes:resource=/com/mplify/db.properties
 * 
 * Separate database properties in file relative to the directory indicated
 * by system property "confighome":
 * 
 *    use.database = yes:confighomefile=db.properties
 * 
 * 2010.11.02 - Created by moving code from _Main into a separate class
 * 2011.01.11 - Added "confighome" syntax
 * 2011.04.05 - Separated out makePropertiesReaderFromReference() so as
 *              to use it from other places.
 * 2011.06.16 - Overall review of this structure        
 *              extractReferencedStuff() tries to interprete unknwon stuff
 *              as a filename.     
 * 2011.11.08 - Moved inner classes out               
 ******************************************************************************/

public class ReferencedProperties {

//    private final static String CLASS = ReferencedProperties.class.getName();
//    private final static Logger LOGGER_extractReferencedStuff = LoggerFactory.getLogger(CLASS + ".extractReferencedStuff");
        
    protected final PropertiesReader properties; // the core properties; may reference annex properties

    /**
     * A complex response used by subclasses
     */

    public static class BooleanSetupResponse {

        public boolean useIt;
        public String key;

    }

    /**
     * Constructor sets the properties
     */

    public ReferencedProperties(PropertiesReader properties) {
        Check.notNull(properties,"properties");
        this.properties = properties;
    }

    /**
     * Extract the "referenced stuff" from the "reference" String. Never returns null. The "pname" is passed for good error messages.
     * Expected syntax of "reference": (resource|file|confighomefile)=resource-or-file-location
     */

    public static ReferencedStuff extractReferencedStuff(String reference, String pname) {
        Check.notNull(reference,"reference");
        String lcReference = reference.toLowerCase().trim();
        String charset = "UTF-8";
        PropertiesReader res;
        if (lcReference.startsWith("resource=")) {
            //
            String resource = reference.substring("resource=".length());
            Check.isFalse(resource.trim().isEmpty(), "The resource in key %s is not indicated: '%s'", pname, reference);
            return new ReferencedStuff(resource);
            //
        } else if (lcReference.startsWith("file=")) {
            //
            String filename = reference.substring("file=".length());
            Check.isFalse(filename.trim().isEmpty(), "The filename in key %s is not indicated: '%s'", pname, reference);
            File file = new File(reference.substring("file=".length())); // absolute or relative file...
            return new ReferencedStuff(file);
            //
        } else if (lcReference.startsWith("filename=")) {
            //
            String filename = reference.substring("filename=".length());
            Check.isFalse(filename.trim().isEmpty(), "The filename in key %s is not indicated: '%s'", pname, reference);
            File file = new File(reference.substring("filename=".length())); // absolute or relative file...
            return new ReferencedStuff(file);
            //
        } else if (lcReference.startsWith("confighomefile=")) {
            //
            boolean throwIfNotFound, normalize;
            String filename = reference.substring("confighomefile=".length());
            String confighome = SystemProperties.getConfighome(throwIfNotFound = false);
            Check.isTrue(confighome != null, "The reference '%s' found in key '%s' demands a 'confighome' system property, but that system property is not set", reference, pname);
            File file = new File(confighome, filename);
            return new ReferencedStuff(file);
            //
        } else {
            //
            // Should we complain? It _might_ be just a file, actually...
            // throw new IllegalArgumentException("Unknown syntax for key '" + pname + ": '" + reference + "'; try one of: resource=..., file=..., confighomefile=...");
            File file = new File(reference);
            return new ReferencedStuff(file);
        }
    }

    /**
     * Static function to initialize a new PropertiesReader given a reference. Never returns null.
     * The reference should look like "(resource|file|confighomefile)=resource-or-file-location"
     * Encoding of the file is always assumed to be UTF-8 (this should actually be somehow configurable).
     * The original key through which the reference was found can be passed; this is used to give
     * good error messages only.
     */

    public static PropertiesReader pullInReferencedProperties(String reference, String pname) throws Exception {
        Check.notNull(reference,"reference");
        ReferencedStuff refstuff = extractReferencedStuff(reference, pname);
        assert refstuff != null;
        String charset = "UTF-8";
        if (refstuff.resource != null) {
            return new PropertiesReader(ResourceHelpers.getStreamFromResource(refstuff.resource), charset);
        } else {
            assert refstuff.file != null;
            return new PropertiesReader(refstuff.file, charset);
        }
    }

    /**
     * This function returns a combination FALSE/TRUE, with additional "ReferencedStuff" (if available) in the case of "TRUE".
     * Never returns null.
     * 
     * A property indicated by "key" may be absent, in which case "FALSE" is returned
     * 
     * If the property is present, its syntax is supposed to follow:
     * 
     * (yes|no)(:(resource|file|confighomefile)=resource-or-file-location)?
     *
     * If the first group says "no", FALSE will be returned.
     * 
     * If the first group says "yes", TRUE will be returned, with additional "ReferencedStuff" extracted from the
     * reference following the first group, if available. 
     */

    public static ReferencedStuffAsNeeded extractReferencedStuffAsNeeded(PropertiesReader properties, String pname) {
        Check.notNull(properties,"properties");
        Check.notNull(pname,"property name");
        //
        // Return false if the key doesn't exist in the first place
        //
        if (!properties.existsNonemptyKeyP(pname)) {
            return new ReferencedStuffAsNeeded(false, null);
        }
        //
        // The value of "key" is either a simple boolean or a boolean with a reference after a ":"
        //
        String value = properties.getAsString(pname);
        int sepIndex = value.indexOf(":");
        //
        // If there is no "sepIndex", assume no reference; all the relevant properties are assumed to be already read
        // through the main properties file. The whole value is interpreted to be just a boolean. If it isn't, an Exception will be raised.
        //
        if (sepIndex < 0) {
            boolean use = properties.getAsBoolean(pname); // will throw if this is not actually a boolean
            return new ReferencedStuffAsNeeded(use, null);
        }
        //
        // We are in presence of X:Y, analyze X and Y
        //
        String left = value.substring(0, sepIndex);
        String reference = value.substring(sepIndex + 1);
        //
        // Again, the boolean is checked and additional info is extracted depending on it
        //
        {
            boolean use = Parsing.parseBoolean(left); // will throw if this is not actually a boolean
            ReferencedStuff stuff = null;
            if (use) {
                stuff = extractReferencedStuff(reference, pname);
            }
            return new ReferencedStuffAsNeeded(use, stuff);
        }
    }

    /**
     * This function returns a yes/no value that can be obtained from the property "key" ("no" being the
     * default, it is returned if there is no value for the "key" at all). If the property has an appended reference,
     * for example:
     * 
     * use.database = yes:confighomefile=db.properties
     *
     * the file or resource referenced is opened and read as a sequence of properties.
     * 
     * The properties thus obtained then "underride" the originally passed properties (so any name clashes
     * are resolved in favor of the existing properties)
     * 
     * Property value syntax:
     * 
     * (yes|no)(:(resource|file|confighomefile)=resource-or-file-location)?
     */

    public static boolean pullInReferencedPropertiesAsNeeded(PropertiesReader properties, String pname) throws Exception {
        ReferencedStuffAsNeeded refstuffAsNeeded = extractReferencedStuffAsNeeded(properties, pname);
        assert refstuffAsNeeded != null;
        if (refstuffAsNeeded.use && refstuffAsNeeded.stuff != null) {
            String charset = "UTF-8";
            PropertiesReader subProps;
            if (refstuffAsNeeded.stuff.resource != null) {
                subProps = new PropertiesReader(ResourceHelpers.getStreamFromResource(refstuffAsNeeded.stuff.resource), charset);
            } else {
                assert refstuffAsNeeded.stuff.file != null;
                subProps = new PropertiesReader(refstuffAsNeeded.stuff.file, charset);
            }
            Set<String> take = subProps.getKeysByRoot("");
            for (String subprkey : take) {
                properties.underride(subprkey, subProps.getAsObject(subprkey));
            }
        }
        return refstuffAsNeeded.use;
    }

}
