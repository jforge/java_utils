package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.junit.TestStarter;
import com.mplify.logging.DateTexter;
import com.mplify.timehandling.CalendarMonth;
import com.mplify.timehandling.LocalDateAndTime;

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
 * 2005.09.08 - Added tests
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 *******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseLocalDateAndTime extends TestStarter {

    private final static String CLASS = TestCaseLocalDateAndTime.class.getName();

    @Test
    public void testCompareTo() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testCompareTo");
        // compare different years
        {
            LocalDateAndTime a = new LocalDateAndTime(2005, CalendarMonth.APRIL, 25);
            LocalDateAndTime b = new LocalDateAndTime(2006, CalendarMonth.APRIL, 25);
            assertTrue(a.compareTo(b) < 0);
            assertTrue(b.compareTo(a) > 0);
            assertTrue(a.compareTo(a) == 0);
            assertTrue(b.compareTo(b) == 0);
        }
        // compare different months
        {
            LocalDateAndTime a = new LocalDateAndTime(2005, CalendarMonth.APRIL, 25);
            LocalDateAndTime b = new LocalDateAndTime(2005, CalendarMonth.MAY, 25);
            assertTrue(a.compareTo(b) < 0);
            assertTrue(b.compareTo(a) > 0);
            assertTrue(a.compareTo(a) == 0);
            assertTrue(b.compareTo(b) == 0);
        }
        // compare different days
        {
            LocalDateAndTime a = new LocalDateAndTime(2005, CalendarMonth.APRIL, 25);
            LocalDateAndTime b = new LocalDateAndTime(2005, CalendarMonth.APRIL, 26);
            assertTrue(a.compareTo(b) < 0);
            assertTrue(b.compareTo(a) > 0);
            assertTrue(a.compareTo(a) == 0);
            assertTrue(b.compareTo(b) == 0);
        }
        // compare different hours
        {
            LocalDateAndTime a = new LocalDateAndTime(2005, CalendarMonth.APRIL, 25, 13);
            LocalDateAndTime b = new LocalDateAndTime(2005, CalendarMonth.APRIL, 25, 14);
            assertTrue(a.compareTo(b) < 0);
            assertTrue(b.compareTo(a) > 0);
            assertTrue(a.compareTo(a) == 0);
            assertTrue(b.compareTo(b) == 0);
        }
        // compare different minutes
        {
            LocalDateAndTime a = new LocalDateAndTime(2005, CalendarMonth.APRIL, 25, 13, 43);
            LocalDateAndTime b = new LocalDateAndTime(2005, CalendarMonth.APRIL, 25, 13, 49);
            assertTrue(a.compareTo(b) < 0);
            assertTrue(b.compareTo(a) > 0);
            assertTrue(a.compareTo(a) == 0);
            assertTrue(b.compareTo(b) == 0);
        }
        // compare different seconds
        {
            LocalDateAndTime a = new LocalDateAndTime(2005, CalendarMonth.APRIL, 25, 13, 43, 27);
            LocalDateAndTime b = new LocalDateAndTime(2005, CalendarMonth.APRIL, 25, 13, 43, 29);
            assertTrue(a.compareTo(b) < 0);
            assertTrue(b.compareTo(a) > 0);
            assertTrue(a.compareTo(a) == 0);
            assertTrue(b.compareTo(b) == 0);
        }
        logger.info("Ending");
    }

    @Test
    public void testCloningAddition() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testCloningAddition");
        logger.info("Starting");
        LocalDateAndTime ref = new LocalDateAndTime(2005, CalendarMonth.APRIL, 25, 13, 43, 27);
        int delta_h = 2;
        int delta_m = 60 * delta_h;
        int delta_s = 60 * delta_m;
        for (int mult = -20; mult < 20; mult++) {
            LocalDateAndTime ldat1 = ref.addHoursClone(delta_h * mult);
            LocalDateAndTime ldat2 = ref.addMinutesClone(delta_m * mult);
            LocalDateAndTime ldat3 = ref.addSecondsClone(delta_s * mult);
            assertEquals(ldat1, ldat2);
            assertEquals(ldat2, ldat1);
            assertEquals(ldat1, ldat3);
            assertEquals(ldat3, ldat1);
            assertEquals(ldat2, ldat3);
            assertEquals(ldat3, ldat2);
        }
        logger.info("Ending");
    }

    @Test
    public void testConstructionFromDateWallclockInUtc() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testConstructionFromDateWallclockInUtc");
        Date then = new Date(0);
        LocalDateAndTime ldat = new LocalDateAndTime(then); // the wallclock is in UTC
        assertEquals(ldat.getYear(), 1970);
        assertEquals(ldat.getMonth(), CalendarMonth.JANUARY);
        assertEquals(ldat.getDayOfMonth(), 1);
        assertEquals(ldat.getHour(), 0);
        assertEquals(ldat.getMinute(), 0);
        assertEquals(ldat.getSecond(), 0);
        assertEquals(ldat.getAbsoluteIfThisIsUTC(), then);
        logger.info("Ok");
    }

    @Test
    public void testConstructionFromDateWallclockInLuxembourg() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testConstructionFromDateWallclockInLuxembourg");
        Date then = new Date(0);
        TimeZone tz = TimeZone.getTimeZone("Europe/Luxembourg");
        LocalDateAndTime ldat = new LocalDateAndTime(then, tz); // the wallclock is in Luxembourg
        assertEquals("year", ldat.getYear(), 1970);
        assertEquals("month", ldat.getMonth(), CalendarMonth.JANUARY);
        assertEquals("day", ldat.getDayOfMonth(), 1);
        assertEquals("hour", ldat.getHour(), 1); // it is already 1 o'clock
        assertEquals("minute", ldat.getMinute(), 0);
        assertEquals("second", ldat.getSecond(), 0);
        assertEquals("absolute", ldat.getAbsolute(tz), then);
        logger.info("Ok");
    }

    @Test
    public void testConstructionFromDate() {
        // Date '0' is the start of the epoch, in UTC: 1970-01-01 00:00:00
        LocalDateAndTime ldat = new LocalDateAndTime(new Date(0));
        assertEquals(ldat, new LocalDateAndTime(1970, CalendarMonth.JANUARY, 1));
    }

    @Test
    public void testExpressionInSomeTimezone() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testExpressionInSomeTimezone");
        LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 1, 12, 0, 0);
        {
            Date absoluteDate = ldat.getAbsoluteIfThisIsUTC();
            logger.info("'2004-12-01 12:00:00' interpreted in UTC and expressed in UTC: " + DateTexter.ALTERNATE0.inUTC(absoluteDate));
            LocalDateAndTime ldat2 = new LocalDateAndTime(absoluteDate);
            assertEquals(ldat, ldat2);
        }
        {
            TimeZone tz = TimeZone.getTimeZone("Europe/Luxembourg");
            Date wallclockDate = ldat.getAbsolute(tz);
            logger.info("'2004-12-01 12:00:00' interpreted in " + tz.getDisplayName() + " and expressed in UTC: " + DateTexter.ALTERNATE0.inUTC(wallclockDate));
            // to check, create a Date using a wallclocktime expressed in UTC
            LocalDateAndTime ldatcheck = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 1, 11, 0, 0);
            assertEquals(wallclockDate, ldatcheck.getAbsoluteIfThisIsUTC());
        }
    }

    @Test
    public void testCalendaring() {
        {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            // first test: setTime() allows you to do Date->WallclockTime transforms
            cal.setTime(new Date(0));
            // getting the fields yields the 'date' expressed in UTC: 1970-01-01 00:00:00 UTC
            assertEquals(cal.get(Calendar.YEAR), 1970);
            assertEquals(cal.get(Calendar.MONTH), Calendar.JANUARY);
            assertEquals(cal.get(Calendar.DAY_OF_MONTH), 1);
            assertEquals(cal.get(Calendar.HOUR_OF_DAY), 0);
            assertEquals(cal.get(Calendar.MINUTE), 0);
            assertEquals(cal.get(Calendar.SECOND), 0);
            assertEquals(cal.getTime(), new Date(0));
            // second test: set() allows you to do WallclockTime->Date transforms (with partial state)
            cal.set(Calendar.YEAR, 1971);
            Date nextYear = cal.getTime();
            assertEquals(nextYear, new Date(365 * 24 * 3600 * 1000L));
        }
        {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Luxembourg"));
            // third test: setTime() allows you to do Date->WallclockTime transforms in non-UTC timezones
            cal.setTime(new Date(0));
            // getting the fields yields the 'date' expressed in CET: 1970-01-01 01:00:00 CET
            assertEquals(cal.get(Calendar.YEAR), 1970);
            assertEquals(cal.get(Calendar.MONTH), Calendar.JANUARY);
            assertEquals(cal.get(Calendar.DAY_OF_MONTH), 1);
            assertEquals(cal.get(Calendar.HOUR_OF_DAY), 1); // It's one o'clock here!!
            assertEquals(cal.get(Calendar.MINUTE), 0);
            assertEquals(cal.get(Calendar.SECOND), 0);
            assertEquals(cal.getTime(), new Date(0));
            // second test: set() allows you to do WallclockTime->Date transforms (with partial state)
            cal.set(Calendar.YEAR, 1971);
            Date nextYear = cal.getTime();
            assertEquals(nextYear, new Date(365 * 24 * 3600 * 1000L));
        }
        {
            // fourth test: setting the timezone shifts the wallclock time values you get
            // but does **not** change the value of getTime() (one of both has to change, so
            // Sun's design decision was to change the local time)
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            cal.setTime(new Date(0));
            assertEquals(cal.get(Calendar.HOUR_OF_DAY), 0);
            cal.setTimeZone(TimeZone.getTimeZone("Europe/Luxembourg"));
            assertEquals(cal.getTime(), new Date(0)); // time didn't change
            assertEquals(cal.get(Calendar.HOUR_OF_DAY), 1); // ...but it's one o'clock here!!
        }
    }

}
