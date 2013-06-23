package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.junit.TestStarter;
import com.mplify.timehandling.CalendarMonth;
import com.mplify.timehandling.DateParser;
import com.mplify.timehandling.LocalDateAndTime;
import com.mplify.timehandling.LocalTime;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Testing the DateParser class.
 * 
 * 2005.04.22 - Created
 * 2008.05.15 - Review
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 *******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseDateParser extends TestStarter {

    private final static String CLASS = TestCaseDateParser.class.getName();

    public final static String DATE_FORMAT_TIME0 = "HH:mm:ss:SSS";
    public final static String DATE_FORMAT_TIME1 = "HH:mm:ss";
    public final static String DATE_FORMAT_TIME2 = "HH:mm";

    @Test
    public void testParsingOfDates() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testParsingOfDates");
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11, 9, 16, 33);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("2004-12-11 09:16:33");
            assertEquals("Test with " + ldat.toString(), v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11, 9, 16, 33);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("  2004-12-11 09:16:33  ");
            assertEquals("Test with " + ldat.toString() + " and noisy whitespace", v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11, 9, 16);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("2004-12-11 09:16");
            assertEquals("Test with " + ldat.toString(), v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11, 9, 16);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("   2004-12-11 09:16   ");
            assertEquals("Test with " + ldat.toString() + " and noisy whitespace", v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("2004-12-11");
            assertEquals("Test with " + ldat.toString(), v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("   2004-12-11   ");
            assertEquals("Test with " + ldat.toString() + " and noisy whitespace", v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11, 9, 16, 33);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("11/12/2004 09:16:33");
            assertEquals("Test with " + ldat.toString(), v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11, 9, 16, 33);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("  11/12/2004 09:16:33  ");
            assertEquals("Test with " + ldat.toString() + " and noisy whitespace", v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11, 9, 16);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("11/12/2004 09:16");
            assertEquals("Test with " + ldat.toString(), v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11, 9, 16);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("  11/12/2004 09:16  ");
            assertEquals("Test with " + ldat.toString() + " and noisy whitespace", v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("11/12/2004");
            assertEquals("Test with " + ldat.toString(), v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("  11/12/2004  ");
            assertEquals("Test with " + ldat.toString() + " and noisy whitespace", v1, v2);
        }
        /* Currently fails
        {
        	LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11);
        	Date v1 = ldat.getPointInTimeAsUTC();
        	Date v2 = DateParser.getDateFromStr("11-12-2004");
        	assertEquals("Test 7",v1, v2);
        }
        */{
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11, 9, 16, 33);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("2004_12_11_09_16_33");
            assertEquals("Test with " + ldat.toString(), v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11, 9, 16, 33);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("   2004_12_11_09_16_33   ");
            assertEquals("Test with " + ldat.toString() + " and noisy whitespace", v1, v2);
        }
        {
            Date v = DateParser.getDateFromStr(null);
            assertNull("Test with null", v);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("2004.12.11");
            assertEquals("Test with " + ldat.toString(), v1, v2);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2004, CalendarMonth.DECEMBER, 11);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("   2004.12.11   ");
            assertEquals("Test with " + ldat.toString() + " and noisy whitespace", v1, v2);
        }
        {
            Date v = DateParser.getDateFromStr("The quick brown fox jumps over the lazy dog");
            assertNull("Test where no parsing possible: fox", v);
        }
        {
            Date v = DateParser.getDateFromStr("14:22:13");
            assertNull("Test where no parsing possible: time", v);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.MARCH, 27);
            Date v1 = ldat.getAbsoluteIfThisIsUTC();
            Date v2 = DateParser.getDateFromStr("2004.14.55");
            assertEquals("Test with lenient parsing (i.e. out-of-range values are ok)", v1, v2);
        }
        logger.info("Ok");
    }

    @Test
    public void testParsingOfTimes() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testParsingOfTimes");
        {
            LocalTime lt = new LocalTime(14, 12);
            Date v1 = new Date(lt.getEquivalentTime() * 1000); // counted from 1st January 1970 UTC
            Date v2 = DateParser.getTimeFromStr("14:12");
            assertEquals("Test with " + lt.toString(), v1, v2);
        }
        {
            LocalTime lt = new LocalTime(14, 12);
            Date v1 = new Date(lt.getEquivalentTime() * 1000); // counted from 1st January 1970 UTC
            Date v2 = DateParser.getTimeFromStr("  14:12  ");
            assertEquals("Test with " + lt.toString() + " and noisy whitespace", v1, v2);
        }
        {
            LocalTime lt = new LocalTime(14, 12, 11);
            Date v1 = new Date(lt.getEquivalentTime() * 1000); // counted from 1st January 1970 UTC
            Date v2 = DateParser.getTimeFromStr("14:12:11");
            assertEquals("Test with " + lt.toString(), v1, v2);
        }
        {
            LocalTime lt = new LocalTime(14, 12, 11);
            Date v1 = new Date(lt.getEquivalentTime() * 1000); // counted from 1st January 1970 UTC
            Date v2 = DateParser.getTimeFromStr("  14:12:11  ");
            assertEquals("Test with " + lt.toString() + " and noisy whitespace", v1, v2);
        }
        {
            LocalTime lt = new LocalTime(14, 12, 11);
            Date v1 = new Date(lt.getEquivalentTime() * 1000 + 877); // counted from 1st January 1970 UTC, plus 877
                                                                     // ms
            Date v2 = DateParser.getTimeFromStr("14:12:11:877");
            assertEquals("Test with " + lt.toString() + ":877", v1, v2);
        }
        {
            LocalTime lt = new LocalTime(14, 12, 11);
            Date v1 = new Date(lt.getEquivalentTime() * 1000 + 877); // counted from 1st January 1970 UTC, plus 877
                                                                     // ms
            Date v2 = DateParser.getTimeFromStr("   14:12:11:877  ");
            assertEquals("Test with " + lt.toString() + ":877 and noisy whitespace", v1, v2);
        }
        {
            LocalTime lt = new LocalTime(100, 78, 0);
            Date v1 = new Date(lt.getEquivalentTime() * 1000);
            Date v2 = DateParser.getTimeFromStr("100:78");
            assertEquals("Test with lenient parsing (i.e. out-of-range values are ok)", v1, v2);
        }
        logger.info("Ok");
    }
}
