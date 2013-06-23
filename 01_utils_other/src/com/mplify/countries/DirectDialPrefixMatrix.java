package com.mplify.countries;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.listparsing.Parsing;
import com.mplify.logging.LogFacilities;
import com.mplify.resources.ResourceHelpers;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A class that is used to represent 
 * 
 * -->> international direct dialing prefixes
 * 
 * Formerly a database table was used but that does not seems to be necessary
 * (some leftover from the P&T ICMS days). Also, using a symbolic id instead
 * of an 'int' is a big improvement as the compiler can do better checks.
 * 
 * 2005.01.30 - Created from database table. The ide is to have better typed
 *              values than just a 'int'
 * 2005.01.31 - Static constructor bettered.
 * 2005.02.01 - Unnecessary numeric ids thrown out.
 * 2009.01.07 - Review: Added Satellite network DD codes, threw 
 * 2011.06.16 - Minor renames (ResourceHelpers.slurpResource)
 * 2011.10.19 - Adapted for LOG4J --> SLF4J migration
 ******************************************************************************/

public class DirectDialPrefixMatrix {

    private final static String CLASS = DirectDialPrefixMatrix.class.getName();
    private final static Logger LOGGER_build = LoggerFactory.getLogger(CLASS + ".build");

    /**
     * Internal maps
     */

    private final static Map<DirectDialPrefixId, Set<CountryId>> byPrefix = new HashMap<DirectDialPrefixId, Set<CountryId>>();
    private final static Map<CountryId, Set<DirectDialPrefixId>> byCountry = new HashMap<CountryId, Set<DirectDialPrefixId>>();

    /**
     * Name of the resource which contains the mapping prefix -> country with this syntax: 1418 -> CANADA 1441 ->
     * BERMUDA 61 -> AUSTRALIA, CHRISTMAS_ISLAND, COCOS_KEELING_ISLANDS Many prefixes may map to one country, many
     * countries may have the same prefix. The resource must be in the same package as this class.
     */

    private final static String MATRIX_RESOURCE = "direct_dial_prefix_matrix.txt";
    private final static String fqResourceName = ResourceHelpers.fullyQualifyResourceName(DirectDialPrefixMatrix.class.getPackage(), MATRIX_RESOURCE);

    /**
     * Standard "empty sets" returned whenever necessary
     */

    private final static Set<CountryId> EMPTY_COUNTRYID_SET = Collections.unmodifiableSet(new HashSet<CountryId>());
    private final static Set<DirectDialPrefixId> EMPTY_PREFIXID_SET = Collections.unmodifiableSet(new HashSet<DirectDialPrefixId>());

    /**
     * Flag controlling one-shot initialization
     */

    private static volatile boolean initialized = false;

    /**
     * Initialization is not done in a static constructor, but by a call to initialize()
     */

    private synchronized static void initialize() {
        if (!initialized) {
            try {
                build();
            } catch (IOException exe) {
                throw new IllegalStateException("While initializing from '" + fqResourceName + "'", exe);
            }
            initialized = true;
        }
    }

    /**
     * Read the "MATRIX_RESOURCE", parse it and fill the internal maps.
     */

    private static void build() throws IOException {
        Logger logger = LOGGER_build;
        // Get a "line number reader" which reads lines from the resource with the matrix data
        LineNumberReader lr;
        {
            String rawMatrixData = ResourceHelpers.slurpResource(fqResourceName, "UTF-8");
            lr = new LineNumberReader(new StringReader(rawMatrixData));
        }
        // Set up the patterns to handle comments and mapping
        Pattern commentPattern = Pattern.compile("^\\s*#");
        Pattern mappingPattern = Pattern.compile("^\\s*(\\w+)\\s*->\\s*(.+?)\\s*$");

        // Process all the lines, filling the internal maps
        {
            String line;
            while ((line = lr.readLine()) != null) {
                {
                    Matcher commentMatcher = commentPattern.matcher(line);
                    if (commentMatcher.lookingAt()) {
                        continue; // loop around
                    }
                }
                {
                    if ("".equals(line.trim())) {
                        continue; // loop around
                    }
                }
                {
                    Matcher m = mappingPattern.matcher(line);
                    if (!m.matches()) {
                        logger.warn("Syntax error at line " + lr.getLineNumber() + " of resource '" + fqResourceName + "': " + LogFacilities.mangleString(line));
                        continue; // loop around
                    }
                    String prefix = m.group(1).trim();
                    String postfix = m.group(2).trim();
                    // prefix must match "exactly"
                    DirectDialPrefixId prefixId = DirectDialPrefixId.obtainUsingExactFit(prefix, false);
                    if (prefixId == null) {
                        logger.warn("Unknown direct-dial prefix at line " + lr.getLineNumber() + " of resource '" + fqResourceName + "': " + LogFacilities.mangleString(line));
                        // forget this line
                        continue;
                    }
                    // postfix must be separable into exactly matchable canonical country names 
                    List<String> canoNames = Parsing.parseCommaSeparatedStuff(postfix, true);
                    if (canoNames.isEmpty()) {
                        // that prefix matches nothing? weird, but possible, just break off here
                        logger.warn("Empty mapping at line " + lr.getLineNumber() + " of resource '" + fqResourceName + "': " + LogFacilities.mangleString(line));
                        // forget this line
                        continue;
                    }
                    // map all the canonical names, dumping those which don't match
                    Set<CountryId> countryIds = new HashSet<CountryId>();
                    for (String canoName : canoNames) {
                        CountryId cid = CountryId.obtainFromCanonicalName(canoName, false);
                        if (cid == null) {
                            logger.warn("Unknown country '" + canoName + "' at line " + lr.getLineNumber() + " of resource '" + fqResourceName + "': " + LogFacilities.mangleString(line));
                        } else {
                            countryIds.add(cid);
                        }
                    }
                    // insert into internal prefixid -> { countryid } map
                    if (byPrefix.containsKey(prefixId)) {
                        logger.warn("Prefix " + prefix + " appears at least twice in resource '" + fqResourceName + "'");
                        byPrefix.get(prefixId).addAll(countryIds);
                    } else {
                        byPrefix.put(prefixId, countryIds);
                    }
                    // insert into internal countryid -> { prefixid } map
                    {
                        for (CountryId cid : countryIds) {
                            if (!byCountry.containsKey(cid)) {
                                byCountry.put(cid, new HashSet<DirectDialPrefixId>());
                            }
                            byCountry.get(cid).add(prefixId);
                        }
                    }
                }
            }
        }
        // Make all the sets immutable
        for (CountryId k : byCountry.keySet()) {
            Set<DirectDialPrefixId> v = byCountry.get(k);
            byCountry.put(k, Collections.unmodifiableSet(v));

        }
        for (DirectDialPrefixId k : byPrefix.keySet()) {
            Set<CountryId> v = byPrefix.get(k);
            byPrefix.put(k, Collections.unmodifiableSet(v));
        }
    }

    /**
     * Write the internal tables in "MATRIX_RESOURCE" style to stdout.
     */

    public static void dump() {

        if (!initialized) {
            initialize();
        }

        List<DirectDialPrefixId> ddList = new ArrayList<DirectDialPrefixId>(byPrefix.keySet());

        Comparator<DirectDialPrefixId> c = new Comparator<DirectDialPrefixId>() {
            @Override
            public int compare(DirectDialPrefixId prefix1, DirectDialPrefixId prefix2) {
                return DirectDialPrefixId.prefixComparison(prefix1.getDdPrefix(), prefix2.getDdPrefix());
            }
        };

        Collections.sort(ddList, c);

        for (DirectDialPrefixId prefixId : ddList) {
            Set<CountryId> countryIdSet = byPrefix.get(prefixId);
            System.out.printf("%-5s -> ", prefixId);
            boolean addComma = false;
            for (CountryId cid : countryIdSet) {
                if (addComma) {
                    System.out.print(",");
                }
                System.out.print(cid.getCanonicalName());
                addComma = true;
            }
            System.out.println();
        }
    }

    /**
     * Given a known "direct dial prefix", return the set of "country id" which can be reached with that prefix.
     * Generally there is only a single country, in some rare cases, one prefix is indeed mapped to several countries. A
     * null prefix is not allowed. Returns an immutable set! Never returns null.
     */

    public static Set<CountryId> getTargets(DirectDialPrefixId prefixId) {
        if (prefixId == null) {
            throw new IllegalArgumentException("The passed 'prefix id' is (null)");
        }
        if (!initialized) {
            initialize();
        }
        Set<CountryId> res = byPrefix.get(prefixId);
        if (res == null) {
            return EMPTY_COUNTRYID_SET;
        } else {
            return res;
        }
    }

    /**
     * Given a known "country id", return the set of "direct dial prefix" which can be used to reach that country. A
     * null country id is not allowed. The _NOWHERE country id (CountryId for UTC timezones) yields the empty set. The
     * _UNDEFINED country id either yields the empty set or an IllegalArgumentException depending on "throw.."
     */

    public static Set<DirectDialPrefixId> getPrefixes(CountryId countryId, boolean throwIfUndefinedCountryId) {
        if (countryId == null) {
            throw new IllegalArgumentException("The passed 'country id' is (null)");
        }
        if (!initialized) {
            initialize();
        }
        // we accept the _NOWHERE country id (CountryId for UTC timezones) and return the empty set
        if (CountryId._NOWHERE.equals(countryId)) {
            return EMPTY_PREFIXID_SET;
        }
        // for the _UNDEFINED country id, we might throw
        if (CountryId._UNDEFINED.equals(countryId)) {
            if (throwIfUndefinedCountryId) {
                throw new IllegalArgumentException("The passed 'country id' is the 'undefined' country id");
            } else {
                return EMPTY_PREFIXID_SET;
            }
        }
        // now for a serious result
        Set<DirectDialPrefixId> res = byCountry.get(countryId);
        if (res == null) {
            return EMPTY_PREFIXID_SET;
        } else {
            return res;
        }
    }

}
