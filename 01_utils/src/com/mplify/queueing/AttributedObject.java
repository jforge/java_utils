package com.mplify.queueing;

import com.mplify.enums.Priority;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Class storing attributes for a queued object.
 * Formerly, this was an inner class of PrioritizedQueueWithTiming.
 * 
 * 2006.05.12 Moved to toplevel 
 ******************************************************************************/

public class AttributedObject<Tx> {

	private final Tx object; // the object that is queued
	private final long entryTime; // when it entered the queue, used in sorting the objects by retrieval priority
	private final long sortieTime; // when it should sortie the queue at the earliest, <= 0 if unset
	private final int myIndex; // objects are indexed by their arrival, this is it. >= 0
	private final Priority priority; // the priority assigned to this object

	public AttributedObject(Tx object, long sortieTime, int myIndex, Priority priority) {
		this.object = object;
		this.entryTime = System.currentTimeMillis();
		this.sortieTime = sortieTime;
		this.myIndex = myIndex;
		this.priority = priority;
	}

	public Tx getObject() {
		return object;
	}

	public long getEntryTime() {
		return entryTime;
	}

	public long getSortieTime() {
		return sortieTime;
	}

	public int getMyIndex() {
		return myIndex;
	}

	public Priority getPriority() {
		return priority;
	}
}