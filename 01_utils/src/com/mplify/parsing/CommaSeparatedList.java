package com.mplify.parsing;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mplify.checkers.Check;
import com.mplify.logging.LogFacilities;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2009, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * This class represents and parses a set of keywords made of:
 * 
 *   alphanumerics "_" ":" "*" "?" "-" "'" "." "+"
 *   
 * which can be transformed into enums or whatever later on.
 * 
 * 2009.08.19 - Created
 * 2009.08.20 - Added constructor taking "rawIn" string
 * 2009.08.27 - Moved to com.mplify.parsing to replace the
 *              "ImmediateProperties" parsing; modified so that it parses
 *              a "comma-separated list"; renamed from AtomSet to 
 *              CommaSeparatedList
 * 2010.12.15 - Keywords can contain "'" (useful for phone numbers)              
 ******************************************************************************/

public class CommaSeparatedList {

    private final static Pattern patternId = Pattern.compile("^\\s*([\\w+\\-\\.\\+\\:\\']+)(.*)$");
    private final static Pattern patternSeparator = Pattern.compile("^\\s*(,)(.*)$");
    private final static Pattern emptyLine = Pattern.compile("^\\s*$");

    private final List<String> list = new LinkedList<String>();

    /**
     * If the expression is not as expected, a ParseException is thrown
     */

    @SuppressWarnings("serial")
    public static class ParseException extends Exception {

        public ParseException() {
            super();
        }

        public ParseException(String message, Throwable cause) {
            super(message, cause);
        }

        public ParseException(String message) {
            super(message);
        }

        public ParseException(Throwable cause) {
            super(cause);
        }

    }

    /**
     * Construct an empty list
     */

    public CommaSeparatedList() {
        // NOP
    }

    /**
     * Construct an list from the "rawIn" string. This is the same as creating an
     * empty list, then calling "addFromString" on it
     */

    public CommaSeparatedList(String rawIn) throws ParseException {
        addFromString(rawIn); // pass string must not be null
    }

    /**
     * Parses a string like "alpha, beta, gamma". The separator must be ',' or ';'
     */

    public void addFromString(String rawIn) throws ParseException {
        Check.notNull(rawIn, "raw string");
        String raw = rawIn;
        boolean separatorMustExist = false;
        while (!emptyLine.matcher(raw).matches()) {
            // A separator MAY or MUST occur
            {
                Matcher matcherSeparator = patternSeparator.matcher(raw);
                if (matcherSeparator.matches()) {
                    raw = matcherSeparator.group(2);
                    separatorMustExist = false; // more separators may follow but they are optional
                    continue; // LOOP AROUND
                } else {
                    if (separatorMustExist) {
                        throw new ParseException("Expected separator at '" + LogFacilities.mangleString(raw) + "'");
                    }
                }
            }
            {
                Matcher matcherId = patternId.matcher(raw);
                if (matcherId.matches()) {
                    String atom = matcherId.group(1);
                    raw = matcherId.group(2);
                    list.add(atom);
                    separatorMustExist = true;
                    continue; // LOOP AROUND
                }
            }
            // if we are here, failure
            throw new ParseException("Expected atom or sequence at '" + LogFacilities.mangleString(raw) + "'");
        }
    }

    /**
     * Removing duplicates. The set of duplicates is returned (never null but may be empty)
     */

    public Set<String> removeDuplicates() {
        Set<String> duplicates = new HashSet<String>();
        Set<String> found = new HashSet<String>();
        Iterator<String> iter = list.iterator();
        while (iter.hasNext()) {
            String atom = iter.next();
            if (found.contains(atom)) {
                iter.remove();
                duplicates.add(atom);
            } else {
                found.add(atom);
            }
        }
        return duplicates;
    }

    /**
     * Empty underlying list?
     */

    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * How many elements?
     */

    public int size() {
        return list.size();
    }

    /**
     * Access the list directly. Never returns null.
     */

    public List<String> getList() {
        return list;
    }
}
