package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.junit.TestStarter;
import com.mplify.timehandling.LocalTime;
import com.mplify.timehandling.SimpleTimer;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Testing the LocalTime class.
 * 
 * 2005.04.22 - Created
 * 2006.04.20 - main() replaced
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 *******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseLocalTime extends TestStarter {

	private final static String CLASS = TestCaseLocalTime.class.getName();

	/**
	 * Try many constructions with different (overflowDay, hour, min, sec) tuples
	 * and compare the results with a similarly constructed Calendar
	 */

	@Test
	public void testLenientConstruction() {
		Logger logger = LoggerFactory.getLogger(CLASS + ".testLenientConstruction");
		logger.info("Starting");
		SimpleTimer st = new SimpleTimer();
		int counter = 0;
		for (int overflowDay = -3; overflowDay < 3; overflowDay++) {
			for (int hour = -30; hour < 30; hour++) {
				// System.out.println(overflowDay + " " + hour);
				for (int min = -80; min < 80; min += 2) {
					for (int sec = -80; sec < 80; sec += 25) {
						counter++;
						Calendar cal = Calendar.getInstance();
						cal.set(Calendar.YEAR, 2000);
						cal.set(Calendar.MONTH, Calendar.JANUARY);
						cal.set(Calendar.DAY_OF_MONTH, 1);
						cal.set(Calendar.SECOND, sec);
						cal.set(Calendar.MINUTE, min);
						cal.set(Calendar.HOUR_OF_DAY, hour + 24 * overflowDay);
						LocalTime lt = new LocalTime(hour, min, sec, overflowDay);
						assertEquals("Second", cal.get(Calendar.SECOND), lt.getSecond());
						assertEquals("Minute", cal.get(Calendar.MINUTE), lt.getMinute());
						assertEquals("Hour", cal.get(Calendar.HOUR_OF_DAY), lt.getHour());
						if (lt.getOverflowDays() < 0) {
							assertEquals("OverflowDays: Month", Calendar.DECEMBER, cal.get(Calendar.MONTH));
							assertEquals("OverflowDays: Year", 1999, cal.get(Calendar.YEAR));
							assertEquals("OverflowDays: Day-of-Month", 31, cal.get(Calendar.DAY_OF_MONTH) - (1 + lt.getOverflowDays()));
						} else if (lt.getOverflowDays() == 0) {
							assertEquals("OverflowDays: Month", Calendar.JANUARY, cal.get(Calendar.MONTH));
							assertEquals("OverflowDays: Year", 2000, cal.get(Calendar.YEAR));
							assertEquals("OverflowDays: Day-of-Month", 1, cal.get(Calendar.DAY_OF_MONTH));
						} else {
							// lt.getOverflowDays()>0
							assertEquals("OverflowDays: Month", Calendar.JANUARY, cal.get(Calendar.MONTH));
							assertEquals("OverflowDays: Year", 2000, cal.get(Calendar.YEAR));
							assertEquals("OverflowDays: Day-of-Month", 1, cal.get(Calendar.DAY_OF_MONTH) - lt.getOverflowDays());
						}
					}
				}
			}
		}
		logger.info("Ending; timer = " + st.elapsedTimeInMillis() + " for " + counter + " constructions, i.e. " + (float) (st.elapsedTimeInMillis()) / (float) counter);
	}

	/**
	 * Trivial comparison test: create 3 LocalTime instances and compare them
	 */

	@Test
	public void testComparison() {
		Logger logger = LoggerFactory.getLogger(CLASS + ".testComparison");
		logger.info("Starting");
		Comparable<Object> c1 = new LocalTime(12, 10, 8, 0);
		Comparable<Object> c2 = new LocalTime(14, 20, 8, 0);
		Comparable<Object> c3 = new LocalTime(14, 20, 8, 1);
		assertTrue(c1 + "<" + c2, c1.compareTo(c2) < 0);
		assertTrue(c2 + ">" + c1, c2.compareTo(c1) > 0);
		assertTrue(c1 + "<" + c3, c1.compareTo(c3) < 0);
		assertTrue(c3 + ">" + c1, c3.compareTo(c1) > 0);
		assertTrue(c1 + "==" + c1, c1.compareTo(c1) == 0);
		assertTrue(c2 + "==" + c2, c2.compareTo(c2) == 0);
		assertTrue(c3 + "==" + c3, c3.compareTo(c3) == 0);
		logger.info("Ending");
	}

	/**
	 * Trivial equals() test: create 2 LocalTime instances and compare them 
	 */

	@Test
	public void testEquals() {
		Logger logger = LoggerFactory.getLogger(CLASS + ".testEquals");
		logger.info("Starting");
		Comparable<?> obj = new LocalTime(14, 55, 12, 2);
		assertTrue(obj.equals(new LocalTime(14, 55, 12, 2)));
		assertTrue("Canonicization is applied", obj.equals(new LocalTime(13, 115, 12, 2)));
		assertTrue(obj.equals(obj));
		assertFalse(obj.equals(null));
		assertFalse("Overflow days is included in comparison", obj.equals(new LocalTime(13, 115, 12, 1)));
		logger.info("Ending");
	}

	/**
	 * Run diffClone() and test the results
	 */

	@Test
	public void testDiff() {
		Logger logger = LoggerFactory.getLogger(CLASS + ".testDiff");
		// use arbitrary value; basically we just test the sign of diff
		logger.info("Starting");
		{
			LocalTime lta = new LocalTime(12, 5, 44, 0);
			LocalTime ltb = new LocalTime(4, 44, 10, 0);
			LocalTime ltc = lta.addClone(ltb);
			assertEquals(ltb, ltc.diffClone(lta));
			assertEquals(new LocalTime(-ltb.getHour(), -ltb.getMinute(), -ltb.getSecond(), -ltb.getOverflowDays()), lta.diffClone(ltc));
		}
		{
			LocalTime lta = new LocalTime(120, 0, -33, -12);
			LocalTime ltb = new LocalTime(-23, 10, 10, 0);
			LocalTime ltc = lta.addClone(ltb);
			assertEquals(ltb, ltc.diffClone(lta));
			assertEquals(new LocalTime(-ltb.getHour(), -ltb.getMinute(), -ltb.getSecond(), -ltb.getOverflowDays()), lta.diffClone(ltc));
		}
		logger.info("Ending");
	}

	/**
	 * Check whether the negative values work
	 */

	@Test
	public void testSignReversal() {
		LocalTime lta = new LocalTime(12, 5, 44, 0);
		LocalTime ltb = new LocalTime(-12, -5, -44, 0);
		assertEquals(new LocalTime(), lta.addClone(ltb));
		assertEquals(new LocalTime(), lta.diffClone(lta));
	}

	/**
	 * Test whether 'circular time' gives expected results
	 */

	@Test
	public void testCircularTime() {
		LocalTime lta = new LocalTime(12, 0, 44, 3);
		LocalTime ltb = new LocalTime(12, 0, 44, 0);
		LocalTime ltc = new LocalTime(0, 0, lta.getCircularTime());
		// scratches the overflow days
		assertEquals(ltc, ltb);
	}

	/**
	 * Test whether 'equivalent time' gives expected results
	 */
	@Test
	public void testEquivalentTime() {
		LocalTime lta = new LocalTime(12, 0, 44, 3);
		LocalTime ltc = new LocalTime(0, 0, lta.getEquivalentTime());
		// keep the overflow days
		assertEquals(ltc, lta);
	}

	@Test
	public void testNegativeClone() {
		LocalTime lta = new LocalTime(12, 0, 44, 3);
		LocalTime ltb = new LocalTime(10, 0, 44, 0);
		LocalTime ltan = lta.negativeClone();
		LocalTime ltbn = ltb.negativeClone();
		assertTrue("1) " + ltan + " + " + lta + " == 0", ltan.addClone(lta).isZero());
		assertTrue("2) " + ltbn + " + " + ltb + " == 0", ltbn.addClone(ltb).isZero());
		assertTrue("3) " + lta + " + " + ltan + " == 0", lta.addClone(ltan).isZero());
		assertTrue("4) " + ltb + " + " + ltbn + " == 0", ltb.addClone(ltbn).isZero());
		ltan.addInPlace(lta);
		ltbn.addInPlace(ltb);
		assertTrue("5) " + ltan + " == 0", ltan.isZero());
		assertTrue("6) " + ltbn + " == 0", ltbn.isZero());
	}

	@Test
	public void testNegativeInPlace() {
		LocalTime lta = new LocalTime(12, 0, 44, 3);
		LocalTime ltb = new LocalTime(lta);
		assertEquals(lta, ltb);
		lta.negativeInPlace();
		assertTrue(lta.addClone(ltb).isZero());
	}

	@Test
	public void testEmptyConstructor() {
		assertTrue((new LocalTime()).isZero());
	}
}
