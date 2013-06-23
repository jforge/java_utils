package com.mplify.queueing;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A 'queue' interface implemented by 'SimpleQueue' and 'PrioritizedQueue'.
 * Note that there is no method declaration for 'enqueueing' as such a
 * method is implementation-specific.
 *  
 * 2003.09.24 First release 
 ******************************************************************************/

public interface Queue<T> {
	
	/**
	 * Wait for an Object to show up in the waiting queue. Returns either 'null'
	 * after the given timeout has expired (if 0, we wait forever) or the
	 * next Object in the queue. This is called by consumers.
	 * Interrupts are caught internally and lead to an immediate return with null.
	 */	
	
	public T waitForObject(long timeout_ms);
	
	/**
	 * How many objects are currently the queue?
	 * Note that if this queried through MRTG, MRTG can compute the average queue size
	 */
	 
	public int getQueueSize();
	
	/**
	 * How many objects have passed through so far? 
	 */
	
	public int getCount();
	
	/**
	 * Recompute the average 'rate of objects coming into the queue' and return it.
	 */
	 
	public double measureAverageRateOfIncomingObjects();

	/**
	 * What is the average 'rate of objects coming into the queue'? (in Hertz)
	 */
	
	public double getAverageRateOfIncomingObjects();
	
	/**
	 * Is it empty?
	 */
	
	public boolean isEmpty();
}