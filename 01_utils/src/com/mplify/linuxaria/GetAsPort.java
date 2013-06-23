package com.mplify.linuxaria;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers.Check;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Scan /etc/services or an equivalent to get the numeric IP port number
 * given the service name.
 *
 * See existing implementations:
 * 
 * Network services database access for java -- https://github.com/wmeissner/jnr-netdb
 * GetServiceByName                          -- http://www.javafaq.nu/java-example-code-162.html
 *
 * TODO: One should actually return a set of port numbers!!
 * TODO: Slurp file once, reread only if changed
 * 
 * 2004.10.19 - Created from methods in Utilities
 * 2009.08.26 - Replaced Jakarta ORO calls with Java Regexp calls;
 *              Simplified handling in getAsPort() by removing reliance on
 *              ImmediateProperties; if /etc/services cannot be found we just
 *              get out
 * 2011.01.17 - Introduced _check.
 * 2011.07.29 - Review, added possibility to search on aliases, too.
 *              Added TestCase.
 * 2012.12.10 - Error suppressed by rearranging code!             
 ******************************************************************************/

public class GetAsPort {

    private final static String CLASS = GetAsPort.class.getName();
    private final static Logger LOGGER_scanServices = LoggerFactory.getLogger(CLASS + ".scanServices");

    /**
     * These patterns are used when scanning /etc/services. These will be matched fully, so anchors not needed!
     */

    private static Pattern PAT_COMMENT = Pattern.compile("(.*?)(#.*)?");
    private static Pattern PAT_CONTENT = Pattern.compile("(\\S+)\\s+(\\d+)\\/(\\w+)\\s*(\\S.*)?");

    /**
     * Unreachable constructor
     */

    private GetAsPort() {
        // Unreachable
    }

    /**
     * Get a value and interpret it as a port. This means that if the value is found to be an integer, it is simply
     * returned as a parsed int. If the value is not an integer it is interpreted as a service tag which, together with
     * the passed 'type' (which is either 'udp' or 'tcp') is used to retrieve the actual port number from
     * "/etc/services". An exception is thrown in case no corresponding line is found. Note that 'type' is *not* used if
     * the port number can be interpreted as an Integer, so you can pass "null" there.
     */

    public static int getAsPort(String serviceNameIn, String protocolIn) {
        return getAsPort(serviceNameIn, protocolIn, null);
    }

    public static int getAsPort(String serviceNameIn, String protocolIn, File sourceIn) {
        Check.notNull(serviceNameIn, "value");
        File source;
        if (sourceIn != null) {
            source = sourceIn;
        }else{
            if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0)
                source = new File("c:/Windows/System32/drivers/etc/services");
            else
                source = new File("/etc/services");
        } 
        //
        // 'value' can be a number or a service tag
        //
        Integer tmp = null;
        try {
            tmp = new Integer(Integer.parseInt(serviceNameIn));
        } catch (NumberFormatException exe) {
            // no luck; not a number; code below will test /etc/services
        }
        //
        // If not a number, check /etc/services
        //
        if (tmp == null) {
            Check.notNull(protocolIn, "type");
            assert protocolIn != null;
            Check.isTrue(source.exists(), "The file '%s' does not exist -- cannot resolve '%s'", source, serviceNameIn);
            Check.isTrue(source.canRead(), "The file '%s' exists but cannot be read -- cannot resolve '%s'", source, serviceNameIn);
            try {
                tmp = scanServices(serviceNameIn, protocolIn.toLowerCase(), source); // this might also throw
            } catch (Exception exe) {
                // NOP, tmp will stay null and we will throw at the next check
            }
            Check.isTrue(tmp != null, "No entry found for service '%s', type '%s' in file '%s'", serviceNameIn, protocolIn, source);
            assert tmp != null;
        }
        //
        // Range check. Note that 0 is ok!
        //
        Check.isFalse(tmp.intValue() < 0 || 0xFFFF < tmp.intValue(), "The value of '%s' is '%s' which does not look like a port number at all", serviceNameIn, tmp);
        return tmp.intValue();
    }

    /**
     * Helper
     */

    private static Integer safePortNumberParse(String x, String serviceName, Logger logger) {
        try {
            return new Integer(Integer.parseInt(x)); // OUTTA HERE: early return
        } catch (Exception exe) {
            logger.warn("While parsing the port number of service " + serviceName, exe);
            return null; // OUTTA HERE: give up
        }
    }

    /**
     * Scan through /etc/services and look for the port number corresponding to 'service' Throws FileNotFoundException
     * if the file does not exist. Throws IOException if there was a problem reading. Throws a NumberFormatException if
     * the port number was bad. Returns null if no valid port entry was found.
     */

    private static Integer scanServices(String serviceNameIn, String protocolIn, File source) throws FileNotFoundException, IOException {
        Logger logger = LOGGER_scanServices;
        assert serviceNameIn != null;
        assert protocolIn != null;
        assert source != null;
        LineNumberReader lnr = null;
        try {
            lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(source), "UTF-8"));
            String line;
            while ((line = lnr.readLine()) != null) {
                //
                // Lop off comment
                //
                {
                    Matcher m1 = PAT_COMMENT.matcher(line);
                    if (m1.matches()) {
                        line = m1.group(1).trim();
                        if (line.isEmpty()) {
                            continue; // pure comment
                        }
                    } else {
                        logger.warn("Unmatched line '" + line + "' while reading file '" + source + "'");
                        continue;
                    }
                }
                //
                // Not a comment
                //
                Matcher m2 = PAT_CONTENT.matcher(line);
                if (!m2.matches()) {
                    logger.warn("Unmatched line '" + line + "' while reading file '" + source + "'");
                    continue;
                }
                //
                // Good content!
                //
                String serviceNameFound = m2.group(1);
                String portNumberFound = m2.group(2);
                String protocolFound = m2.group(3);
                String rest = m2.group(4);
                /*                
                    System.out.println("SERVICE " + serviceNameFound);
                    System.out.println("PORT    " + portNumberFound);
                    System.out.println("PROTO   " + protocolFound);
                    System.out.println("REST    " + rest);
                */
                //
                // Correct type?
                //
                if (!protocolIn.equalsIgnoreCase(protocolFound)) {
                    continue;
                }
                //
                // Did we match the primary service name?
                //
                if (serviceNameIn.equalsIgnoreCase(serviceNameFound)) {
                    return safePortNumberParse(portNumberFound, serviceNameFound, logger);
                }
                //
                // Aliases may exist
                //
                if (rest != null) {
                    String[] aliases = rest.split(" ");
                    for (String alias : aliases) {
                        if (serviceNameIn.equalsIgnoreCase(alias)) {
                            return safePortNumberParse(portNumberFound, serviceNameFound, logger);
                        }
                    }
                }
            }
        } finally {
            try {
                if (lnr != null) {
                    lnr.close();
                }
            } catch (IOException exe) {
                logger.warn("While closing reader on " + source, exe);
            }
        }
        //
        // If we are here, no result!
        //
        return null;
    }
}
