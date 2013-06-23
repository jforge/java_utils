package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.junit.TestStarter;
import com.mplify.timehandling.LocalTime;
import com.mplify.timehandling.LocalTimeParser;
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
public class TestCaseLocalTimeParser extends TestStarter {

	private final static String CLASS = TestCaseLocalTimeParser.class.getName();

	@Test
	public void testParsing() {
		Logger logger = LoggerFactory.getLogger(CLASS + ".testParsing");
		logger.info("Starting");
		assertEquals(new LocalTime(13, 44), LocalTimeParser.parse("13:44"));
		assertEquals(new LocalTime(13, 44, 12), LocalTimeParser.parse("13:44:12"));
		assertEquals(new LocalTime(134, 44, 99), LocalTimeParser.parse("134:44:99"));
		assertEquals(new LocalTime(-34, -44, -12), LocalTimeParser.parse("-34:44:12"));
		logger.info("Ending");
	}

	@Test
	public void testParsingFailure() {
		Logger logger = LoggerFactory.getLogger(CLASS + ".testParsingFailure");
		logger.info("Starting");
		try {
			LocalTimeParser.parse("13:13:448");
			fail("There should have been an exception: 3 digits in seconds value");
		} catch (IllegalArgumentException exe) {
			// that's ok
		}
		try {
			LocalTimeParser.parse("13:448");
			fail("There should have been an exception: 3 digits in minute value");
		} catch (IllegalArgumentException exe) {
			// that's ok			
		}
		try {
			LocalTimeParser.parse("13");
			fail("There should have been an exception: single number");
		} catch (IllegalArgumentException exe) {
			// that's ok			
		}
		logger.info("Ending");
	}
}
