package com.mplify.linuxaria.jvminfo;

import java.math.BigInteger;

import com.mplify.logging.Story;
import com.mplify.logging.storyhelpers.Doublet;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2007, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Class holding stuff collected from '/proc' in one place. Exists to make
 * retrieval atomic.
 * 
 * 2004.06.21 - Created as private nested class of ProcAccessHelper
 * 2009.11.06 - Class moved out to toplevel for better legibility
 * 2010.02.18 - Reintegrated into new "JMX" project; data shall be access by
 *              MBean instead of through printout.
 *              Removed "when collected"
 * 2010.09.24 - Made public because also used in TX_Keepalive. Fields
 *              also changed from protected to public
 ******************************************************************************/

public class ProcCollection {

    public final long minorFaultSum; // minor faults for the process since its inception (== sum over threads)
    public final long majorFaultSum; // major faults for the process since its inception (== sum over threads)
    public final long childrenMinorFaultSum; // the same for the children (== sum over threads)
    public final long childrenMajorFaultSum; // the same for the children (== sum over threads)
    public final long utimeSum; // number of jiffies (1/100th sec) spent in user mode since process inception (== sum over threads)
    public final long stimeSum; // number of jiffies (1/100th sec) spent in kernel mode since process inception (== sum over threads)
    public final long childrenUtimeSum; // the same for the children (== sum over threads)
    public final long childrenStimeSum; // the same for the children (== sum over threads)
    public final BigInteger vsize; // virtual memory size (obtained from thread group leader)
    public final BigInteger rss; // resident set size (obtained from thread group leader)

    public ProcCollection(long minorFaultSum, long majorFaultSum, long childrenMinorFaultSum, long childrenMajorFaultSum, long utimeSum, long stimeSum, long childrenUtimeSum, long childrenStimeSum,
            BigInteger vsize, BigInteger rss) {
        this.minorFaultSum = minorFaultSum;
        this.majorFaultSum = majorFaultSum;
        this.childrenMinorFaultSum = childrenMinorFaultSum;
        this.childrenMajorFaultSum = childrenMajorFaultSum;
        this.utimeSum = utimeSum;
        this.stimeSum = stimeSum;
        this.childrenUtimeSum = childrenUtimeSum;
        this.childrenStimeSum = childrenStimeSum;
        this.vsize = vsize;
        this.rss = rss;
    }

    @SuppressWarnings("boxing")
    public Story toStory() {
        Story res = new Story();
        res.add(new Doublet("Minor faults so far", minorFaultSum));
        res.add(new Doublet("Major faults so far", majorFaultSum));
        res.add(new Doublet("Minor faults so far, children", childrenMinorFaultSum));
        res.add(new Doublet("Major faults so far, children", childrenMajorFaultSum));
        res.add(new Doublet("User mode burn, so fa", utimeSum + " jiffies (1/100s)"));
        res.add(new Doublet("Kernel mode burn, so far", stimeSum + " jiffies (1/100s)"));
        // TODO: express in seconds and MiBs
        res.add(new Doublet("User mode burn so far, children", childrenUtimeSum + " jiffies (1/100s)"));
        res.add(new Doublet("Kernel mode burn so far, children", childrenStimeSum + " jiffies (1/100s)"));
        res.add(new Doublet("Virtual memory size", reasonablePrintout(vsize)));
        res.add(new Doublet("Resident set size", reasonablePrintout(rss)));
        return res;
    }

    private final BigInteger HUNDRED = new BigInteger("100");
    private final BigInteger KiBYTE = new BigInteger("1024");
    private final BigInteger MiBYTE = new BigInteger("1048576");
    private final BigInteger GiBYTE = new BigInteger("1073741824");

    private String reasonablePrintout(BigInteger byteSize) {
        assert byteSize != null;
        if (byteSize.compareTo(GiBYTE) > 0) {
            double x = (byteSize.multiply(HUNDRED).divide(GiBYTE).doubleValue()) / 100;
            return x + " GiB";

        }
        if (byteSize.compareTo(MiBYTE) > 0) {
            double x = (byteSize.multiply(HUNDRED).divide(MiBYTE).doubleValue()) / 100;
            return x + " MiB";
        }
        if (byteSize.compareTo(KiBYTE) > 0) {
            double x = (byteSize.multiply(HUNDRED).divide(KiBYTE).doubleValue()) / 100;
            return x + " KiB";
        } else {
            return byteSize.toString() + " byte";
        }
    }
}