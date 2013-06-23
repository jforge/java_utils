package tests.core_low;

import com.mplify.checkers.CheckFailedException
import com.mplify.junit.TestStarter;
import com.mplify.queryconstraints.DateTimeSpec
import com.mplify.queryconstraints.DateTimeSpec.CstTimeZone
import com.mplify.queryconstraints.DateTimeSpec.FillOp
import static org.junit.Assert.*
import org.junit.Test

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

class TestCaseDateTimeSpec extends TestStarter {

    @Test
    void testCompleteSpecification() {
        t1: {
            // standard, correct date
            DateTimeSpec tc = new DateTimeSpec(2001,12,12,13,14,15,CstTimeZone.GMT, true)
            assertEquals 'YEAR:2001,MONTH:12,DAY:12,HOUR:13,MINUTE:14,SECOND:15,[GMT]',tc.toString()
            assertEquals '2001-12-12 13:14:15',tc.toCalendarString()
        }
        t2: {
            // standard, correct date
            DateTimeSpec tc = new DateTimeSpec(2001,12,12,13,14,15,CstTimeZone.LUX, true)
            assertEquals 'YEAR:2001,MONTH:12,DAY:12,HOUR:13,MINUTE:14,SECOND:15,[LUX]',tc.toString()
            assertEquals '2001-12-12 13:14:15', tc.toCalendarString()
        }
        t3: {
            // wrong date, leniently interpreted
            DateTimeSpec tc = new DateTimeSpec(2001,2,30,36,17,18,CstTimeZone.LUX, true)
            assertEquals 'YEAR:2001,MONTH:2,DAY:30,HOUR:36,MINUTE:17,SECOND:18,[LUX]',tc.toString()
            assertEquals '2001-03-03 12:17:18', tc.toCalendarString()
        }
        t4: {
            // wrong date, non-leniently interpreted
            try {
                DateTimeSpec tc = new DateTimeSpec(2001,2,30,36,17,18,CstTimeZone.LUX, false)
                fail("Expected a check failed exception")
            }
            catch (CheckFailedException exe) {
                // expected
            }
        }
    }
    
    @Test
    void testPartialSpecification() {
        t1: {
            // standard, correct date
            DateTimeSpec tc = new DateTimeSpec(2001,12,12,null,null,null,CstTimeZone.GMT, true)
            assertEquals 'YEAR:2001,MONTH:12,DAY:12,[GMT]',tc.toString()
            println "T1 " + tc.toCalendarString()
            // ==~ matches the string wholly, no need to add ^ and $ anchors
            assertTrue "TPS1", tc.toCalendarString() ==~ /2001\-12\-12 .*/
        }
        t2: {
            // standard, correct date
            DateTimeSpec tc = new DateTimeSpec(2001,null,12,null,20,null,CstTimeZone.GMT, true)
            assertEquals 'YEAR:2001,DAY:12,MINUTE:20,[GMT]',tc.toString()
            println "T2 " + tc.toCalendarString()
            // ==~ matches the string wholly, no need to add ^ and $ anchors
            assertTrue "TPS2", tc.toCalendarString() ==~ /2001\-\d\d\-12 \d\d:20:\d\d/
        }
        t3: {
            // fully empty date, GMT
            DateTimeSpec tc = new DateTimeSpec(null,null,null,null,null,null,CstTimeZone.GMT, true)
            assertEquals '[GMT]',tc.toString()
            println "T3 " + tc.toCalendarString() // basically, just the current time in GMT
        }
        t3x: {
            // fully empty date, UTC
            DateTimeSpec tc = new DateTimeSpec(null,null,null,null,null,null,CstTimeZone.LUX, true)
            assertEquals '[LUX]',tc.toString()
            println "T3X " + tc.toCalendarString() // basically, just the current time in LUX
        }
        t4: {
            // wrong date, lenient interpretation
            DateTimeSpec tc = new DateTimeSpec(2001,23,1,null,null,null,CstTimeZone.GMT, true)
            assertEquals 'YEAR:2001,MONTH:23,DAY:1,[GMT]',tc.toString()
            println "T4 " + tc.toCalendarString()
            assertTrue "TPS4", tc.toCalendarString() ==~ /2002\-11\-01 .*/
        }
        t5: {
            // wrong date, lenient interpretation
            DateTimeSpec tc = new DateTimeSpec(2001,10,11,25,70,-12,CstTimeZone.GMT, true)
            assertEquals 'YEAR:2001,MONTH:10,DAY:11,HOUR:25,MINUTE:70,SECOND:-12,[GMT]',tc.toString()
            println "T5 " + tc.toCalendarString()
            assertTrue "TPS5", tc.toCalendarString() ==~ /2001\-10\-12 02:09:48/
        }
        t6: {
            // wrong date, lenient interpretation
            DateTimeSpec tc = new DateTimeSpec(2001,1,1,24,00,00,CstTimeZone.GMT, true)
            assertEquals 'YEAR:2001,MONTH:1,DAY:1,HOUR:24,MINUTE:0,SECOND:0,[GMT]',tc.toString()
            println "T6 " + tc.toCalendarString()
            assertTrue "TPS6", tc.toCalendarString() ==~ /2001\-01\-02 00:00:00/
        }
        t7: {
            // wrong date, non-lenient interpretation
            try {
                DateTimeSpec tc = new DateTimeSpec(2001,23,null,null,null,null,CstTimeZone.GMT, false)
                fail("Expected a check failed exception")
            } catch (CheckFailedException exe) {
                // expected
            }
        }
    }
    
    @Test
    void testFieldCompletion() {
        c1: {
            DateTimeSpec tc = new DateTimeSpec(2001,12,12,null,null,null,CstTimeZone.GMT, true)
            assertEquals 'YEAR:2001,MONTH:12,DAY:12,[GMT]',tc.toString()
            tc.completeFields(FillOp.LOWEST_VALUE, null)
            assertEquals 'YEAR:2001,MONTH:12,DAY:12,HOUR:0,MINUTE:0,SECOND:0,[GMT]',tc.toString()
            println "C1 " + tc.toCalendarString()
        }
        c2: {
            DateTimeSpec tc = new DateTimeSpec(2001,12,null,null,null,null,CstTimeZone.GMT, true)
            assertEquals 'YEAR:2001,MONTH:12,[GMT]',tc.toString()
            tc.completeFields(FillOp.HIGHEST_VALUE, null)
            assertEquals 'YEAR:2001,MONTH:12,DAY:31,HOUR:23,MINUTE:59,SECOND:59,[GMT]',tc.toString()
            println "C2 " + tc.toCalendarString()
        }
        c3: {
            DateTimeSpec tc = new DateTimeSpec(2000,02,null,14,15,16,CstTimeZone.GMT, true)
            assertEquals 'YEAR:2000,MONTH:2,HOUR:14,MINUTE:15,SECOND:16,[GMT]',tc.toString()
            tc.completeFields(FillOp.HIGHEST_VALUE, null)
            assertEquals 'YEAR:2000,MONTH:2,DAY:29,HOUR:14,MINUTE:15,SECOND:16,[GMT]',tc.toString() // Y2K has 29th February
            println "C3 " + tc.toCalendarString()
        }
        c4: {
            DateTimeSpec tc = new DateTimeSpec(2000,10,null,null,null,null,CstTimeZone.LUX, true)
            assertEquals 'YEAR:2000,MONTH:10,[LUX]',tc.toString()
            tc.completeFields(FillOp.CURRENT_VALUE, new Date(13445566))
            assertEquals 'YEAR:2000,MONTH:10,DAY:1,HOUR:4,MINUTE:44,SECOND:5,[LUX]',tc.toString()
            println "C4 " + tc.toCalendarString()
        }

    }
}
