package com.mplify.queueing;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.logging.DateTexter;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * This structure keeps 'runnables' (generally, the type T) in a time-tagged
 * queue. 
 * 
 * *** This is NOT A THREAD SAFE STRUCTURE ***
 * 
 * 2003.11.08 - Released
 * 2005.09.03 - Copied from m3p.msg.queue.rightsider.worker.WaitingQueues
 *              for use by DealerCtrlRunnable.  
 * 2006.03.24 - Added quickExtract()
 * 2009.05.26 - Replaced Vector by ArrayList for "queue"; a bidirectional
 *              linked list would be still better... or rather, one
 *              has to invert the queue.
 * 2009.05.27 - Full rewrite from WaitingQueue<T> to TimedQueue<T>. Using
 *              a linked list underneath
 ******************************************************************************/

public class TimedQueue<T> {

    private final static String CLASS = TimedQueue.class.getName();

    private final List<QueueInfo<T>> queue = new LinkedList<QueueInfo<T>>(); // sorted by time ascending

    private final static Logger LOGGER_pushToQueue = LoggerFactory.getLogger(CLASS + ".pushToQueue");
    private final static Logger LOGGER_testAndPullFromQueue = LoggerFactory.getLogger(CLASS + ".testAndPullFromQueue");
    private final static Logger LOGGER_listSorted = LoggerFactory.getLogger(CLASS + ".listSorted");

    /*
     * Class used to hook stuff into the list.
     */

    private static class QueueInfo<TT> {

        public final Date when;
        public final TT stuff;

        public QueueInfo(TT stuff, Date when) {
            assert when != null;
            this.stuff = stuff;
            this.when = when;
        }
    }

    /**
     * Common code: push stuff to queue. You *can* insert null! If a "null" date is passed, "now" is used as time
     */

    public void pushToQueue(T stuff, Date when) {
        Logger logger = LOGGER_pushToQueue;
        Date locWhen;
        if (when == null) {
            locWhen = new Date();
        } else {
            locWhen = when;
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Going to insert stuff into queue; 'when' is " + DateTexter.NORMAL.inDefault(locWhen) + "; queue length is now " + queue.size());
        }
        {
            ListIterator<QueueInfo<T>> iter = queue.listIterator();
            while (iter.hasNext()) {
                QueueInfo<T> qi = iter.next();
                if (!locWhen.before(qi.when)) {
                    // locWhen >= qi.when ... continue searching
                } else {
                    // locWhen < qi.when ... insert before qi
                    iter.previous();
                    iter.add(new QueueInfo<T>(stuff, locWhen));
                }
            }
            if (!iter.hasNext()) {
                // insert at far end
                queue.add(new QueueInfo<T>(stuff, locWhen));
            }
        }
        assert listSorted();
    }

    /**
     * A simple check. Comment out once you are sure it works
     */

    private boolean listSorted() {
        Logger logger = LOGGER_listSorted;
        Date currentDate = new Date(0);
        for (QueueInfo<T> qi : queue) {
            int pos = 0;
            if (qi.when.before(currentDate)) {
                logger.error("Postcondition does not hold in queue at position " + pos + " " + DateTexter.NORMAL.inDefault(qi.when) + " < " + DateTexter.NORMAL.inDefault(currentDate));
                return false;
            }
            currentDate = qi.when;
            pos++;
        }
        return true;
    }

    /**
     * Pulling from queue (remove from front) - returns (null) if the queue is empty or if the foremost entry has a
     * marker time that is strictly time-after the passed 'now'. 
     */

    public T testAndPullFromQueue(long now) {
        Logger logger = LOGGER_testAndPullFromQueue;
        if (queue.isEmpty()) {
            return null;
        } else {
            QueueInfo<T> qi = queue.get(0);
           
            if (logger.isDebugEnabled()) {
                logger.debug("Encountered stuff to be removed at " + DateTexter.NORMAL.inDefault(qi.when) + " and 'now' is " + DateTexter.NORMAL.inDefault(new Date(now)) + "; remove: " + (qi.when.getTime() < now)
                        + "; new queue length " + (queue.size()-1));
            }
            if (qi.when.getTime() <= now) {
                queue.remove(0);
                return qi.stuff;
            } else {
                return null;
            }
        }
    }

    /**
     * Get the marker time of the formemost entry in a queue, or null if there is no entry at all
     */

    public Date getForemostMarkerTime() {
        if (queue.isEmpty()) {
            return null;
        } else {
            QueueInfo<T> qi = queue.get(0);
            return qi.when;
        }
    }

    /**
     * Get a special ListIterator which has an extension whereby one can ask the next "when" value. The iterator allows
     * removal only. Calling "add" or "set" will throw UnsupportedOperationException. Note that if "nextWhen()" is
     * called, one has to issue a "previous" first if one wants to obtain the corresponding "next()"
     */

    public static class TimedQueueListIterator<TT> implements ListIterator<TT> {

        private final ListIterator<QueueInfo<TT>> actualIterator;

        private TimedQueueListIterator(ListIterator<QueueInfo<TT>> ai) {
            this.actualIterator = ai;
        }

        @Override
        public void add(TT e) {
            throw new UnsupportedOperationException("The 'add' operation is not supported");
        }

        @Override
        public boolean hasNext() {
            return actualIterator.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return actualIterator.hasPrevious();
        }

        @Override
        public TT next() {
            QueueInfo<TT> qi = actualIterator.next();
            return qi.stuff;
        }

        public Date nextWhen() {
            QueueInfo<TT> qi = actualIterator.next();
            return qi.when;
        }

        @Override
        public int nextIndex() {
            return actualIterator.nextIndex();
        }

        @Override
        public TT previous() {
            QueueInfo<TT> qi = actualIterator.previous();
            return qi.stuff;
        }

        @Override
        public int previousIndex() {
            return actualIterator.previousIndex();
        }

        @Override
        public void remove() {
            actualIterator.remove();
        }

        @Override
        public void set(TT e) {
            throw new UnsupportedOperationException("The 'set' operation is not supported");
        }

    }

    /**
     * Get the special iterator which has the "when" question
     */

    public TimedQueueListIterator<T> getTimedQueueListIterator() {
        return new TimedQueueListIterator<T>(queue.listIterator());
    }

    /**
     * Size of this queue...
     */

    public int size() {
        return queue.size();
    }

}
