package com.mplify.properties;

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
 * A nicer naming of properties than the raw string. This has not yet seen
 * much distribution.
 * 
 * 2009.01.19 - Created
 * 2010.10.21 - Remove all the specialization-of-property code which complexifies 
 *              things needlessly and is also dynamically resolved, thus
 *              messing up saearching of code.
 * 2013.02.01 - Added make()              
 ******************************************************************************/

public class PropertyName extends AbstractName {

    // Maybe this should be a DottedAbstractName
    
    public PropertyName(String name) {
        super(name);
    }
    
    // sugar
    
    public static PropertyName make(String name) {
        return new PropertyName(name);
    }
    
}
