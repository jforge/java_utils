package com.mplify.properties;

import java.util.List;
import java.util.TreeMap;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.checkers.Check;
import com.mplify.names.AbstractName;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 ******************************************************************************/

public class PayloadWithJdom<T extends AbstractName> extends Payload<T> {

    private final static String CLASS = PayloadWithJdom.class.getName();

    private final static Logger LOGGER_init = LoggerFactory.getLogger(CLASS + ".<init>");
    
    /**
     * Construct from a JDOM element. This needs a factory of T
     */

    public PayloadWithJdom(Element element, Factory<T> factory) {
        Check.notNull(element, "element");
        Check.notNull(factory, "factory");
        List<?> children = element.getChildren();
        for (Object child : children) {
            if (child instanceof Element) {
                Element childElement = (Element) child;
                String key = childElement.getName(); // not null, will be trimmed and lowercases in set()
                String value = childElement.getText(); // not null, but may be empty
                if (mapping == null) {
                    mapping = new TreeMap<T, String>();
                }
                String old = mapping.put(factory.make(key), value);
                if (old != null) {
                    LOGGER_init.warn("Duplicate key entry '" + key + "' with previous value '" + old + "' and new '" + value + "' -- replaced previous value");
                }
            }
        }
    }

    /**
     * Insert JDOM elements below the passed element, so that if the constructor is passed that element, the element can
     * be reconstructed. Returns the passed element for "chaining calls".
     */

    public Element injectJdom(Element element) {
        Check.notNull(element, "element");
        if (!isEmpty()) {
            for (T key : mapping.keySet()) {
                element.addContent(new Element(key.toString()).setText(mapping.get(key)));
            }
        }
        return element;
    }

}
