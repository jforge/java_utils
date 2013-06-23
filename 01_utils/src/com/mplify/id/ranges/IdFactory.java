package com.mplify.id.ranges;

import com.mplify.id.AbstractId;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2009, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * An interface for something that can create a subclass of AbstractId
 * 
 * 2009.12.18 - Moved out of RangeSequence to its own toplevel class
 * 2010.09.27 - Moved from the specialized project "70_msgserver_cli" 
 *              to project "04_core_low" and package "com.mplify.id_ranges".
 ******************************************************************************/

public interface IdFactory<T extends AbstractId> {
    
    public T make(Integer id);
    
}