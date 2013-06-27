package com.mplify.xml;

import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdom.Content;
import org.jdom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
 * A set of functions used by DealerDevices when chekcing/parsing XML requests
 * or responses.
 *
 * 2002.03.28 - Extracted from DealerDeviceC and set up as a top-level class.
 * 2004.05.10 - Reactivated
 * 2004.11.21 - Slight modification of parseXMLStringIntoDocument; now
 *              throws XMLParsingException
 * 2005.06.18 - Moved to pure JAXP (instead of Xerces). Classes from m3p.env
 *              are gone and some surviving code (a few lines out of two
 *              huge classes of unsure use) have been migrated here. It is
 *              now 02:00 in the morning.
 * 2008.11.18 - Additions
 * 2011.02.03 - Introduced DomNodeType. This replaces a simple print function.
 * 2011.02.04 - DocumentBuilderFactory now constructed statically.
 * 2011.05.13 - Slight review. Addec Check calls, also unified code with
 *              "LazyBody"; JDOM construction is now done exclusively here.
 * 2013.01.14 - Functions renamed for consistency              
 ******************************************************************************/

public class XMLParsing {

    private final static String CLASS = XMLParsing.class.getName();
//    private final static Logger LOGGER_transformDocument = LoggerFactory.getLogger(CLASS + ".transformDocument");
    private final static Logger LOGGER_static = LoggerFactory.getLogger(CLASS + ".static_initializer");

    /**
     * Static DocumentBuilderFactory; creating a new one might be expensive
     */

    private static final DocumentBuilderFactory documentBuilderFactory_namespaceAware;
    private static final DocumentBuilderFactory documentBuilderFactory_namespaceUnaware;

    static {
        Logger logger = LOGGER_static;
        // Get a DocumentBuilderFactory whose actual implementation is defined by the system setup (JAXP)
        // Also read: http://www.w3.org/blog/systeam/2008/02/08/w3c_s_excessive_dtd_traffic
        {
            documentBuilderFactory_namespaceAware = DocumentBuilderFactory.newInstance();
            documentBuilderFactory_namespaceAware.setNamespaceAware(true);
            try {
                documentBuilderFactory_namespaceAware.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            } catch (ParserConfigurationException exe) {
                logger.warn("While setting feature on " + documentBuilderFactory_namespaceAware.getClass().getName(), exe);
            }
        }
        {
            documentBuilderFactory_namespaceUnaware = DocumentBuilderFactory.newInstance();
            documentBuilderFactory_namespaceUnaware.setNamespaceAware(false);
            try {
                documentBuilderFactory_namespaceUnaware.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            } catch (ParserConfigurationException exe) {
                logger.warn("While setting feature on " + documentBuilderFactory_namespaceUnaware.getClass().getName(), exe);
            }
        }
    }

    /**
     * Constructor is private so this class cannot be instantiated
     */

    private XMLParsing() {
        // Unreachable
    }

    /**
     * An enumeration of NodeTypes, remapped from the antediluvian numeric W3C values
     */

    public static enum DomNodeType {

        Element(org.w3c.dom.Element.ELEMENT_NODE),

        Attribute(org.w3c.dom.Element.ATTRIBUTE_NODE),

        Text(org.w3c.dom.Element.TEXT_NODE),

        CdataSection(org.w3c.dom.Element.CDATA_SECTION_NODE),

        EntityReference(org.w3c.dom.Element.ENTITY_REFERENCE_NODE),

        Entity(org.w3c.dom.Element.ENTITY_NODE),

        ProcessingInstruction(org.w3c.dom.Element.PROCESSING_INSTRUCTION_NODE),

        Comment(org.w3c.dom.Element.COMMENT_NODE),

        Document(org.w3c.dom.Element.DOCUMENT_NODE),

        DocumentType(org.w3c.dom.Element.DOCUMENT_TYPE_NODE),

        DocumentFragment(org.w3c.dom.Element.DOCUMENT_FRAGMENT_NODE),

        Notation(org.w3c.dom.Element.NOTATION_NODE);

        private final short value;

        DomNodeType(short value) {
            this.value = value;
        }

        public short getValue() {
            return value;
        }
    }

    private final static Map<Short, DomNodeType> domNodeTypeMap;

    static {
        Map<Short, DomNodeType> map = new HashMap();
        for (DomNodeType dnt : DomNodeType.values()) {
            map.put(Short.valueOf(dnt.getValue()), dnt);
        }
        domNodeTypeMap = Collections.unmodifiableMap(map);
    }

    /**
     * Get the DomNodeType from the raw short coming from the W3C DOM Node
     */

    public static DomNodeType obtain(short domNodeTypeRaw) {
        DomNodeType dnt = domNodeTypeMap.get(Short.valueOf(domNodeTypeRaw));
        if (dnt == null) {
            throw new IllegalArgumentException("There is no " + DomNodeType.class.getName() + " corresponding to value " + domNodeTypeRaw);
        } else {
            return dnt;
        }
    }

    /**
     * Do a recursive traversal of the DOM tree; no judgement is made about the tree's content, only information about
     * it is appended to the passed StringBuffer
     */

    public static void traverseW3cDomTree(org.w3c.dom.Node node, int indent, StringBuffer buf) {
        String indentx = LogFacilities.getSpaceString(indent);
        {
            String value = "";
            String children = "";
            String attributes = "";
            if (node.getNodeValue() != null) {
                value = " \"" + LogFacilities.mangleString(node.getNodeValue()) + "\"";
            }
            if (node.hasChildNodes()) {
                children = " children:" + node.getChildNodes().getLength();
            }
            if (node.hasAttributes()) {
                attributes = " attributes:" + node.getAttributes().getLength();
            }
            buf.append(indentx);
            buf.append(obtain(node.getNodeType()).toString());
            buf.append(":");
            buf.append(node.getNodeName());
            buf.append(value);
            buf.append(children);
            buf.append(attributes);
            buf.append("\n");
        }
        //
        // extract attributes, then traverse some more
        //
        {
            org.w3c.dom.NamedNodeMap attributes = node.getAttributes();
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    org.w3c.dom.Node attribute = attributes.item(i);
                    traverseW3cDomTree(attribute, indent + 2, buf);
                }
            }
        }
        //
        // extract children, then traverse some more
        //
        {
            org.w3c.dom.NodeList children = node.getChildNodes();
            if (children != null) {
                for (int i = 0; i < children.getLength(); i++) {
                    org.w3c.dom.Node child = children.item(i);
                    traverseW3cDomTree(child, indent + 2, buf);
                }
            }
        }
    }

    /**
     * "Analyze XML". This means parsing the XML string, then verifying it according to a DTD (if 'verifyXML' is set), and
     * alternatively traversing the document tree, printing out the nodes to "logger" at level info (if 'traverseXML' is
     * set). Currently, no DTD verification is done. If neither 'traverseXML' and 'verifyXML' are set, this is a NOP.
     */

    public static void analyzeXmlString(String str, boolean traverseXML, boolean verifyXML, Logger logger) throws Exception {
        if (traverseXML || verifyXML) {
            Document document = parseXmlStringIntoW3cDomDocument(str);
            //
            // If so desired, traverse the DOM tree, printing its structure
            //
            if (traverseXML) {
                StringBuffer buf = new StringBuffer();
                traverseW3cDomTree(document, 0, buf);
                logger.info(buf.toString());
            }
            //
            // If so desired, verify according to the DTD (not done currently); actually this should be done by the parser
            //
            if (verifyXML) {
                // TO BE DONE
            }
        }
    }

    /**
     * Create the "DocumentBuilder" (a DOM Parser as configured in the JVM)
     * See http://download.oracle.com/javase/6/docs/api/index.html?overview-summary.html 
     */
    
    private static DocumentBuilder createDocumentBuilder(boolean withNamespaceAwareness) throws XMLParsingException {
        DocumentBuilder docBuilder;
        try {
            if (withNamespaceAwareness) {
                synchronized (documentBuilderFactory_namespaceAware) {
                    docBuilder = documentBuilderFactory_namespaceAware.newDocumentBuilder();
                }
            } else {
                synchronized (documentBuilderFactory_namespaceUnaware) {
                    docBuilder = documentBuilderFactory_namespaceUnaware.newDocumentBuilder();
                }
            }
            return docBuilder;
        } catch (ParserConfigurationException exe) {
            throw new XMLParsingException("Could not create a document builder", exe);
        }        
    }
    
    /**
     * Parse an XML String into a W3C DOM Document using system-provided (JAXP) parser. Returns the document or throws
     * with an XMLParsingException. Has no namespace awareness! Note that XSLT requires namespace support. Attempting to
     * transform a DOM that was not contructed with a namespace-aware parser may result in errors. Thrown exceptions
     * do not contain the passed text.
     */

    public static Document parseXmlStringIntoW3cDomDocument(String xmlText) throws XMLParsingException {
        boolean withNamespaceAwareness;
        return parseXmlStringIntoW3cDomDocument(xmlText, withNamespaceAwareness=false);
    }

    /**
     * Parse an XML String into a W3C DOM Document using system-provided (JAXP) parser. Returns the document or throws
     * with an XMLParsingException. Namespace awareness can be switched on/off. Note that XSLT requires namespace
     * support. Attempting to transform a DOM that was not contructed with a namespace-aware parser may result in
     * errors.  Thrown exceptions do not contain the passed text.
     */

    public static Document parseXmlStringIntoW3cDomDocument(String xmlText, boolean withNamespaceAwareness) throws XMLParsingException {
        Check.notNull(xmlText,"xml text");
        DocumentBuilder docBuilder = createDocumentBuilder(withNamespaceAwareness);
        // Assign an error handler so that we know what's going on
        XMLErrorHandler errorHandler = new XMLErrorHandler();
        docBuilder.setErrorHandler(errorHandler);
        // package the "source of characters", i.e. the passed String
        StringReader reader = new StringReader(xmlText);
        InputSource source = new InputSource(reader);
        Document doc;
        try {
            doc = docBuilder.parse(source); // may throw
        } catch (Exception exe) {
            throw new XMLParsingException("While parsing the XML text", exe);
        }
        // If there was an error (and we have not yet gotten out with an
        // exception), do so now.
        if (errorHandler.isSomethingBadHappened()) {
            throw new XMLParsingException("The parser could not correctly parse the XML text");
        } else {
            return doc;
        }
    }

    /**
     * Parse the XML text directly into a JDOM "document" using a DOM Parser
     */

    public static org.jdom.Document xmlStringToJdomDocument(String xmlText) throws XMLParsingException {
        // Parse into a W3C DOM Document first. Note that a null "xmlText" is not allowd.
        // This works even if the text actually just represents an Element!
        // This works even if the text has a (useless) <?xml?> header giving an encoding
        org.w3c.dom.Document domDoc = parseXmlStringIntoW3cDomDocument(xmlText);
        // Now transform into a JDOM Document
        org.jdom.input.DOMBuilder domBuilder = new org.jdom.input.DOMBuilder();
        return domBuilder.build(domDoc);
    }

    /**
     * Find a first element in a JDOM document. One may specify which one exactly by giving the "expected toplevel element name" or (null)
     * if one doesn't care about the actual name. The "xml text" cannot be null, however.
     */

    public static Element parseThenFindToplevelJdomElement(String xmlText, String expectedToplevelElementName, boolean throwIfNoneFound) throws XMLParsingException {
        return findToplevelJdomElement(xmlStringToJdomDocument(xmlText), expectedToplevelElementName, throwIfNoneFound);
    }
    
    /**
     * Old migrated Antoine stuff, heavily simplified. Also uses an proper ErrorListener.
     */

    /*
    public static String transformDocument(String xslSource, String xmlSource) throws XMLParsingException, XMLTransformingException {
        Document xml = XMLParsing.parseXMLStringIntoDocument(xmlSource);
        Document xsl = XMLParsing.parseXMLStringIntoDocument(xslSource);
        if (LOGGER_transformDocument.isDebugEnabled()) {
            LOGGER_transformDocument.info("xmlSource:\n" + xmlSource);
            LOGGER_transformDocument.info("xslSource:\n" + xslSource);
        }
        /*
         * { StringBuffer buf=new StringBuffer(); traverseDOMTree(xml,0,buf);
         * LOGGER_transformDocument.info("xml:\n"+buf.toString()); } { StringBuffer buf=new StringBuffer();
         * traverseDOMTree(xsl,0,buf); LOGGER_transformDocument.info("xsl:\n"+buf.toString()); }
         *
        TransformerFactory trafoFactory = TransformerFactory.newInstance();
        Transformer trafo;
        try {
            // trafo = trafoFactory.newTransformer(new DOMSource(xsl));
            StringReader xslStringReader = new StringReader(xslSource);

            StreamSource xslStreamSource = new StreamSource(xslStringReader);
            trafo = trafoFactory.newTransformer(xslStreamSource);
            LOGGER_transformDocument.debug("trafo:\n" + trafo);
        } catch (TransformerConfigurationException exe) {
            throw new XMLTransformingException("While getting Transformer", exe);
        }
        StringWriter writer = new StringWriter();
        // create a listener that throws on error or fatal error
        XSLTErrorListener listener = new XSLTErrorListener();
        trafo.setErrorListener(listener);
        try {
            trafo.transform(new DOMSource(xml), new StreamResult(writer));
            if (LOGGER_transformDocument.isDebugEnabled()) {
                LOGGER_transformDocument.info("writer.getBuffer().toString():\n" + writer.getBuffer().toString());
            }
            return writer.getBuffer().toString();
        } catch (Exception exe) {
            throw new XMLTransformingException("While transforming", exe);
        }
    }
    */

    /**
     * Helper used to find the first ELEMENT with a given name in a W3C DOM document's immediate children.
     * "expectedToplevelElementName" cannot be null or empty. 
     */

    public static Node findToplevelW3cDomElement(Document w3cDomDoc, String expectedToplevelElementName) {
        Check.notNull(w3cDomDoc,"DOM document");
        Check.notNull(expectedToplevelElementName,"expected toplevel element name");
        Check.notNullAndNotOnlyWhitespace(expectedToplevelElementName, "expected toplevel element name");
        String etn = expectedToplevelElementName.trim();
        Node theNode = null;
        {
            NodeList childNodes = w3cDomDoc.getChildNodes();
            int i = 0;
            Node child = null;
            while (theNode == null && (child = childNodes.item(i++)) != null) {
                if (etn.equals(child.getNodeName())) {
                    theNode = child;
                }
            }
        }
        if (theNode == null) {
            throw new IllegalArgumentException("The passed 'DOM document' does not contain a toplevel element with name '" + etn + "'");
        }
        return theNode;
    }

    /**
     * Find a first element in a JDOM document. One may specify which one exactly by giving the "expected toplevel element name" or (null)
     * if one doesn't care about the actual name.
     */

    public static Element findToplevelJdomElement(org.jdom.Document jdomDoc, String expectedToplevelElementName, boolean throwIfNoneFound) {
        Check.notNull(jdomDoc,"JDOM document");        
        List<?> contentList = jdomDoc.getContent();
        assert contentList != null;
        for (Object obj : contentList) {
            Content content = (Content) obj;
            if (content instanceof Element) {
                Element element = (Element) content;
                if (expectedToplevelElementName == null) {
                    // return this at once
                    return (Element) content;
                }
                else if (expectedToplevelElementName.equals(element.getName())) { 
                    // return this only if the element tag matches, taking the first found
                    return element;
                }
            }
        }
        if (throwIfNoneFound) {
            if (expectedToplevelElementName == null) {
                throw new IllegalStateException("Could not find any toplevel element in the passed JDOM document at all");
            }
            else {
                throw new IllegalStateException("Could not find any toplevel element named '" + expectedToplevelElementName + "' in the passed JDOM document");
            }
        } else {
            return null;
        }
    }
    
    /**
     * Extract a toplevel element from the XML text, which may be just an element, and which may have a (useless) <?xml?> header
     * giving an encoding. Pass the expected name of the toplevel element. Throws IllegalArgumentException if no such toplevel
     * element could be found.
     */

    public static Element extractToplevelElement(String xmlText, String expectedToplevelElementName) throws XMLParsingException {
        Check.notNull(xmlText,"xml text");
        Check.notNull(expectedToplevelElementName,"expected toplevel name");
        Check.notNullAndNotOnlyWhitespace(expectedToplevelElementName,"expected toplevel name");
        //               
        // Parse 'xml text' into a W3C DOM Document
        // This works even if the text actually just represents an Element!
        // This works even if the text has a (useless) <?xml?> header giving an encoding
        //
        org.w3c.dom.Document domDoc = XMLParsing.parseXmlStringIntoW3cDomDocument(xmlText);
        //
        // Transform the W3C DOM doc into a JDOM doc
        //
        org.jdom.input.DOMBuilder domBuilder = new org.jdom.input.DOMBuilder();
        org.jdom.Document jdomDoc = domBuilder.build(domDoc);
        //
        // Find a given toplevel element in the content, which must exist
        //
        boolean throwIfNoneFound;
        Element element = findToplevelJdomElement(jdomDoc, expectedToplevelElementName.trim(), throwIfNoneFound=false);       
        if (element == null) {
            throw new IllegalArgumentException("No '" + expectedToplevelElementName + "' element found in '" + LogFacilities.mangleString(xmlText) + "'");
        }
        return element;
    }
    
    /**
     * Transform a string into XML-safe characters by removing non-XML characters and escaping others. The text can be
     * moved down to ASCII by setting the appropriate flag; all characters above 0x7F are then escaped. Non-ISO-8859-1
     * characters are suppressed, as are non-XML characters. Note that the resulting string has no longer an LF or CR or
     * TAB characters, they have all been escaped!
     */

    public static String safeEscape_ISO_8859_1_toXML(String text) {
        return safeEscape_ISO_8859_1_toXML(text, false);
    }

    public static String safeEscape_ISO_8859_1_toXML(String text, boolean toAscii) {
        return safeEscape_ISO_8859_1_toXML(new StringBuffer(), text, toAscii);
    }

    public static String safeEscape_ISO_8859_1_toXML(StringBuffer buf, String text, boolean toAscii) {
        // Should this also be able to handle 'null'?
        int limit = text.length();
        for (int i = 0; i < limit; i++) {
            char ch = text.charAt(i);
            if (0x00 <= ch && ch <= 0x08) {
                // This is no XML
                // --> suppress without replacement
            } else if (ch == 0x09) {
                // TAB: This is valid XML; replace by escaped numeric value
                buf.append("&#09;");
            } else if (ch == 0x0A) {
                // LINEFEED: This is valid XML; replace by escaped numeric value
                buf.append("&#10;");
            } else if (ch == 0x0B || ch == 0x0C) {
                // This is no XML
                // --> suppress without replacement
            } else if (ch == 0x0D) {
                // CARRIAGE RETURN: This is valid XML; replace by escaped numeric value
                buf.append("&#13;");
            } else if (0x0E <= ch && ch <= 0x1F) {
                // This is no XML
                // --> suppress without replacement
            } else if (0x20 <= ch && ch <= 0x7F) {
                // This is valid XML, but needs some 'post-replacement'
                if (ch == '<') {
                    // buf.append("&#60;");
                    buf.append("&lt;");
                } else if (ch == '>') {
                    // buf.append("&#62;");
                    buf.append("&gt;");
                } else if (ch == '&') {
                    // buf.append("&#38;");
                    buf.append("&amp;");
                } else if (ch == '"') {
                    // buf.append("&#34;");
                    buf.append("&quot;");
                } else if (ch == '\'') {
                    // buf.append("&#39;");
                    buf.append("&apos;");

                } else {
                    buf.append(ch);
                }
            } else if (0x80 <= ch && ch <= 0x9F) {
                // In ISO-8859-1, these characters don't exist
                // --> suppress without replacement
            } else if (ch == 0xA0) {
                // ISO-8859-1 non-breakable space
                // -> insert the numeric character reference (see also XML def 1.0, paragraph 4.1)
                buf.append("&#160;");
            } else if (0xA1 <= ch && ch <= 0xFF) {
                if (toAscii) {
                    buf.append("&#");
                    buf.append((int) (ch));
                    buf.append(";");
                } else {
                    buf.append(ch);
                }
            } else {
                // non-ISO-8859-1 character
                // TODO: Why not escape?????
                buf.append(".");
            }
        }
        return buf.toString();
    }

}
