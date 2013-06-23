package com.mplify.xml;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.output.EscapeStrategy;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.mplify.checkers.Check;
import com.mplify.properties.PropertyName;
import com.mplify.tools.ByteCoding;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Simple utilities
 *
 * Unsolved mysteries: Many methods do 'verifications'. These may not be needed
 * if the DOM parser checks the tree. The DOM parser tries to get the DTD -
 * so is verification done or not?
 * 
 * 2004.09.29 - Created from a common superclass during code cleanup.
 * 2004.11.22 - Added renderAttribute() for set
 * 2005.08.29 - Some review: stringToUtf8ByteStream() now just transforms
 *              a string instead of an XML document. All the methods formerly
 *              in XMLDocument/XMDocumentHelper have been moved to here as
 *              they have all become 'static'
 * 2005.09.09 - Added removeTextDeclaration() and changeRootNodeName()
 * 2008.08.13 - Modified safeEscape_ISO_8859_1_toXML() to escape < > ' " &
 *              using the special &lt; etc... instead of numeric codes. This
 *              was donr for fun while trying out a modific for Voxeo
 * 2008.11.19 - Slight simplifications
 * 2009.04.14 - Added stringifyRawly(), deprecated the other stringify operations
 *              Added an overloaded stringifyPrettily for docs
 * 2009.12.07 - Jakarta ORO replaced by Java Regexp; dodgy.
 * 2010.10.05 - JDOM Formats set up so that the same instances are used always.
 *              Add the special "DB FORMAT" which encodes any nonprintables
 *              and no other. What happens to characters > 8 bit in the 
 *              database? That depends on what the charset of the column is.
 * 2011.05.13 - Introduced check
 * 2011.05.30 - Introduced Check.cannotHappen() to replace a local throw
 * 2011.06.22 - Slight rewrites and added more calls to Check
 * 2013.02.18 - Added FORMAT_FOR_CANONICIZATION
 *              Added stringifyForCanonicization()
 *              Added destroyComments()
 *              Added hashElement() and hashElementRecursively()
 * 2013.02.19 - Reviewed again as regarding hashElement
 * 2013.03.14 - Removed use of "XML" base class and the render() functions.
 *              Everything is now done via JDom.
 *              Renamed XMLHelper --> JDomHelper
 *                                
 * TODO: Not all the formats are adapted to the database, but might be used
 * for it...
 ******************************************************************************/

public class JDomHelper {

    public final static String EOL = "\r\n"; // but see http://www.w3.org/TR/2004/REC-xml-20040204/#sec-line-ends

    /**
     * Raw Format: performs no whitespace changes, use the UTF-8 encoding, 
     * do not expand empty elements, includes the declaration and encoding, 
     * uses the default entity escape strategy.
     */

    private final static Format FORMAT_RAW = Format.getRawFormat();

    /**
     * Pretty Format: perform whitespace beautification with 2-space indents,
     * use the UTF-8 encoding, do not expand empty elements, include the
     * declaration and encoding, and uses the default entity escape strategy.
     */

    private final static Format FORMAT_PRETTY = Format.getPrettyFormat();

    /**
     * Compact Format: perform whitespace normalization (left and right trim plus internal
     * whitespace is normalized to a single space), use the UTF-8 encoding,
     * do not expand empty elements, include the declaration and encoding, and 
     * uses the default entity escape strategy.
     */

    private final static Format FORMAT_COMPACT = Format.getCompactFormat();

    /**
     * "Database" Format: is the raw format with a special escape strategy
     */

    private final static Format FORMAT_DB = Format.getRawFormat().setEscapeStrategy(new EscapeStrategy() {

        // encode everything not ASCII to avoid various encoding problems on the connection and the columns
        // note that the JDOM XMLFormatter has some special behaviour regarding /n (not encoded), /r (encoded) and
        // /r/n (encoded to #xD/r/n, which is wrong)

        @Override
        public boolean shouldEscape(char ch) {
            return (ch < 32 || 127 <= ch);

        }
    });

    /**
     * "Flat" Format: is the compact format with a special escape strategy
     */

    private final static Format FORMAT_FOR_CANONICIZATION = Format.getCompactFormat().setEscapeStrategy(new EscapeStrategy() {

        // encode everything not ASCII to avoid various encoding problems on the connection and the columns
        // note that the JDOM XMLFormatter has some special behaviour regarding /n (not encoded), /r (encoded) and
        // /r/n (encoded to #xD/r/n, which is wrong)

        @Override
        public boolean shouldEscape(char ch) {
            return (ch < 32 || 127 <= ch);

        }
    });

    /**
     * Stringify the XML structure, return it as a monoline String. MODIFIES TEXT!
     * Used mainly in debugging to print the XML structure.
     * For any text content, the leading and trailing whitespace is **removed**, internal newlines are
     * **removed** and sequences of whitespace is reduced to a single whitespace. 
     */

    public static String stringifyCompactly(Element jdomElement) {
        // don't care about the 'encoding' as we do NOT add the standard xml header
        // ("<?xml version="1.0" encoding="UTF-8"?>") at all and as we deal in 'Strings'
        // which are post-encoding!
        XMLOutputter outputter = new XMLOutputter(FORMAT_COMPACT);
        StringWriter sw = new StringWriter();
        try {
            outputter.output(jdomElement, sw);
        } catch (IOException exe) {
            Check.cannotHappen(exe);
        }
        return sw.toString();
    }

    /**
     * Stringify the XML structure, return it as a multiline String. Used mainly in debugging to print the XML
     * structure. Note that for any text content, the leading and trailing whitespace is **removed**, though Newlines
     * inside the text are preserved.
     */

    public static String stringifyPrettily(Element jdomElement) {
        // use the pretty format, please
//        Format format = Format.getPrettyFormat();
        // don't care about the 'encoding' as we do NOT add the standard xml header
        // ("<?xml version="1.0" encoding="UTF-8"?>") at all and as we deal in 'Strings'
        // which are post-encoding!
        XMLOutputter outputter = new XMLOutputter(FORMAT_PRETTY);
        StringWriter sw = new StringWriter();
        try {
            outputter.output(jdomElement, sw);
        } catch (IOException exe) {
            Check.cannotHappen(exe);
        }
        return sw.toString();
    }

    /**
     * Stringify the XML structure, return it as a multiline String. Used mainly in debugging to print the XML
     * structure. Note that for any text content, the leading and trailing whitespace is **removed**, though Newlines
     * inside the text are preserved.
     */

    public static String stringifyPrettily(org.jdom.Document jdomDocument) {
        // don't care about the 'encoding' as we do NOT add the standard xml header
        // ("<?xml version="1.0" encoding="UTF-8"?>") at all and as we deal in 'Strings'
        // which are post-encoding!
        XMLOutputter outputter = new XMLOutputter(FORMAT_PRETTY);
        StringWriter sw = new StringWriter();
        try {
            outputter.output(jdomDocument, sw);
        } catch (IOException exe) {
            Check.cannotHappen(exe);
        }
        return sw.toString();
    }

    /**
     * Formatting according to the JDOM doc:
     * "Returns a new Format object that performs no whitespace changes, uses
     * the UTF-8 encoding, doesn't expand empty elements, includes the
     * declaration and encoding, and uses the default entity escape strategy.
     * Tweaks can be made to the returned Format instance without affecting
     * other instances."
     */

    public static String stringifyRawly(Element jdomElement) {
        XMLOutputter outputter = new XMLOutputter(FORMAT_RAW);
        StringWriter sw = new StringWriter();
        try {
            outputter.output(jdomElement, sw);
        } catch (IOException exe) {
            Check.cannotHappen(exe);
        }
        return sw.toString();
    }

    /**
     * Formatting for the Database. This yields a "raw formatted" XML string,
     * i.e. properly escaped XML which has no changes in whitespace
     * but which has every codepoint outside of the range [32,126] properly escaped
     * with XML entities. /n and is not translated and /r/n is wrongly translated;
     * actually, on should make sure that /r/n is translated to /rn first... TODO 
     */

    public static String stringifyForDatabase(Element jdomElement) {
        XMLOutputter outputter = new XMLOutputter(FORMAT_DB);
        StringWriter sw = new StringWriter();
        try {
            outputter.output(jdomElement, sw);
        } catch (IOException exe) {
            Check.cannotHappen(exe);
        }
        return sw.toString();
    }

    /**
     * Extra flat formatting
     */

    public static String stringifyForCanonicization(Element jdomElement) {
        XMLOutputter outputter = new XMLOutputter(FORMAT_FOR_CANONICIZATION);
        StringWriter sw = new StringWriter();
        try {
            outputter.output(jdomElement, sw);
        } catch (IOException exe) {
            Check.cannotHappen(exe);
        }
        return sw.toString();
    }

    /**
     * Destroy "commentary" nodes recursively underneath Element
     * Returns the number of destructions
     */

    public static int destroyComments(Element element) {
        Check.notNull(element, "element");
        int res = 0;
        List<Content> destroyThese = null;
        for (Object it : element.getContent()) {
            if (it instanceof Comment) {
                if (destroyThese == null) {
                    destroyThese = new LinkedList<Content>();
                }
                destroyThese.add((Content) it);
            }
        }
        if (destroyThese != null) {
            res += destroyThese.size();
            for (Content it : destroyThese) {
                element.removeContent(it);
            }
        }
        for (Object it : element.getChildren()) {
            res += destroyComments((Element) it);
        }
        return res;
    }

    /**
     * Hashing of a JDOM Tree rooted at "element" into a HEX string using SHA-1
     * Commentary nodes are disregarded; attributes are canonically sorted.
     * If the passed "buf" is not null, it is filled with a debug string showing
     * what has been checked in what order.
     */

    public static String hashElement(Element element, StringBuilder buf) {
        Check.notNull(element, "element");
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException exe) {
            Check.cannotHappen(exe);
        }
        assert md != null;
        try {
            hashElementRecursively(md, buf, element);
        } catch (UnsupportedEncodingException exe) {
            Check.cannotHappen(exe);
        }
        return ByteCoding.toHexString(md.digest());
    }

    /**
     * Recursively traverse tree, updating the "message digest" and possibly the buf if it is not null.
     * In principle one could build a text representation, then hash that. However, in order to reduce
     * memory footprint, we hash continuously into the MessageDigest buffer and only accumulate a text
     * represntation (which is not exactly what was hashed) in "buf" is not null...
     */

    private static void hashElementRecursively(MessageDigest md, StringBuilder buf, Element element) throws UnsupportedEncodingException {
        assert md != null;
        assert element != null;
        String charset = "UTF-8";
        //
        // Update with the name of the current "element"
        //
        {
            md.update((byte) 0);
            md.update(element.getQualifiedName().getBytes(charset));
            md.update((byte) 1);
            if (buf != null) {
                buf.append("[");
                buf.append(element.getQualifiedName());
                buf.append("]");
            }
        }
        //
        // If there are attributes, sort them trivially by their name, then update
        // Note that there are generally only 1 or 2 attributes
        //
        {
            List<?> attributes = element.getAttributes();
            if (!attributes.isEmpty()) {
                List<String> attrNames = new ArrayList<String>(attributes.size());
                for (Object obj : attributes) {
                    Attribute attr = (Attribute) obj;
                    attrNames.add(attr.getName());
                }
                Collections.sort(attrNames);
                for (String name : attrNames) {
                    String value = element.getAttribute(name).getValue();
                    md.update((byte) 2);
                    md.update(name.getBytes(charset));
                    md.update((byte) 3);
                    md.update(value.getBytes(charset));
                    md.update((byte) 4);
                    if (buf != null) {
                        buf.append("[");
                        buf.append(name);
                        buf.append(",");
                        buf.append(value);
                        buf.append("]");
                    }
                }
            }
        }
        //
        // There may be text; however it it is only whitespace, just consider that there is NO TEXT.
        // Otherwise, the text is used "as is", with inner CR, LF etc. Nothing is trimmed.
        //
        {
            String text = element.getText();
            if (!isOnlyWhitespace(text)) {
                md.update((byte) 5);
                md.update(text.getBytes(charset));
                md.update((byte) 6);
                if (buf != null) {
                    buf.append("[");
                    buf.append(text);
                    buf.append("]");
                }
            }
        }
        //
        // Recursive descent; comment nodes, processing instructions, entity references are disregarded
        //
        {
            List<?> children = element.getChildren();
            for (Object obj : children) {
                if (buf != null) {
                    buf.append(">");
                }
                md.update((byte) 7);
                hashElementRecursively(md, buf, (Element) obj);
                md.update((byte) 8);
                if (buf != null) {
                    buf.append("<");
                }
            }
        }
    }

    /**
     * Helper: is this only whitespace? The empty string IS only whitespace
     */

    private static boolean isOnlyWhitespace(String x) {
        assert x != null;
        int len = x.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(x.charAt(i))) {
                return false; // OUTTA HERE; not whitespace only
            }
        }
        return true;
    }

    /**
     * CHeck for emptyness
     */

    public static boolean isEmptyElement(Element element) {
        return element != null && element.getAttributes().isEmpty() && element.getChildren().isEmpty();
    }

    /**
     * Helper used in constructing query, it hashes the "element" into a property name 
     * while disregarding text whitespace and XML comments. Additionally, the trivial element
     * give rise to a special hash which is not a hexadecimal value.
     */

    public static PropertyName hashElement(Element element) {
        Check.notNull(element, "element");
        // Note that PropertyName will lowercase the string
        if (isEmptyElement(element)) {
            return new PropertyName("empty");
        } else {
            StringBuilder buf = null;
            String res = JDomHelper.hashElement(element, buf);
            // this is a hexadecimal string, one might consider using Base64 instead
            return new PropertyName(res);
        }
    }
}
