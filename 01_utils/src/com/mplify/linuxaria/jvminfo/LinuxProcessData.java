package com.mplify.linuxaria.jvminfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.mplify.logging.Story;
import com.mplify.logging.storyhelpers.Doublet;
import com.mplify.mbeans.MBeanMarker;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2007, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Implementation of an MBean which exports various process parameters read
 * from the Linux /proc filesystem (thus works on Linux only)
 * 
 * Data is read from /proc and buffered. A re-read can either be triggered by 
 * calling re-read explicitly or else by waiting until a daemon thread
 * decides to re-read the data by itself. Access to the underlying buffered
 * data is synchronized (the lock object being "collectionLock". A daemon
 * thread is supposed to animate run() of this class to ensure regular
 * re-collection of data.
 * 
 * In order for JMX to recognize this as a StandardMBean implementing
 * "LinuxProcessDataMBean", the class MUST be named "LinuxProcessData"
 * 
 * 2010.02.18 - Created for new JMX project 
 * 2011.03.30 - Added toFlatStory()
 ******************************************************************************/

public class LinuxProcessData implements LinuxProcessDataMBean, Runnable, MBeanMarker {

    /**
     * Structure describing the data obtainable at the MBean interface
     */

    private static CompositeType myType;

    /**
     * Immutable collections of the keys one can use in the MBean
     */

    private static Set<String> keySet;
    private static List<String> sortedKeyList;

    static {
        try {
            myType = getMyType();
        } catch (Exception exe) {
            throw new IllegalStateException("Constructing CompositeType resulted in exception", exe);
        }
        {
            keySet = Collections.unmodifiableSet(new HashSet<String>(myType.keySet()));
        }
        {
            List<String> _sortedKeyList = new ArrayList<String>(keySet);
            Collections.sort(_sortedKeyList);
            sortedKeyList = Collections.unmodifiableList(_sortedKeyList);
        }
    }

    /**
     * Information obtained during the last collection. (null) if no information has actually been obtained yet. There
     * is also a "lock object" to make sure two collections don't run at the same time
     */

    private ProcCollection collection = null;
    private Object collectionLock = new Object();

    /**
     * Timing
     */

    private long lastCollectionTime = 0; // when "procCollection" was created
    private long nextCollectionTime = 0; // when "procCollection" will be created next
    private long collectionInterval_ms = 5000; // time between collections; always at least 500 ms

    /**
     * Building "myType"
     */

    private static CompositeType getMyType() throws OpenDataException {
        String typeName = LinuxProcessData.class.getName();
        String desc = "Information about the current process extraced from the Linux /proc filesystem.";
        int fieldCount = 10;
        String[] itemNames = new String[fieldCount];
        String[] itemDescs = new String[fieldCount];
        OpenType<?>[] itemTypes = new OpenType<?>[fieldCount];
        {
            itemNames[0] = "minorFaultSum";
            itemDescs[0] = "Minor faults for the process since process inception";
            itemTypes[0] = SimpleType.LONG;
        }
        {
            itemNames[1] = "majorFaultSum";
            itemDescs[1] = "Major faults for the process since process inception";
            itemTypes[1] = SimpleType.LONG;
        }
        {
            itemNames[2] = "childrenMinorFaultSum";
            itemDescs[2] = "Minor faults for the process children since process inception";
            itemTypes[2] = SimpleType.LONG;
        }
        {
            itemNames[3] = "childrenMajorFaultSum";
            itemDescs[3] = "Major faults for the process children since process inception";
            itemTypes[3] = SimpleType.LONG;
        }
        {
            itemNames[4] = "utimeSum";
            itemDescs[4] = "Number of jiffies (1/100th sec) spent in user mode since process inception";
            itemTypes[4] = SimpleType.LONG;
        }
        {
            itemNames[5] = "stimeSum";
            itemDescs[5] = "Number of jiffies (1/100th sec) spent in kernel mode since process inception";
            itemTypes[5] = SimpleType.LONG;
        }
        {
            itemNames[6] = "childrenUtimeSum";
            itemDescs[6] = "Number of jiffies (1/100th sec) spent in user mode for the children since process inception";
            itemTypes[6] = SimpleType.LONG;
        }
        {
            itemNames[7] = "childrenStimeSum";
            itemDescs[7] = "Number of jiffies (1/100th sec) spent in kernel mode for the children since process inception";
            itemTypes[7] = SimpleType.LONG;
        }
        {
            itemNames[8] = "vsize";
            itemDescs[8] = "Virtual memory size (obtained from thread group leader)";
            itemTypes[8] = SimpleType.BIGINTEGER;
        }
        {
            itemNames[9] = "rss";
            itemDescs[9] = "Resident set size (obtained from thread group leader)";
            itemTypes[9] = SimpleType.BIGINTEGER;
        }
        return new CompositeType(typeName, desc, itemNames, itemDescs, itemTypes);
    }

    /**
     * If a background thread shall run to collect data, use a Thread object to run a daemon thread to animate
     * Runnable.run().
     */

    @Override
    public void run() {
        for (;;) {
            sleep();
            collect();
        }
    }

    /**
     * Sleeping until time's up. Sleep locks the "collectionLock" but then waits() on it, liberating the lock while it
     * waits. At that moment, other threads can call "collect()"
     */

    private void sleep() {
        synchronized (collectionLock) {
            while (System.currentTimeMillis() < nextCollectionTime) {
                try {
                    long waitTime_ms = Math.max(1, nextCollectionTime - System.currentTimeMillis());
                    // the sleep
                    collectionLock.wait(waitTime_ms);
                } catch (Exception exe) {
                    // probably interrupted
                }
            }
        }
    }

    /**
     * To have collection of new data proceed at once, call this
     */

    @Override
    public void collect() {
        synchronized (collectionLock) {
            this.collection = ProcCollector.readProcFilesystem(); // returns null if collection fails
            this.lastCollectionTime = System.currentTimeMillis();
            this.nextCollectionTime = this.lastCollectionTime + this.collectionInterval_ms;
        }
    }

    /**
     * To set the collection interval, call this. The interval must be at least 500 ms.
     */

    @Override
    public void setCollectionInterval_ms(long collectionInterval_ms) {
        synchronized (collectionLock) {
            this.collectionInterval_ms = Math.max(500, collectionInterval_ms);
        }
    }

    /**
     * Getter for the collection interval
     */

    @Override
    public long getCollectionInterval_ms() {
        synchronized (collectionLock) {
            return this.collectionInterval_ms;
        }
    }

    /**
     * When did the last collection happen? Returns null if "never"
     */

    @Override
    public Date getLastCollectionTime() {
        synchronized (collectionLock) {
            if (lastCollectionTime == 0) {
                return null;
            } else {
                return new Date(lastCollectionTime);
            }
        }
    }

    /**
     * When will the next collection happen? Retuns null if "undefined"
     */

    @Override
    public Date getNextCollectionTime() {
        synchronized (collectionLock) {
            if (nextCollectionTime == 0) {
                return null;
            } else {
                return new Date(nextCollectionTime);
            }
        }
    }

    /**
     * Get an "Open MBean" representation of the collected data. Returns null if there is no data because there has not
     * been a collection yet or because this is not a Linux system.
     */

    @Override
    public CompositeData getProcCollection() throws OpenDataException {
        synchronized (collectionLock) {
            if (collection == null) {
                return null;
            } else {
                int limit = sortedKeyList.size();
                String[] itemNames = new String[limit];
                Object[] itemValues = new Object[limit];
                int i = 0;
                for (String itemName : sortedKeyList) {
                    itemNames[i] = itemName;
                    itemValues[i] = get(itemName);
                    i++;
                }
                return new CompositeDataSupport(myType, itemNames, itemValues);
            }
        }
    }

    /**
     * Helper to get the Object corresponding to key "key", assuming collection is not null
     */

    private Object get(String key) {
        if (key == null) {
            throw new IllegalArgumentException("The passed key is (null)");
        } else if ("".equals(key)) {
            throw new IllegalArgumentException("The passed key is the empty string");
        } else {
            if ("minorFaultSum".equals(key)) {
                return Long.valueOf(collection.minorFaultSum);
            } else if ("majorFaultSum".equals(key)) {
                return Long.valueOf(collection.majorFaultSum);
            } else if ("childrenMinorFaultSum".equals(key)) {
                return Long.valueOf(collection.childrenMinorFaultSum);
            } else if ("childrenMajorFaultSum".equals(key)) {
                return Long.valueOf(collection.childrenMajorFaultSum);
            } else if ("utimeSum".equals(key)) {
                return Long.valueOf(collection.utimeSum);
            } else if ("stimeSum".equals(key)) {
                return Long.valueOf(collection.stimeSum);
            } else if ("childrenUtimeSum".equals(key)) {
                return Long.valueOf(collection.childrenUtimeSum);
            } else if ("childrenStimeSum".equals(key)) {
                return Long.valueOf(collection.childrenStimeSum);
            } else if ("vsize".equals(key)) {
                return collection.vsize;
            } else if ("rss".equals(key)) {
                return collection.rss;
            } else {
                throw new IllegalStateException("Unhandled key '" + key + "' -- code fix needed");
            }
        }
    }

    /**
     * A method that obtains all the values and returns them as a String
     */

    @Override
    public String toFlatStory() {
        synchronized (collectionLock) {
            if (this.collection != null) {
                Story story = this.collection.toStory();
                story.add(0, new Doublet("Collected at", new Date(this.lastCollectionTime)));
                return story.toString();
            } else {
                return "[nothing collected yet]";
            }
        }
    }

}
