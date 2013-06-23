package tests.core_low;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.mplify.junit.TestStarter;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Run all the tests registered in this TestSuite.
 * 
 * This class:
 * 
 * 1) Can be run via the Eclipse-integrated JUnit 4 Runner because of the
 *    annotations.
 *    
 * 3) Can be run as a "Java Application" via main() because 
 *    MainForTestCases._main picks up the @SuiteClasses and puts them into a
 *    TestSuite
 * 
 * 2008.11.29 - Created
 * 2010.10.09 - Reviewed
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 * 2011.10.18 - Adapted to JUnit 4
 * 2013.01.02 - Adapted to use TestStarter and JUnit 4 
 * 2013.01.29 - Added com.mplify.logic.TestLogicExpr
 * 2013.02.18 - Added TestCaseXMLHelper2
 *******************************************************************************/

@RunWith(Suite.class)
@SuiteClasses({ TestCaseAddressAcceptor.class, TestCaseCalendarMonth.class, TestCaseCheck.class, TestCaseCountryId.class, TestCaseCrypto.class, TestCaseDateParser.class, TestCaseDateTexter.class,
        TestCaseDateTimeConstraint.class, TestCaseDateTimeSpec.class, TestCaseDottedAbstractName.class, TestCaseGetAsPort.class, TestCaseIntConstraint.class,
        TestCaseLocalDate.class, TestCaseLocalDateAndTime.class, TestCaseLocalTime.class, TestCaseLocalTimeParser.class, TestCaseMarshalUnmarshalPayload.class,
        TestCaseMarshalUnmarshalProperties.class, TestCasePayload.class, TestCasePhoneNumber.class, TestCaseRfc3066LangTag.class, TestCaseStringPacking.class, TestCaseTvec.class,
        TestReadingFilesWithNonAsciiFilenames.class, TestCaseConfirmationRequestSet.class,
        com.mplify.logic.TestCaseLogicExpr.class, TestCaseMutableBoolean.class, TestCaseXMLHelper2.class })
public class AllTests extends TestStarter {

    // NOP, currently just a holder for annotations to be used by a JUnit Runner

}
