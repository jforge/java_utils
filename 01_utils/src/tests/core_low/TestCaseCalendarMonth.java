package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.junit.TestStarter;
import com.mplify.timehandling.CalendarMonth;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Testing the CalendarMonth class.
 * 
 * 2005.04.22 - Created
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 *******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseCalendarMonth extends TestStarter {

    private final static String CLASS = TestCaseCalendarMonth.class.getName();

    @Test
    public void testGettingJavaValueFromCalendarMonthInstance() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testGettingJavaValueFromCalendarMonthInstance");
        assertEquals(Calendar.JANUARY, CalendarMonth.JANUARY.getJavaValue());
        assertEquals(Calendar.FEBRUARY, CalendarMonth.FEBRUARY.getJavaValue());
        assertEquals(Calendar.MARCH, CalendarMonth.MARCH.getJavaValue());
        assertEquals(Calendar.APRIL, CalendarMonth.APRIL.getJavaValue());
        assertEquals(Calendar.MAY, CalendarMonth.MAY.getJavaValue());
        assertEquals(Calendar.JUNE, CalendarMonth.JUNE.getJavaValue());
        assertEquals(Calendar.JULY, CalendarMonth.JULY.getJavaValue());
        assertEquals(Calendar.AUGUST, CalendarMonth.AUGUST.getJavaValue());
        assertEquals(Calendar.SEPTEMBER, CalendarMonth.SEPTEMBER.getJavaValue());
        assertEquals(Calendar.OCTOBER, CalendarMonth.OCTOBER.getJavaValue());
        assertEquals(Calendar.NOVEMBER, CalendarMonth.NOVEMBER.getJavaValue());
        assertEquals(Calendar.DECEMBER, CalendarMonth.DECEMBER.getJavaValue());
        logger.info("Ok");
    }

    @Test
    public void testGettingCalendarMonthInstanceFromJavaValue() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testGettingCalendarMonthInstanceFromJavaValue");
        assertEquals(CalendarMonth.JANUARY, CalendarMonth.obtainFromJava(Calendar.JANUARY));
        assertEquals(CalendarMonth.FEBRUARY, CalendarMonth.obtainFromJava(Calendar.FEBRUARY));
        assertEquals(CalendarMonth.MARCH, CalendarMonth.obtainFromJava(Calendar.MARCH));
        assertEquals(CalendarMonth.APRIL, CalendarMonth.obtainFromJava(Calendar.APRIL));
        assertEquals(CalendarMonth.MAY, CalendarMonth.obtainFromJava(Calendar.MAY));
        assertEquals(CalendarMonth.JUNE, CalendarMonth.obtainFromJava(Calendar.JUNE));
        assertEquals(CalendarMonth.JULY, CalendarMonth.obtainFromJava(Calendar.JULY));
        assertEquals(CalendarMonth.AUGUST, CalendarMonth.obtainFromJava(Calendar.AUGUST));
        assertEquals(CalendarMonth.SEPTEMBER, CalendarMonth.obtainFromJava(Calendar.SEPTEMBER));
        assertEquals(CalendarMonth.OCTOBER, CalendarMonth.obtainFromJava(Calendar.OCTOBER));
        assertEquals(CalendarMonth.NOVEMBER, CalendarMonth.obtainFromJava(Calendar.NOVEMBER));
        assertEquals(CalendarMonth.DECEMBER, CalendarMonth.obtainFromJava(Calendar.DECEMBER));
        logger.info("Ok");
    }

    @Test
    public void testGettingCalendarMonthInstanceFromItsValue() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testGettingCalendarMonthInstanceFromItsValue");
        assertEquals(CalendarMonth.JANUARY, CalendarMonth.obtain(1));
        assertEquals(CalendarMonth.FEBRUARY, CalendarMonth.obtain(2));
        assertEquals(CalendarMonth.MARCH, CalendarMonth.obtain(3));
        assertEquals(CalendarMonth.APRIL, CalendarMonth.obtain(4));
        assertEquals(CalendarMonth.MAY, CalendarMonth.obtain(5));
        assertEquals(CalendarMonth.JUNE, CalendarMonth.obtain(6));
        assertEquals(CalendarMonth.JULY, CalendarMonth.obtain(7));
        assertEquals(CalendarMonth.AUGUST, CalendarMonth.obtain(8));
        assertEquals(CalendarMonth.SEPTEMBER, CalendarMonth.obtain(9));
        assertEquals(CalendarMonth.OCTOBER, CalendarMonth.obtain(10));
        assertEquals(CalendarMonth.NOVEMBER, CalendarMonth.obtain(11));
        assertEquals(CalendarMonth.DECEMBER, CalendarMonth.obtain(12));
        logger.info("Ok");
    }

    @Test
    public void testEquals() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testEquals");
        Object obj = CalendarMonth.JANUARY;
        assertTrue(obj.equals(CalendarMonth.JANUARY));
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("January"));
        logger.info("Ok");
    }

    @Test
    public void testComparison() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testComparison");
        Comparable<Object> c1 = CalendarMonth.JANUARY;
        Comparable<Object> c2 = CalendarMonth.FEBRUARY;
        assertTrue("January is less than February", c1.compareTo(c2) < 0);
        assertTrue("February is more than January", c2.compareTo(c1) > 0);
        assertTrue("January is equal to January", c1.compareTo(c1) == 0);
        assertTrue("February is equal to February", c2.compareTo(c2) == 0);
        logger.info("Ok");
    }
}
