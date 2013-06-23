package com.mplify.linuxaria.jvminfo;

import java.util.Date;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2007, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Declaration of an MBean which exports various process parameters read
 * from the Linux /proc filesystem (thus works on Linux only)
 * 
 * This currently is a "standard MBean"
 * 
 * 2010.02.18 - Created for new JMX project 
 * 2011.03.30 - Added toFlatStory()
 ******************************************************************************/

public interface LinuxProcessDataMBean {

    /**
     * To have collection of new data proceed at once, call this
     */

    public void collect();
    
    /**
     * To set the collection interval, call this. The interval must be at least 500 ms.
     */

    public void setCollectionInterval_ms(long collectionInterval_ms);

    /**
     * Getter for the collection interval
     */
    
    public long getCollectionInterval_ms();
        
    /**
     * When did the last collection happen? Returns null if "never"
     */

    public Date getLastCollectionTime();
    
    /**
     * When will the next collection happen? Retuns null if "undefined"
     */

    public Date getNextCollectionTime();

    /**
     * Get an "Open MBean" representation of the collected data. Returns null if there is no data.
     */

    public CompositeData getProcCollection() throws OpenDataException;
   
    /**
     * Get a user-readable text of the values
     */
    
    public String toFlatStory();
}
