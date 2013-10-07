package com.mplify.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.mplify.logging.LoglevelAid;
import com.mplify.logging.LoglevelAid.Loglevel;
import com.mplify.logging.Story;
import com.mplify.logging.storyhelpers.Doublet;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A container for all the properties that might be of interest.
 * Constructors of "property name" instances may want to dump their keys
 * into the lister so that a complete list of the keys expected exist.
 *
 * Note that "property key" and "property name" are used with the same meaning
 * 
 * This is a "static" because it's really kinda global...
 * 
 * 2010.08.20 - Created
 ******************************************************************************/

public class PropertyNameLister {

    private final static Set<String> propertyNames = new HashSet();

    public static void listWhichPropertiesExist(PropertiesReader properties, Logger logger, Loglevel level) {
        assert properties != null;
        assert logger != null;
        assert level != null;
        if (LoglevelAid.isEnabledFor(logger,level)) {
            synchronized (propertyNames) {
                Story story = new Story();
                List<String> l = new ArrayList(propertyNames);
                Collections.sort(l);
                for (String s : l) {
                    story.add(new Doublet(s, Boolean.valueOf(properties.existsNonemptyKeyP(s))));
                }
                story.write(logger, level);
            }
        }
    }

    public static void addPropertyNames(Set<String> names) {
        if (names == null) {
            return;
        }
        synchronized (propertyNames) {
            propertyNames.addAll(names);
        }
    }
}
