package com.mplify.properties;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A marker interface for "PropertyHandling" classes, which collect a 
 * reference to "properties" and a reference to "property names" and export
 * several methods that read properties.
 * 
 * 2010.10.31 - Created
 ******************************************************************************/

public interface PropertyHandling {

    public PropertiesReader getProperties();

    
}
