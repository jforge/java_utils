package com.mplify.logic

import org.jdom.Element;
import org.jdom.Attribute
import org.jdom.Element

import com.mplify.checkers._check
import com.mplify.enums.Troolean
import com.mplify.id.AbstractId
import com.mplify.properties.PropertyName
import com.mplify.properties.PropertyName;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2013, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Parsing a "preprocessing mode" 
 *
 * 2013.01.29 - Done
 * 2013.02.01 - Reinvented as enum
 ******************************************************************************/

enum MatchMode {

    MATCH, FIND

}