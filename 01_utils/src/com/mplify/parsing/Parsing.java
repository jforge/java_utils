package com.mplify.parsing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.mplify.checkers.Check;
import com.mplify.logging.LogFacilities;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Facilities for taking apart strings.
 *
 * 2004.10.19 - Moved to this class from the biggish 'Utilities'
 * 2008.10.02 - Fixed the code for parseCommaSeparatedStuff() so that an
 *              empty string results in an empty List, not a List with the
 *              empty string as single element.
 * 2009.08.26 - parseCommaSeparatedAtoms() now uses CommaSeparatedList
 * 2010.10.20 - Added joinCommaSeparatedAtoms()
 * 2010.10.21 - parseCommaSeparatedAtomsToArray() moved to here
 * 2011.11.10 - joinCommaSeparatedStrings() now takes Collection instead of
 *              List
 * 2012.05.30 - Generalized joinCommaSeparatedStrings() to 
 *              joinCommaSeparatedStuff()
 ******************************************************************************/

public class Parsing {

//    private final static String CLASS = Parsing.class.getName();
    private final static Pattern PATTERN = Pattern.compile(",");

    /**
     * Unreachable constructor
     */

    private Parsing() {
        // Unreachable
    }

    /**
     * Parse a line containing a list of comma-separated stuff (anything between commmas). Individual results may be
     * trimmed. If "line" is empty, an empty array is returned
     */

    public static List<String> parseCommaSeparatedStuff(String line, boolean trim) {
        Check.notNull(line, "line");
        if (line.trim().isEmpty()) {
            // specially handle the empty string otherwise you will get a List with an "empty string" entry
            return new ArrayList<String>(0);
        }
        String[] result = PATTERN.split(line);
        List<String> res = new ArrayList<String>(result.length);
        for (int i = 0; i < result.length; i++) {
            if (trim) {
                res.add(result[i].trim());
            } else {
                res.add(result[i]);
            }
        }
        return res;
    }

    /**
     * Parse a line containing the list of comma-separated "atoms" (where an "atom" is a string composed of A-Z, a-z,
     * 0-9, _, -, ., +, : i.e. something that matches the Perl Regexp [\w\-\.\+\:]+ Whitespace surrounding the atoms is
     * thrown away. Empty atoms are not allowed. The line is supposed to have been obtained from a 'properties' file.
     * The key name of that line can also be passed as 'key' in order to print good debugging information. We return a
     * list to keep the ordering. Duplicates are however eliminated - only the first entry found is used.
     * Don't care about whether "line" or "key" null. If "line" is null, a warning will be output and the empty list
     * will be returned.
     */

    public static List<String> parseCommaSeparatedAtoms(String line, String key, Logger logger, boolean warnIfDuplicate) {
        // don't care about whether "line" or "key" null
        try {
            CommaSeparatedList csl = new CommaSeparatedList(line); // if "line" is null, an exception will be raised, then caught below
            Set<String> duplicates = csl.removeDuplicates();
            if (!duplicates.isEmpty() && logger != null && warnIfDuplicate) {
                logger.warn("Found duplicates while parsing value of key '" + key + "': '" + LogFacilities.mangleString(line) + "'");
            }
            return csl.getList(); // filled list (may still be empty)
        } catch (CommaSeparatedList.ParseException exe) {
            if (logger != null) {
                logger.warn("While parsing value of key '" + key + "': '" + LogFacilities.mangleString(line) + "'", exe);
            }
            return new LinkedList<String>(); // empty list
        }
    }

    /**
     * Parse the passed 'value' into a boolean - many values are acceptable but if none of these fits, an exception is
     * thrown. (null) cannot be passed.
     */

    public static boolean parseBoolean(String value) {
        String lvalue = value.toLowerCase().trim();
        if (lvalue.equals("yes") || lvalue.equals("true") || lvalue.equals("on") || lvalue.equals("1") || lvalue.equals("y") || lvalue.equals("t")) {
            return true;
        }
        if (lvalue.equals("no") || lvalue.equals("false") || lvalue.equals("off") || lvalue.equals("0") || lvalue.equals("n") || lvalue.equals("f")) {
            return false;
        }
        throw new IllegalArgumentException("The passed value '" + value + "' cannot be interpreted as a boolean");
    }

    /**
     * Regenerate a list. (null) yields the empty list.
     */

    public static String joinCommaSeparatedStuff(Collection<?> c) {
        StringBuilder buf = new StringBuilder();
        if (c != null) {
            boolean addSep = false;
            for (Object x : c) {
                if (addSep) {
                    buf.append(",");
                }
                if (x == null) {
                    buf.append("(null)");
                } else {
                    buf.append(x.toString());
                }
                addSep = true;
            }
        }
        return buf.toString();
    }

    /**
     * Parse into an array instaed of a List<String>
     */

    @SuppressWarnings("unused")
    public static String[] parseCommaSeparatedAtomsToArray(String key, String val, Logger logger) {
        boolean warnIfDuplicates;
        List<String> list = Parsing.parseCommaSeparatedAtoms(val, key, logger, warnIfDuplicates = true);
        String[] array = new String[list.size()];
        int i = 0;
        for (Iterator<String> iter = list.iterator(); iter.hasNext();) {
            array[i++] = iter.next();
        }
        return array;
    }
}
