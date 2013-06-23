package com.mplify.mutable;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * An integer which can be modified in called methods (thus it implements a 
 * pass-and-return value)
 * 
 * It is also fully synchronized, thus can be used as base for a counter.
 * 
 * 2009.03.27 - Added the getThenInc() method
 ******************************************************************************/

public class MutableInteger {

	private int value;

	public MutableInteger(int init) {
		value = init;
	}

	public synchronized void inc() {
	    value += 1;
	}

	public synchronized void dec() {
	    value -= 1;
	}

	public synchronized void inc(int x) {
		value += x;
	}

	public synchronized void dec(int x) {
		value -= x;
	}

	public synchronized void set(int x) {
		value = x;
	}

	public synchronized int get() {
		return value;
	}

	public synchronized int getThenInc() {
	    int res = value;
	    value++;
	    return res;
	}
	
	@Override
    public synchronized String toString() {
		return Integer.toString(value);		
	}
}