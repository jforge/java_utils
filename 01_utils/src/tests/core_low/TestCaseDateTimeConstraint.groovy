package tests.core_low;

import static org.junit.Assert.*;

import com.mplify.junit.TestStarter;
import com.mplify.queryconstraints.DateTimeConstraint
import com.mplify.queryconstraints.NoMatchException
import org.junit.Test;


/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Test the "DateTimeSpec" class
 *
 * 2011.10.13 - Created
 ******************************************************************************/

class TestCaseDateTimeConstraint extends TestStarter {

    @Test
    void testTrivial() {
        assertEquals('T1','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,SECOND:16,[GMT]', new DateTimeConstraint('2011-12-13 14:15:16 UTC').toTestString())
        assertEquals('T2','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,SECOND:16,[GMT]', new DateTimeConstraint('2011-12-13 14:15:16 GMT').toTestString())
        assertEquals('T3','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,SECOND:16,[GMT]', new DateTimeConstraint('2011-12-13 14:15:16 G').toTestString())
        assertEquals('T4','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,SECOND:16,[GMT]', new DateTimeConstraint('2011-12-13 14:15:16 U').toTestString())
        assertEquals('T5','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,SECOND:16,[GMT]', new DateTimeConstraint('2011-12-13 14:15:16UTC').toTestString())
        assertEquals('T6','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,SECOND:16,[GMT]', new DateTimeConstraint('2011-12-13 14:15:16GMT').toTestString())
        assertEquals('T7','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,SECOND:16,[LUX]', new DateTimeConstraint('2011-12-13 14:15:16 LUX').toTestString())
        assertEquals('T8','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,SECOND:16,[LUX]', new DateTimeConstraint('2011-12-13 14:15:16 L').toTestString())
        assertEquals('T9','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,SECOND:16,[LUX]', new DateTimeConstraint('2011-12-13 14:15:16').toTestString())
        assertEquals('T9','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,SECOND:16,[LUX]', new DateTimeConstraint('2011.12 13 14:15:16').toTestString())
        assertEquals('T10','YEAR:2011,MONTH:1,DAY:1,HOUR:1,MINUTE:1,SECOND:1,[GMT]', new DateTimeConstraint('2011-1-1 1:1:1 UTC').toTestString())
        assertEquals('T11','YEAR:2011,MONTH:1,DAY:1,HOUR:1,MINUTE:1,SECOND:1,[GMT]', new DateTimeConstraint('2011 -  1 - 1 1 : 1 : 1  UTC').toTestString())
        assertEquals('T12','YEAR:2011,MONTH:1,DAY:1,HOUR:1,MINUTE:1,SECOND:1,[GMT]', new DateTimeConstraint('11 -  1 - 1 1 : 1 : 1  UTC').toTestString())
        assertEquals('T13','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,SECOND:16,[GMT]', new DateTimeConstraint('2011.12.13 14:15:16 GMT').toTestString())
        assertEquals('T13','YEAR:2000,MONTH:2,DAY:50,HOUR:14,MINUTE:15,SECOND:16,[GMT]', new DateTimeConstraint('0.2.50 14:15:16 GMT').toTestString())
    }

    @Test
    void testDownToMinute() {
        assertEquals('DTM1','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,[GMT]', new DateTimeConstraint('2011-12-13 14:15: UTC').toTestString())
        assertEquals('DTM2','YEAR:2011,MONTH:12,DAY:13,HOUR:14,MINUTE:15,[LUX]', new DateTimeConstraint('2011-12-13 14:15  LUX').toTestString())
    }
    
    @Test
    void testDownToHour() {
        assertEquals('DTH1','YEAR:2011,MONTH:12,DAY:13,HOUR:14,[GMT]', new DateTimeConstraint('2011-12-13 14:: UTC').toTestString())
        assertEquals('DTH2','YEAR:2011,MONTH:12,DAY:13,HOUR:14,[GMT]', new DateTimeConstraint('2011-12-13 14: UTC').toTestString())
        assertEquals('DTH3','YEAR:2011,MONTH:12,DAY:13,HOUR:14,[GMT]', new DateTimeConstraint('2011-12-13 14  UTC').toTestString())
    }

    @Test
    void testDownToDay() {
        assertEquals('DTD1','YEAR:2011,MONTH:12,DAY:13,[GMT]', new DateTimeConstraint('2011-12-13 UTC').toTestString())
        assertEquals('DTD2','YEAR:2011,MONTH:12,DAY:13,[GMT]', new DateTimeConstraint('2011.12.13 UTC').toTestString())
        assertEquals('DTD3','YEAR:2011,MONTH:12,DAY:13,[GMT]', new DateTimeConstraint('2011 12 13 UTC').toTestString())
        assertEquals('DTD4','YEAR:2011,MONTH:12,DAY:13,[GMT]', new DateTimeConstraint('2011-12 13 UTC').toTestString())
    }

    @Test
    void testDownToMonth() {
        assertEquals('DTMo1','YEAR:2011,MONTH:12,[GMT]', new DateTimeConstraint('2011-12 UTC').toTestString())
        assertEquals('DTMo2','YEAR:2011,MONTH:12,[GMT]', new DateTimeConstraint('2011.12 UTC').toTestString())
        assertEquals('DTMo3','YEAR:2011,MONTH:12,[GMT]', new DateTimeConstraint('2011 12 UTC').toTestString())
        assertEquals('DTMo4','YEAR:2011,MONTH:12,[GMT]', new DateTimeConstraint('2011-12- UTC').toTestString())
        assertEquals('DTMo5','YEAR:2011,MONTH:12,[GMT]', new DateTimeConstraint('2011-12. UTC').toTestString())
    }

    @Test
    void testDownToYear() {
        assertEquals('DTY1','YEAR:2011,[GMT]', new DateTimeConstraint('2011-- UTC').toTestString())
        assertEquals('DTY2','YEAR:2011,[GMT]', new DateTimeConstraint('2011.. UTC').toTestString())
        assertEquals('DTY3','YEAR:2011,[GMT]', new DateTimeConstraint('2011- UTC').toTestString())
        assertEquals('DTY4','YEAR:2011,[GMT]', new DateTimeConstraint('2011. UTC').toTestString())
        try {
            new DateTimeConstraint('2011 UTC')
            fail("Bare number not allowed")
        } catch (NoMatchException exe) {
            // expected
        }
        try {
            new DateTimeConstraint('11 UTC')
            fail("Bare number not allowed")
        }
        catch (NoMatchException exe) {
            // expected
        }
    }

    @Test
    void testHMS() {
        assertEquals('HMS1','HOUR:12,MINUTE:13,SECOND:14,[GMT]', new DateTimeConstraint('12:13:14 UTC').toTestString())
    }

    @Test
    void testHM() {
        assertEquals('HM1','HOUR:12,MINUTE:13,[GMT]', new DateTimeConstraint('12:13: UTC').toTestString())
        assertEquals('HM2','HOUR:12,MINUTE:13,[GMT]', new DateTimeConstraint('12:13 UTC').toTestString())
    }

    @Test
    void testH() {
        assertEquals('H1','HOUR:12,[GMT]', new DateTimeConstraint('12:: UTC').toTestString())
        assertEquals('H2','HOUR:12,[GMT]', new DateTimeConstraint('12: UTC').toTestString())
        try {
            new DateTimeConstraint('12 UTC')
            fail("Bare number not allowed")
        }
        catch (NoMatchException exe) {
            // expected
        }
    }
}
