package com.mplify.queueing;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * This class implements a set of Queues (the derived class determines whether
 * this shall be a set of 'PrioritizedQueue' or 'SimpleQueue') The set is
 * filled in with Queues by consumer processes. Objects are then inserted into
 * these Queues by Producer processes. Note that you can only add Queues to a
 * QueueSet, not remove them. This is a structure used in inter-process 
 * communication, similar to InfoBus or Java-Messaging-System 'mailboxes'.
 *  
 * Here's the Scenario on how to use the QueueSet
 * 
 * Initialization
 * ==============
 * - Create the global properties
 * - Create the QueueSet instance and store it in the global properties
 * - Initialize the consumers(name,id)
 * - Initialize the producers
 * - Run everything
 * 
 * Consumer
 * ========
 * Initialization: - Create a consumer-specific queue using passed (name,id)
 *                 - Get the global properties
 *                 - Get the QueueSet instance from the properties
 *                 - Insert the consumer-specific queue into the QueueSet using (name,id)
 * Running:        - Just wait on the consumer-specific queue for an object to show up                    
 * 
 * Producer
 * ========
 * Initialization: - Get the global properties
 *                 - Get the QueueSet instance from the properties
 * Running:        - Produce an Object
 *                 - Find its Consumer, which must be identified by 'name'
 *                 - Enqueue the Object into the QueueSet under 'name'
 * 
 * 2003.09.24 First release 
 ******************************************************************************/

public abstract class QueueSet {

//    private final static String CLASS = QueueSet.class.getName();

    /**
     * The keepers of the queues
     */

    private final Hashtable<HashKey, Queue<?>> queues; // Queues hashed by their (name,id) tuple
    private final Hashtable<String, AttributedVector> queueVector; // Vectors of Queues (actually, AttributedVector) hashed by their (name)

    /**
     * The policy used when inserting an object into a queue taken from the ordered set
     * of queues that have the same name. There are two policies: 
     * 'random': take any queue of the set at random
     * 'round-robin': take the next queue of the set, looping back to the first if there are no more
     * Instead of using integers, we use instances 
     */

    public final static class InsertionPolicy {

        private final String name;

        public InsertionPolicy(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public final static InsertionPolicy POLICY_RANDOM = new InsertionPolicy("RANDOM");
    public final static InsertionPolicy POLICY_ROUNDROBIN = new InsertionPolicy("ROUND_ROBIN");

    /**
     * Key used in the 'queues' hashtable. The class is 'protected' as it is used
     * in the subclasses
     */

    protected final static class HashKey {

        private final Integer id;
        private final String name;

        public HashKey(String name, int id) {
            this.name = name;
            this.id = new Integer(id);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (!(obj instanceof HashKey)) {
                return false;
            }
            HashKey other = (HashKey) (obj);
            return (other.name.equals(this.name) && other.id.equals(this.id));
        }

        @Override
        public int hashCode() {
            return (id.hashCode() ^ name.hashCode());
        }

        public int getId() {
            return id.intValue();
        }

        public String getString() {
            return name;
        }

        @Override
        public String toString() {
            return "[" + name + "," + id + "]";
        }
    }

    /**
     * Value used in in the hash-of-vectors
     * The 'round-robin-index' and 'random' attribute is assigned to each Vector.
     * The 'get' methods are synchronized so that if several threads
     * insert at the same time, things are still well-defined.
     */

    @SuppressWarnings("serial")
    private static class AttributedVector extends Vector<Queue<?>> {

        private int roundRobinIndex;
        private final Random random;

        public AttributedVector() {
            roundRobinIndex = 0;
            random = new Random();
        }

        public synchronized Queue<?> getRoundRobin() {
            roundRobinIndex = (roundRobinIndex + 1) % (this.size());
            return this.get(roundRobinIndex);
        }

        public synchronized Queue<?> getRandom() {
            return this.get(random.nextInt(this.size()));
        }
    }

    /**
     * Constructor
     */

    public QueueSet() {
        queues = new Hashtable<HashKey, Queue<?>>();
        queueVector = new Hashtable<String, AttributedVector>();
    }

    /**
     * Add a new queue with the given name and id. Will throw if there already 
     * is a queue with the given name and id. The 'Queue' is passed, it is an
     * interface - any number of implementations are possible underneath.
     */

    protected synchronized void insertQueue(Queue<?> queue, String name, int id) {
        if (name == null) {
            throw new IllegalArgumentException("The passed queue name is (null)");
        }
        if (queue == null) {
            throw new IllegalArgumentException("The passed name is (null)");
        }
        // set it up in the 'hash of queues'
        HashKey hk = new HashKey(name, id);
        if (queues.containsKey(hk)) {
            throw new IllegalArgumentException("There is already a queue named " + hk);
        }
        queues.put(hk, queue);
        // also set it up in the 'hash of vectors'
        if (!queueVector.containsKey(name)) {
            queueVector.put(name, new AttributedVector());
        }
        AttributedVector vec = queueVector.get(name);
        vec.add(queue);
    }

    /**
     * Add a new queue with the given name. The id will be interpreted to be '0'
     * Will throw if there already is a queue with the given name (and id 0). The 'Queue'
     * is passed, it is an interface - any number of implementations are possible underneath
     */

    protected synchronized void insertQueue(Queue<?> queue, String name) {
        insertQueue(queue, name, 0);
    }

    /**
     * Get a Queue from the set of Queues with name 'name' using the given InsertionPolicy.
     * Throws if there is no such queue or InsertionPolicy
     */

    protected Queue<? extends Object> getQueue(String name, InsertionPolicy pol) {
        if (name == null) {
            throw new IllegalArgumentException("The passed queue name is (null)");
        }
        AttributedVector vec = queueVector.get(name);
        if (vec == null) {
            throw new IllegalArgumentException("There is no queue with name '" + name + "'");
        }
        if (pol == POLICY_RANDOM) {
            return vec.getRandom();
        } else if (pol == POLICY_ROUNDROBIN) {
            return vec.getRoundRobin();
        } else {
            throw new IllegalArgumentException("There is no policy '" + pol + "'");
        }
    }

    /**
     * Get the queue with the given String as 'name'
     */

    public Queue<?> getQueue(String name) {
        if (name == null) {
            throw new IllegalArgumentException("The passed queue name is (null)");
        }
        AttributedVector vec = queueVector.get(name);
        if (vec == null) {
            throw new IllegalArgumentException("There is no queue with name '" + name + "'");
        }
        if (vec.size() != 1) {
            throw new IllegalArgumentException("There are " + vec.size() + " queues with name '" + name + "'");
        }
        return vec.firstElement();
    }

    /**
     * Get the queue with the given String as 'name' and the given integer as 'id'
     */

    public Queue<?> getQueue(String name, int id) {
        if (name == null) {
            throw new IllegalArgumentException("The passed queue name is (null)");
        }
        // set it up in the 'hash of queues'
        HashKey hk = new HashKey(name, id);
        if (!queues.containsKey(hk)) {
            throw new IllegalArgumentException("There no queue named " + hk);
        }
        return queues.get(hk);
    }

    /**
     * How many objects are currently in the queues?
     * Note that if this queried through MRTG, MRTG can compute the average queue size
     */

    public int getTotalQueueSize() {
        int total = 0;
        Enumeration<Queue<?>> iter = queues.elements();
        while (iter.hasMoreElements()) {
            Queue<?> queue = iter.nextElement();
            total += queue.getQueueSize();
        }
        return total;
    }

    /**
     * How many objects have passed through so far? 
     */

    public int getCount() {
        int total = 0;
        Enumeration<Queue<?>> iter = queues.elements();
        while (iter.hasMoreElements()) {
            Queue<?> queue = iter.nextElement();
            total += queue.getCount();
        }
        return total;
    }

    /**
     * What is the rate of objects coming into the queue? (in Hertz)
     * It is the total over all the queues
     */

    public double getAverageRateOfIncomingObjects() {
        double total = 0;
        Enumeration<Queue<?>> iter = queues.elements();
        while (iter.hasMoreElements()) {
            Queue<?> queue = iter.nextElement();
            total += queue.getAverageRateOfIncomingObjects();
        }
        return total;
    }

    /**
     * Get the number of queues
     */

    public int getNumberOfQueues() {
        return queues.size();
    }

}
