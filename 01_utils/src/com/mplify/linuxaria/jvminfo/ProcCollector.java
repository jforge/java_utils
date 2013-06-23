package com.mplify.linuxaria.jvminfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2007, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Read /proc filesystem on Linux to find out about process behaviour
 * 
 * 2004.06.21 - Created.
 * 2004.10.19 - Re-purposed for Mobilux, copied to new project
 *              "customer_mobilux_message_server"
 * 2005.03.21 - Added a way to get information from the '/proc' 
 *              filesystem under NPTL Linux 2.6.
 *              Renamed class to JvmInfoGenerator because it generates
 *              info about the JVM, not only about the VM (Virtual Memory)
 * 2005.03.24 - 64-bit address space values must be parsed with 'BigInteger'
 * 2005.06.28 - Moved the aliveness value to its own class so that this one
 *              only deals with statically querying the /proc filesystem
 * 2008.06.20 - Caught harmless "FileNotFoundException" occuring if the
 *              entry for a thread is read which is already gone, which
 *              happens generally when the process shuts down.
 * 2008.06.30 - Renamed to "ProcAccessHelper"
 * 2009.08.26 - Class moved to a new, separate package
 * 2009.11.05 - Static nested classed moved to toplevel
 *              Renamed ProcAccessHelper --> ProcAccess
 * 2010.02.18 - Reintegrated into new "JMX" project; data shall be access by
 *              MBean instead of through printout.
 *              Renamed to ProcCollector.
 * 2010.09.24 - Made public because also used in TX_Keepalive
 *               
 * TODO: Export what went wrong (the exception caught) as an array of string
 ******************************************************************************/

public class ProcCollector {

    private final static String CLASS = ProcCollector.class.getName();
    private final static Logger LOGGER_readProcFilesystem = LoggerFactory.getLogger(CLASS + ".readProcFilesystem");    

    /*
     * Unreachable constructor as this class needs no instances
     */

    private ProcCollector() {
        // unreachable
    }

    /**
     * Get information from the '/proc' filesystem, namely:
     * 
     * minorFaultSum - minor faults for the process since its inception (== sum over threads) majorFaultSum - major
     * faults for the process since its inception (== sum over threads) childrenMinorFaultSum - the same for the
     * children (== sum over threads) childrenMajorFaultSum - the same for the children (== sum over threads) utimeSum -
     * number of jiffies (1/100th sec) spent in user mode since process inception (== sum over threads) stimeSum -
     * number of jiffies (1/100th sec) spent in kernel mode since process inception (== sum over threads)
     * childrenUtimeSum - the same for the children (== sum over threads) childrenStimeSum - the same for the children
     * (== sum over threads) vsize - virtual memory size (obtained from thread group leader) rss - resident set size
     * (obtained from thread group leader)
     * 
     * If this is not such a system, the appropriate files won't be found and the update will silently fail. And return
     * null.
     */

    public static ProcCollection readProcFilesystem() {
        Logger logger = LOGGER_readProcFilesystem;
        // set up local values 
        long loc_minorFaultSum = 0;
        long loc_majorFaultSum = 0;
        long loc_childrenMinorFaultSum = 0;
        long loc_childrenMajorFaultSum = 0;
        long loc_utimeSum = 0;
        long loc_stimeSum = 0;
        long loc_childrenUtimeSum = 0;
        long loc_childrenStimeSum = 0;
        BigInteger loc_vsize = new BigInteger("0");
        BigInteger loc_rss = new BigInteger("0");
        try {
            // buf will take up the contents of the 'stat' files
            char[] buf = new char[500];
            // the 'stat' file of this thread
            File selfStat = new File("/proc/self/stat");
            if (!selfStat.exists()) {
                // not the correct Linux version 
                return null;
            }
            int actuallyRead;
            {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(selfStat)); // assume default charset 
                actuallyRead = isr.read(buf, 0, buf.length);
                isr.close();
            }
            // assume the buffer contains the Linux 'stat' line; parse it!
            StatLine sl = new StatLine(new String(buf, 0, actuallyRead));
            // this seems to have succeeded (i.e. no exception), so get the thread group id, stored under 'process group' 
            int tgid = sl.process_group;
            int tid = sl.pid;
            File threadGroupDir = new File("/proc", Integer.toString(tgid));
            File threadDir = new File("/proc", Integer.toString(tid));
            File taskSubDir_tgid = new File(threadGroupDir, "task");
            File taskSubDir_self = new File("/proc/self/task");
            File taskSubDir_tid = new File(threadDir, "task");
            if (logger.isDebugEnabled()) {
                if (threadGroupDir.exists()) {
                    logger.debug(threadGroupDir + " exists");
                } else {
                    logger.debug(threadGroupDir + " does not exist");
                }
                if (threadDir.exists()) {
                    logger.debug(threadDir + " exists");
                } else {
                    logger.debug(threadDir + " does not exist");
                }
                if (taskSubDir_tgid.exists()) {
                    logger.debug(taskSubDir_tgid + " exists");
                } else {
                    logger.debug(taskSubDir_tgid + " does not exist");
                }
                if (taskSubDir_self.exists()) {
                    logger.debug(taskSubDir_self + " exists");
                } else {
                    logger.debug(taskSubDir_self + " does not exist");
                }
                if (taskSubDir_tid.exists()) {
                    logger.debug(taskSubDir_tid + " exists");
                } else {
                    logger.debug(taskSubDir_tid + " does not exist");
                }
            }
            // get the list of thread ids from a 'task' subdir that exists
            File taskSubDir;
            if (taskSubDir_self.exists()) {
                taskSubDir = taskSubDir_self;
            } else if (taskSubDir_tgid.exists()) {
                taskSubDir = taskSubDir_tgid;
            } else if (taskSubDir_tid.exists()) {
                taskSubDir = taskSubDir_tid;
            } else {
                throw new IllegalStateException("No path found to a 'task' subdirectory - sorry");
            }
            String[] tids = taskSubDir.list();
            // loop over all the tids and check out their 'stat' files 
            for (int i = 0; i < tids.length; i++) {
                try {
                    File threadStat = new File(taskSubDir, tids[i] + "/stat");
                    InputStreamReader isr = new InputStreamReader(new FileInputStream(threadStat)); // assume default charset
                    int actuallyRead2 = isr.read(buf, 0, buf.length);
                    isr.close();
                    // parse the stat file...
                    StatLine sl2 = new StatLine(new String(buf, 0, actuallyRead2));
                    // seems to have succeeded, collect the data
                    loc_minorFaultSum += sl2.min_flt;
                    loc_majorFaultSum += sl2.maj_flt;
                    loc_childrenMinorFaultSum += sl2.cmin_flt;
                    loc_childrenMajorFaultSum += sl2.cmaj_flt;
                    loc_utimeSum += sl2.utime;
                    loc_stimeSum += sl2.stime;
                    loc_childrenUtimeSum += sl2.cutime;
                    loc_childrenStimeSum += sl2.cstime;
                    if (i == 0) {
                        // take any stat file for vsize and rss
                        loc_vsize = sl2.vsize; // originally in bytes
                        loc_rss = sl2.rss.multiply(new BigInteger("4096")); // originally in pages == 4KByte on x86, so we multiply
                    }
                } catch (FileNotFoundException exe) {
                    // If 'threadStat' is gone, we will get an FileNotFoundException; this
                    // happens when the process shuts down and the logger is called for a last time
                    // Do nothing and continue looping
                }
            }
            // we made it w/o Exception; transfer values in one atomic operation
            return new ProcCollection(loc_minorFaultSum, loc_majorFaultSum, loc_childrenMinorFaultSum, loc_childrenMajorFaultSum, loc_utimeSum, loc_stimeSum, loc_childrenUtimeSum,
                    loc_childrenStimeSum, loc_vsize, loc_rss);
        } catch (Exception exe) {
            // something went wrong...info this
            logger.info("While accessing /proc filesystem", exe);            
            return null;
        }
    }
}
