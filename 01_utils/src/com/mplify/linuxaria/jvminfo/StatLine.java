package com.mplify.linuxaria.jvminfo;

import java.math.BigInteger;
import java.util.StringTokenizer;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2007, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A class representing the contents of /proc/TID/stat. We are using the 
 * description given in O'Reilly: "/proc et /sys - accès direct au noyau Linux"
 * by Olivier Daudel. The value's sizes give rise to some caveats; see below. We
 * assume: 
 * 
 * %d == 32-bit signed ==> int 
 * %lu == 32-bit bitset or we use a 64-bit signed integer instead to accomodate 32-bit unsigned ==> long 
 * %ld == 32-bit signed value ==> int 
 * %%llu == 64-bit unsigned; we use 64-bit signed ==> long Addresses are BigInteger to accomodate 64-bit unsigned values
 * 
 * Package visibility is sufficient here!
 * 
 * 2004.06.21 - Created as private nested class of ProcAccessHelper
 * 2009.11.06 - Class moved out to toplevel for better legibility
 * 2010.02.18 - Reintegrated into new "JMX" project; data shall be access by
 *              MBean instead of through printout.
 ******************************************************************************/

class StatLine {

//    private final static String CLASS = StatLine.class.getName();
//    private final static Logger LOGGER_init = LoggerFactory.getLogger(CLASS + ".<init>");    

    public final int pid; // %d, thread id (by tradition, called pid) 
    public final String comm; // name of executable, "zomb" if zombi
    public final char state; // state of thread: 0->R, 1->S, 2->D, 4->Z, 8->T, 16->X
    public final int ppid; // %d,  parent thread id, we assume 32-bit signed
    public final int process_group; // %d,  thread id of thread group leader == thread group id
    public final int session; // %d,  session id == SID
    public final int tty_nr; // %d,  major and minor numbers of controlling tty
    public final int tty_pgrp; // %d,  controlling terminal thread group id
    public final long flags; // %lu, flags of thread
    public final long min_flt; // %lu, number of minor page faults since thread inception
    public final long cmin_flt; // %lu, number of minor page faults of thread's children since thread inception
    public final long maj_flt; // %lu, number of major page faults since thread inception
    public final long cmaj_flt; // %lu, number of major page faults of thread's children since thread inception
    public final long utime; // %lu, number of jiffies spent in user mode since inception
    public final long stime; // %lu, number of jiffies spent in kernel mode since inception
    public final long cutime; // %lu, number of jiffies spent by thread's children in user mode since inception
    public final long cstime; // %lu, number of jiffies spent by thread's children in kernel mode since inception
    public final int priority; // %ld, effective dynamic priority
    public final int nice; // %ld, nice value
    public final int num_threads; // %ld, number of threads in thread group
    public final int it_real_value; // %ld, number of jiffies to the next SIGALRM
    public final long start_time; // %ll, utime of thread start counted from system boot in jiffies
    public final BigInteger vsize; // %lu, virtual memory size in byte
    public final BigInteger rss; // %ld, resident set size in blocks (4KB on x86)
    public final BigInteger rlim_rss_cur; // %lu, current virtual memory limit ulimit -S -v
    public final BigInteger start_code; // %lu, address of code start
    public final BigInteger end_code; // %lu, address of code end
    public final BigInteger start_stack; // %lu, address of start of stack
    public final BigInteger esp; // %lu, stack pointer
    public final BigInteger eip; // %lu, instruction pointer
    public final long pending; // %lu, pending signals
    public final long blocked; // %lu, blocked signals
    public final long sigign; // %lu, ignores signals
    public final long sigcatch; // %lu, caught signals
    public final BigInteger wchan; // %lu, address of wait function
    public final long nswap; // %lu, number of pages swapped out - always 0
    public final long cnswap; // %lu, number of pages swapped out for child threads - always 0
    public final int exit_signal; // %d,  signal to send to parent on exit
    public final int processor_task_cpu; // %d,  cpu lastly used: 0, 1, 2, etc..
    public final int rt_priority; // %lu, real-time priority or 0 if NORMAL
    public final int policy; // %lu, scheduling policy: 0->NORMAL, 1->FIFO, 2->RR

    public StatLine(String line) {
//        Logger logger = LOGGER_init;
        StringTokenizer st = new StringTokenizer(line);
        // log after each parse so that we know when something goes wrong
        pid = Integer.parseInt(st.nextToken());
        // logger.info("pid: " + pid);
        // TODO: this will fail if 'comm' contains spaces - need a regexp here? The name is enclosed in ()
        comm = st.nextToken();
        // logger.info("comm: " + comm);
        state = st.nextToken().charAt(0);
        // logger.info("state: " + state);
        ppid = Integer.parseInt(st.nextToken());
        // logger.info("ppid: " + ppid);
        process_group = Integer.parseInt(st.nextToken());
        // logger.info("process_group: " + process_group);
        session = Integer.parseInt(st.nextToken());
        // logger.info("session: " + session);
        tty_nr = Integer.parseInt(st.nextToken());
        // logger.info("tty_nr: " + tty_nr);
        tty_pgrp = Integer.parseInt(st.nextToken());
        // logger.info("tty_pgrp: " + tty_pgrp);
        flags = Long.parseLong(st.nextToken());
        // logger.info("flags: " + flags);
        min_flt = Long.parseLong(st.nextToken());
        // logger.info("min_flt: " + min_flt);
        cmin_flt = Long.parseLong(st.nextToken());
        // logger.info("cmin_flt: " + cmin_flt);
        maj_flt = Long.parseLong(st.nextToken());
        // logger.info("maj_flt: " + maj_flt);
        cmaj_flt = Long.parseLong(st.nextToken());
        // logger.info("cmaj_flt: " + cmaj_flt);
        utime = Long.parseLong(st.nextToken());
        // logger.info("utime: " + utime);
        stime = Long.parseLong(st.nextToken());
        // logger.info("stime: " + stime);
        cutime = Long.parseLong(st.nextToken());
        // logger.info("cutime: " + cutime);
        cstime = Long.parseLong(st.nextToken());
        // logger.info("cstime: " + cstime);
        priority = Integer.parseInt(st.nextToken());
        // logger.info("priority: " + priority);
        nice = Integer.parseInt(st.nextToken());
        // logger.info("nice: " + nice);
        num_threads = Integer.parseInt(st.nextToken());
        // logger.info("num_threads: " + num_threads);
        it_real_value = Integer.parseInt(st.nextToken());
        // logger.info("it_real_value: " + it_real_value);
        start_time = Long.parseLong(st.nextToken());
        // logger.info("start_time: " + start_time);
        vsize = new BigInteger(st.nextToken());
        // logger.info("vsize: " + vsize);
        rss = new BigInteger(st.nextToken());
        // logger.info("rss: " + rss);
        rlim_rss_cur = new BigInteger(st.nextToken());
        // logger.info("rlim_rss_cur: " + rlim_rss_cur);
        start_code = new BigInteger(st.nextToken());
        // logger.info("start_code: " + start_code);
        end_code = new BigInteger(st.nextToken());
        // logger.info("end_code: " + end_code);
        start_stack = new BigInteger(st.nextToken());
        // logger.info("start_stack: " + start_stack);
        esp = new BigInteger(st.nextToken());
        // logger.info("esp: " + esp);
        eip = new BigInteger(st.nextToken());
        // logger.info("eip: " + eip);
        pending = Long.parseLong(st.nextToken());
        // logger.info("pending: " + pending);
        blocked = Long.parseLong(st.nextToken());
        // logger.info("blocked: " + blocked);
        sigign = Long.parseLong(st.nextToken());
        // logger.info("sigign: " + sigign);
        sigcatch = Long.parseLong(st.nextToken());
        // logger.info("sigcatch: " + sigcatch);
        wchan = new BigInteger(st.nextToken());
        // logger.info("wchan: " + wchan);
        nswap = Long.parseLong(st.nextToken());
        // logger.info("nswap: " + nswap);
        cnswap = Long.parseLong(st.nextToken());
        // logger.info("cnswap: " + cnswap);
        exit_signal = Integer.parseInt(st.nextToken());
        // logger.info("exit_signal       : " + exit_signal);
        processor_task_cpu = Integer.parseInt(st.nextToken());
        // logger.info("processor_task_cpu: " + processor_task_cpu);
        rt_priority = Integer.parseInt(st.nextToken());
        // logger.info("rt_priority       : " + rt_priority);
        policy = Integer.parseInt(st.nextToken());
        // logger.info("policy: " + policy);
    }
}
