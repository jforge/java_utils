package tests.core_low;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.junit.TestStarter;
import com.mplify.logging.DateTexter;
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
 * Testing the DateTexter class.
 * 
 * 2005.05.02 - Created
 * 2005.10.18 - French text test added
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 *******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseDateTexter extends TestStarter {

    private final static String CLASS = TestCaseDateTexter.class.getName();

    @Test
    public void testTextingUTC() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testTextingUTC");
        LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.MAY, 2, 15, 16, 17);
        Date in = ldat.getAbsoluteIfThisIsUTC();
        assertEquals("2005-05-02 15:16:17", DateTexter.ALTERNATE0.inUTC(in));
        assertEquals("2005-05-02 15:16", DateTexter.ALTERNATE1.inUTC(in));
        assertEquals("2005-05-02", DateTexter.ALTERNATE2.inUTC(in));
        assertEquals("02/05/2005 15:16:17", DateTexter.ALTERNATE3.inUTC(in));
        assertEquals("02/05/2005 15:16", DateTexter.ALTERNATE4.inUTC(in));
        assertEquals("02/05/2005", DateTexter.ALTERNATE5.inUTC(in));
        assertEquals("2005.05.02", DateTexter.ALTERNATE6.inUTC(in));
        assertEquals("2005_05_02_15_16_17", DateTexter.ALTERNATE7.inUTC(in));
        assertEquals("15:16:17:000", DateTexter.TIME0.inUTC(in));
        assertEquals("15:16:17", DateTexter.TIME1.inUTC(in));
        assertEquals("15:16", DateTexter.TIME2.inUTC(in));
        logger.info("Ok");
    }

    @Test
    public void testTextingDefault() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testTextingDefault");
        LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.MAY, 2, 15, 16, 17);
        Date in = ldat.getAbsolute(TimeZone.getDefault());
        assertEquals("2005-05-02 15:16:17", DateTexter.ALTERNATE0.inDefault(in));
        assertEquals("2005-05-02 15:16", DateTexter.ALTERNATE1.inDefault(in));
        assertEquals("2005-05-02", DateTexter.ALTERNATE2.inDefault(in));
        assertEquals("02/05/2005 15:16:17", DateTexter.ALTERNATE3.inDefault(in));
        assertEquals("02/05/2005 15:16", DateTexter.ALTERNATE4.inDefault(in));
        assertEquals("02/05/2005", DateTexter.ALTERNATE5.inDefault(in));
        assertEquals("2005.05.02", DateTexter.ALTERNATE6.inDefault(in));
        assertEquals("2005_05_02_15_16_17", DateTexter.ALTERNATE7.inDefault(in));
        assertEquals("15:16:17:000", DateTexter.TIME0.inDefault(in));
        assertEquals("15:16:17", DateTexter.TIME1.inDefault(in));
        assertEquals("15:16", DateTexter.TIME2.inDefault(in));
        logger.info("Ok");
    }

    @Test
    public void testTextAndParseForDate() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testTextAndParseForDate");
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.MAY, 2, 15, 16, 17);
            Date in = ldat.getAbsoluteIfThisIsUTC();
            String dateStr = DateTexter.ALTERNATE0.inUTC(in);
            Date out = DateParser.getDateFromStr(dateStr);
            assertEquals("Testing ALTERNATE0 with " + dateStr, in, out);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.MAY, 2, 15, 16);
            Date in = ldat.getAbsoluteIfThisIsUTC();
            String dateStr = DateTexter.ALTERNATE1.inUTC(in);
            Date out = DateParser.getDateFromStr(dateStr);
            assertEquals("Testing ALTERNATE1 with " + dateStr, in, out);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.MAY, 2);
            Date in = ldat.getAbsoluteIfThisIsUTC();
            String dateStr = DateTexter.ALTERNATE2.inUTC(in);
            Date out = DateParser.getDateFromStr(dateStr);
            assertEquals("Testing ALTERNATE2 with " + dateStr, in, out);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.MAY, 2, 15, 16, 17);
            Date in = ldat.getAbsoluteIfThisIsUTC();
            String dateStr = DateTexter.ALTERNATE3.inUTC(in);
            Date out = DateParser.getDateFromStr(dateStr);
            assertEquals("Testing ALTERNATE3 with " + dateStr, in, out);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.MAY, 2, 15, 16);
            Date in = ldat.getAbsoluteIfThisIsUTC();
            String dateStr = DateTexter.ALTERNATE4.inUTC(in);
            Date out = DateParser.getDateFromStr(dateStr);
            assertEquals("Testing ALTERNATE4 with " + dateStr, in, out);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.MAY, 2);
            Date in = ldat.getAbsoluteIfThisIsUTC();
            String dateStr = DateTexter.ALTERNATE5.inUTC(in);
            Date out = DateParser.getDateFromStr(dateStr);
            assertEquals("Testing ALTERNATE5 with " + dateStr, in, out);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.MAY, 2);
            Date in = ldat.getAbsoluteIfThisIsUTC();
            String dateStr = DateTexter.ALTERNATE6.inUTC(in);
            Date out = DateParser.getDateFromStr(dateStr);
            assertEquals("Testing ALTERNATE6 with " + dateStr, in, out);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.MAY, 2, 15, 16, 17);
            Date in = ldat.getAbsoluteIfThisIsUTC();
            String dateStr = DateTexter.ALTERNATE7.inUTC(in);
            Date out = DateParser.getDateFromStr(dateStr);
            assertEquals("Testing ALTERNATE7 with " + dateStr, in, out);
        }
        logger.info("Ok");
    }

    @Test
    public void testTextAndParseForTime() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testTextAndParseForTime");
        {
            LocalTime lt = new LocalTime(15, 16, 17);
            Date in = new Date(lt.getEquivalentTime() * 1000);
            String timeStr = DateTexter.TIME0.inUTC(in);
            Date out = DateParser.getTimeFromStr(timeStr);
            assertEquals("Tesing TIME0 with " + timeStr, in, out);
        }
        {
            LocalTime lt = new LocalTime(15, 16, 17);
            Date in = new Date(lt.getEquivalentTime() * 1000);
            String timeStr = DateTexter.TIME1.inUTC(in);
            Date out = DateParser.getTimeFromStr(timeStr);
            assertEquals("Tesing TIME1 with " + timeStr, in, out);
        }

        {
            LocalTime lt = new LocalTime(15, 16);
            Date in = new Date(lt.getEquivalentTime() * 1000);
            String timeStr = DateTexter.TIME2.inUTC(in);
            Date out = DateParser.getTimeFromStr(timeStr);
            assertEquals("Tesing TIME2 with " + timeStr, in, out);
        }
        logger.info("Ok");
    }

    @Test
    public void testTextInFrench() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testTextInFrench");
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.MAY, 2, 15, 16, 17);
            Date in = ldat.getAbsoluteIfThisIsUTC();
            String text = DateTexter.ALTERNATE8.inUTC(in);
            assertEquals("2 mai 2005 à 15:16:17", text);
        }
        {
            LocalDateAndTime ldat = new LocalDateAndTime(2005, CalendarMonth.JANUARY, 31, 15, 16, 17);
            Date in = ldat.getAbsoluteIfThisIsUTC();
            String text = DateTexter.ALTERNATE8.inUTC(in);
            assertEquals("31 janvier 2005 à 15:16:17", text);
        }
        logger.info("Ok");
    }
}
