package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.junit.TestStarter;
import com.mplify.timehandling.CalendarMonth;
import com.mplify.timehandling.LocalDate;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Testing the LocalDate class.
 * 
 * 2005.04.22 - Created
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 *******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseLocalDate extends TestStarter {

    private final static String CLASS = TestCaseLocalDate.class.getName();

    @Test
    public void testLenientConstruction() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testLenientConstruction");
        {
            LocalDate ld = new LocalDate(2004, CalendarMonth.JANUARY, 1);
            assertEquals(1, ld.getDayOfMonth());
            assertEquals(CalendarMonth.JANUARY, ld.getMonth());
            assertEquals(2004, ld.getYear());
        }
        {
            LocalDate ld = new LocalDate(2004, CalendarMonth.JANUARY, 32);
            assertEquals(1, ld.getDayOfMonth());
            assertEquals(CalendarMonth.FEBRUARY, ld.getMonth());
            assertEquals(2004, ld.getYear());
        }
        {
            LocalDate ld = new LocalDate(2004, CalendarMonth.DECEMBER, 34);
            assertEquals(3, ld.getDayOfMonth());
            assertEquals(CalendarMonth.JANUARY, ld.getMonth());
            assertEquals(2005, ld.getYear());
        }
        {
            LocalDate ld = new LocalDate(2004, CalendarMonth.FEBRUARY, -1);
            assertEquals(30, ld.getDayOfMonth());
            assertEquals(CalendarMonth.JANUARY, ld.getMonth());
            assertEquals(2004, ld.getYear());
        }
        {
            // Amazing! February 0-th is January 31st. I like it!
            LocalDate ld = new LocalDate(2004, CalendarMonth.FEBRUARY, 0);
            assertEquals(31, ld.getDayOfMonth());
            assertEquals(CalendarMonth.JANUARY, ld.getMonth());
            assertEquals(2004, ld.getYear());
        }
        logger.info("Ok");
    }

    @Test
    public void testComparison() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testComparison");
        Comparable<Object> c1 = new LocalDate(2005, CalendarMonth.JANUARY, 12);
        Comparable<Object> c2 = new LocalDate(2005, CalendarMonth.JANUARY, 13);
        assertTrue(c1 + "<" + c2, c1.compareTo(c2) < 0);
        assertTrue(c2 + ">" + c1, c2.compareTo(c1) > 0);
        assertTrue(c1 + "==" + c1, c1.compareTo(c1) == 0);
        assertTrue(c2 + "==" + c2, c2.compareTo(c2) == 0);
        logger.info("Ok");
    }

    @Test
    public void testEquals() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testEquals");
        Comparable<?> obj = new LocalDate(2005, CalendarMonth.JANUARY, 31);
        assertTrue(obj.equals(new LocalDate(2005, CalendarMonth.JANUARY, 31)));
        assertTrue(obj.equals(new LocalDate(2005, CalendarMonth.FEBRUARY, 0)));
        assertFalse(obj.equals(null));
        assertFalse(obj.equals("January"));
        logger.info("Ok");
    }

    @Test
    public void testAddition() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testAddition");
        {
            LocalDate ld = new LocalDate(2004, CalendarMonth.DECEMBER, 1);
            LocalDate ldnew = ld.addDaysClone(12);
            ld.addDaysInplace(12);
            assertTrue("Values are equal", ld.equals(ldnew));
            assertFalse("Instances are different", ld == ldnew);
            assertEquals(new LocalDate(2004, CalendarMonth.DECEMBER, 13), ld);
        }
        {
            LocalDate ld = new LocalDate(2004, CalendarMonth.DECEMBER, 1);
            LocalDate ldnew = ld.addMonthsClone(44);
            ld.addMonthsInplace(44);
            assertTrue("Values are equal", ld.equals(ldnew));
            assertFalse("Instances are different", ld == ldnew);
            assertEquals(new LocalDate(2008, CalendarMonth.AUGUST, 1), ld);
        }
        {
            LocalDate ld = new LocalDate(2004, CalendarMonth.DECEMBER, 1);
            LocalDate ldnew = ld.addYearsClone(12);
            ld.addYearsInplace(12);
            assertTrue("Values are equal", ld.equals(ldnew));
            assertFalse("Instances are different", ld == ldnew);
            assertEquals(new LocalDate(2016, CalendarMonth.DECEMBER, 1), ld);
        }
        {
            LocalDate ld = new LocalDate(2004, CalendarMonth.DECEMBER, 1);
            LocalDate ldnew = ld.addWeeksClone(5);
            ld.addWeeksInplace(5);
            assertTrue("Values are equal", ld.equals(ldnew));
            assertFalse("Instances are different", ld == ldnew);
            assertEquals(new LocalDate(2005, CalendarMonth.JANUARY, 5), ld);
        }
        logger.info("Ok");
    }

}
