package com.mplify.tools;

import java.util.regex.Pattern;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Test an Internet Address String according to the syntax of RFC5322, but
 * somewhat simplified.  
 * 
 * This class exists because the "javax.mail.InternetAddress" constructor
 * accepts bad syntax according to RFC5322: "a@b.c." with a final dot passes!
 * (one might say that the final dot indicates an absolute domain, but actually
 *  such a string is not allowed).
 * 
 * OTOH, according to RFC5322, some addresses are valid but their domain names 
 * as per RFC1034 are actually invalid, e.g. "email@bureau.?" would be 
 * acceptable to RFC5322 but the domain name is clearly invalid.
 *
 * So the domain name is additionally tested against RFC1034.
 *
 * The Acceptor does not accept an address without an "@", i.e. the address
 * *must* be qualified!
 *
 * http://tools.ietf.org/html/rfc1034 :  DOMAIN NAMES - CONCEPTS AND FACILITIES
 * http://tools.ietf.org/html/rfc2181 :  Clarifications to the DNS Specification
 * http://tools.ietf.org/html/rfc3492 :  Punycode: A Bootstring encoding of Unicode for Internationalized Domain Names in Applications (IDNA)
 * http://tools.ietf.org/html/rfc3986 :  Uniform Resource Identifiers (URI): Generic Syntax
 * http://tools.ietf.org/html/rfc5322 :  Internet Message Format
 *
 * RFC 2396 has a different interpretation of domain names than rfc1034,
 * actually allowing domainlabels to start with digits, except for the "toplabel":
 * 
 * hostname      = *( domainlabel "." ) toplabel [ "." ]
 * domainlabel   = alphanum | alphanum *( alphanum | "-" ) alphanum
 * toplabel      = alpha | alpha *( alphanum | "-" ) alphanum
 *
 * BUT NO UNDERSCORES
 * 
 * We shall follow the above syntax.
 * 
 * Even after AddressAcceptor has okayed the string, there may be unforeseen
 * problems either when constructing a javax.mail.InternetAddress or using
 * a javax.mail.InternetAddress (for example, when sending, Java "checks"
 * the InternetAddress and may uncover more problems), so client code has to
 * call InternetAddress.validate(), which calls the private 
 * InternetAddress.checkAddress() [see the open-sourced Sun JavaMail code]
 * 
 * See also the corresponding test case.
 * 
 * 2010.12.17 - Created
 * 2011.05.11 - Improved to catch bad domain names according to RFC1034, see
 *              bug#3124
 * 2011.09.08 - Improved to relax checks according to RFC2396, see bug#3484
 * 2013.01.31 - People are using "_" in "hostnames". They cannot, but they do.
 *              Allow underscores, then....   
 ******************************************************************************/
 
public class AddressAcceptor {

    private final static String ATEXT_REGEX = "[A-Za-z0-9!#\\$%&'*+-/=?^_`{|}~]+";
    
    private final static String DOMAINLABEL_REGEX = "[A-Za-z0-9]([A-Za-z0-9-]*[A-Za-z0-9])?";
    private final static String TOPLABEL_REGEX = "[A-Za-z]([A-Za-z0-9-]*[A-Za-z0-9])?";

    private final static String DOMAINLABEL_REGEX_WITH_UNDERSCORES = "[A-Za-z0-9]([A-Za-z0-9_-]*[A-Za-z0-9])?";
    private final static String TOPLABEL_REGEX_WITH_UNDERSCORES = "[A-Za-z]([A-Za-z0-9_-]*[A-Za-z0-9])?";

    private final static Pattern ATEXT_PATTERN = Pattern.compile(ATEXT_REGEX);
    
    private final static Pattern DOMAINLABEL_PATTERN = Pattern.compile(DOMAINLABEL_REGEX);
    private final static Pattern TOPLABEL_PATTERN = Pattern.compile(TOPLABEL_REGEX);

    private final static Pattern DOMAINLABEL_PATTERN_WITH_UNDERSCORES = Pattern.compile(DOMAINLABEL_REGEX_WITH_UNDERSCORES);
    private final static Pattern TOPLABEL_PATTERN_WITH_UNDERSCORES = Pattern.compile(TOPLABEL_REGEX_WITH_UNDERSCORES);

    /**
     * Cannot be instantiated as we just have static testing here
     */

    private AddressAcceptor() {
        // NOP
    }

    /**
     * Check whether "address" passes a "Simplified RFC5322 Syntax" Test. 
     * Underscores are not allowed
     */

    public static boolean acceptAddress(String address) {
        return acceptAddress(address, false);
    }
    
    /**
     * Check whether "address" passes a "Simplified RFC5322 Syntax" Test.
     * Underscores-allowed can be switched on on need.  
     */

    public static boolean acceptAddress(String address, boolean withUnderscores) {
        // We can't be arsed to build an ANTLR parser for this..  
        // System.out.println("Address? '" + address + "'");
        if (address == null) {
            return false;
        } else {
            String[] x = address.trim().split("@", -1);
            return (x.length == 2 && acceptLocalPart(x[0]) && acceptDomainPart(x[1], withUnderscores));
        }
    }

    /**
     * Sub test
     */
    
    public static boolean acceptLocalPart(String localPart) {
        assert localPart != null;
        return acceptDotAtom(localPart);
    }

    /**
     * Sub test
     */

    public static boolean acceptDomainPart(String domainPart, boolean withUnderscores) {
        assert domainPart != null;
        // special case: a final dot is ok, just strip it
        String fixedDomainPart;
        if (domainPart.endsWith(".")) {
            fixedDomainPart = domainPart.substring(0, domainPart.length()-1);
        }
        else {
            fixedDomainPart = domainPart;
        }
        return acceptDotLabel(fixedDomainPart, withUnderscores);
    }

    /**
     * Sub test
     */

    private static boolean acceptDotAtom(String dotAtom) {
        assert dotAtom != null;
        // System.out.println("DotAtom? '" + dotAtom + "'");
        String x[] = dotAtom.split("\\.", -1);
        if (x.length == 0) {
            return false;
        } else {
            for (String atext : x) {
                if (!acceptAtext(atext)) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Sub test
     */

    private static boolean acceptDotLabel(String dotLabel, boolean withUnderscores) {
        assert dotLabel != null;
        // System.out.println("DotAtom? '" + dotLabel + "'");
        String x[] = dotLabel.split("\\.", -1);
        if (x.length == 0) {
            return false;
        } else {
            for (int i=0;i<x.length-1;i++) {            
                if (!acceptDomainlabel(x[i], withUnderscores)) {
                    return false;
                }
            }
            // so far, so good - test toplabel
            return acceptToplabel(x[x.length-1], withUnderscores);
        }
    }

    /**
     * Sub test
     */

    private static boolean acceptAtext(String atext) {
        boolean res = ATEXT_PATTERN.matcher(atext).matches();
        // System.out.println("Atext? '" + atext + "' --> " + res);
        return res;
    }
    
    /**
     * Sub test
     */

    private static boolean acceptDomainlabel(String label, boolean withUnderscores) {
        Pattern pat = (withUnderscores ? DOMAINLABEL_PATTERN_WITH_UNDERSCORES : DOMAINLABEL_PATTERN);
        boolean res = pat.matcher(label).matches() && label.length()<=63;
        // System.out.println("Label? '" + label + "' --> " + res);
        return res;
    }
    
    /**
     * Sub test
     */

    private static boolean acceptToplabel(String label, boolean withUnderscores) {
        Pattern pat = (withUnderscores ? TOPLABEL_PATTERN_WITH_UNDERSCORES : TOPLABEL_PATTERN);        
        boolean res = pat.matcher(label).matches() && label.length()<=63;
        // System.out.println("Label? '" + label + "' --> " + res);
        return res;
    }
}
