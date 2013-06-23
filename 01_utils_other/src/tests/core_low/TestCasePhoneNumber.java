package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


import com.mplify.countries.DirectDialPrefixId;
import com.mplify.junit.TestStarter;
import com.mplify.phonenumber.PhoneNumber;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Testing the PhoneNumber class.
 * 
 * 2005.04.22 - Created (actually not but wthat the hey)
 * 2006.07.28 - Added code due to modified PhoneNumber
 * 2007.01.03 - Moved to its own package
 * 2007.05.04 - The tests fail because the new numbering of 
 * 		        of 2006-09-01 has been introduced. Fixed! (That should have been
 *              BeTo's task, should it not?)
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 *******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCasePhoneNumber extends TestStarter {

//    private final static String CLASS = TestCasePhoneNumber.class.getName();

    boolean isaMobileNumber;

    /**
     * Helper method calling the static canonicizePhoneNumber() method and checking the result.
     */

    private static void canonicizeNumberStatically(String input, String expectedOutput, boolean isaMobileNumberInput) {
        String res = PhoneNumber.canonicizePhoneNumber(input, isaMobileNumberInput);
        if (expectedOutput == null) {
            assertNull("The result should be (null) but is '" + res + "' for input '" + input + "'", res);
        } else {
            assertEquals("The result should be '" + expectedOutput + "' but is '" + res + "' for input '" + input + "'", expectedOutput, res);
        }
    }

    /**
     * Helper method creating a new PhoneNumber instance, but expecting an Exception
     */

    private static void canonicizeNumberByInstantiationExpectingException(String input, boolean isaMobileNumberInput) {
//        Logger logger = LoggerFactory.getLogger(CLASS + ".canonicizeNumberByInstantiationExpectingException");
        try {
            PhoneNumber pn = new PhoneNumber(input, isaMobileNumberInput);
            fail("There should have been an exception for '" + input + "'");
        } catch (IllegalArgumentException exe) {
            // ok, exception expected
            return;
        }
    }

    /**
     * Helper method creating a new PhoneNumber instance and checking the result
     */

    private static void canonicizeNumberByInstantiation(String input, DirectDialPrefixId expectedCcId, String expectedLocal, boolean isaMobileNumberInput) {
//        Logger logger = LoggerFactory.getLogger(CLASS + ".canonicizeNumberByInstantiation");
        PhoneNumber pn = new PhoneNumber(input, isaMobileNumberInput);
        assertEquals(expectedCcId, pn.getCountryCodeId());
        assertEquals(expectedLocal, pn.getLocalNumber());
        assertEquals(expectedCcId.toString() + pn.getLocalNumber(), pn.getCanonicizedComposite());
    }

    @Test
    public void testStaticPhoneNumberCanonicizationBadCases() {
        canonicizeNumberStatically("", null, isaMobileNumber = false);
        canonicizeNumberStatically(null, null, isaMobileNumber = false);
        canonicizeNumberStatically("ABC", null, isaMobileNumber = false);
        canonicizeNumberStatically("+352021ABC555", null, isaMobileNumber = false);
        canonicizeNumberStatically("", null, isaMobileNumber = true);
        canonicizeNumberStatically(null, null, isaMobileNumber = true);
        canonicizeNumberStatically("ABC", null, isaMobileNumber = true);
        canonicizeNumberStatically("+352021ABC555", null, isaMobileNumber = true);
    }

    @Test
    public void testStaticPhoneNumberCanonicizationBadLuxoNumbers() {
        canonicizeNumberStatically("+352071-123456", null, isaMobileNumber = true);
        canonicizeNumberStatically("352071-123456", null, isaMobileNumber = true);
        canonicizeNumberStatically("071-123456", null, isaMobileNumber = true);
    }

    @Test
    public void testStaticPhoneNumberCanonicizationShortThusBadLuxoNumbers() {
        canonicizeNumberStatically("+352091-23456", null, isaMobileNumber = true);
        canonicizeNumberStatically("352091-23456", null, isaMobileNumber = true);
        canonicizeNumberStatically("091-23456", null, isaMobileNumber = true);
    }

    @Test
    public void testStaticPhoneNumberCanonicizationLongThusBadLuxoNumbers() {
        canonicizeNumberStatically("+352691-2345678", null, isaMobileNumber = true);
        canonicizeNumberStatically("352691-2345678", null, isaMobileNumber = true);
        canonicizeNumberStatically("691-2345678", "6912345678", isaMobileNumber = true);
    }

    @Test
    public void testStaticPhoneNumberCanonicizationAnyInternationalNumber() {
        canonicizeNumberStatically("441234-5679-910", "4412345679910", isaMobileNumber = false);
        canonicizeNumberStatically("00441234-5679-910", "4412345679910", isaMobileNumber = false);
        canonicizeNumberStatically("+441234-5679-910", "4412345679910", isaMobileNumber = false);
    }

    @Test
    public void testStaticPhoneNumberCanonicizationVoxMobileNumbersOldStyle() {
        // note that 'old style 061' numbers are transformed to 'new style 661' numbers
        canonicizeNumberStatically("+352061-123456", "352661123456", isaMobileNumber = true);
        canonicizeNumberStatically("352061-123456", "352661123456", isaMobileNumber = true);
        canonicizeNumberStatically("061-123456", "352661123456", isaMobileNumber = true);
        canonicizeNumberStatically("+352061-123456", "352661123456", isaMobileNumber = false);
        canonicizeNumberStatically("352061-123456", "352661123456", isaMobileNumber = false);
        // the same as above for voicemail numbers
        /*
         canonicizeNumberStatically("+352068-123456", "352668123456", isaMobileNumber = true);
         canonicizeNumberStatically("352068-123456", "352668123456", isaMobileNumber = true);
         canonicizeNumberStatically("068-123456", "352668123456", isaMobileNumber = true);
         canonicizeNumberStatically("+352068-123456", "352668123456", isaMobileNumber = false);
         canonicizeNumberStatically("352068-123456", "352668123456", isaMobileNumber = false);
         */
        // compare "061 is mobile" and "061 is not necessarily mobile"
        canonicizeNumberStatically("061-123456", "352661123456", isaMobileNumber = true);
        canonicizeNumberStatically("061-123456", null, isaMobileNumber = false); // won't be recognized in case of 'not necessarily mobile'
    }

    @Test
    public void testStaticPhoneNumberCanonicizationVoxMobileNumbersNewStyle() {
        canonicizeNumberStatically("+352661-123456", "352661123456", isaMobileNumber = true);
        canonicizeNumberStatically("352661-123456", "352661123456", isaMobileNumber = true);
        canonicizeNumberStatically("661-123456", "352661123456", isaMobileNumber = true);
        canonicizeNumberStatically("+352661-123456", "352661123456", isaMobileNumber = false);
        canonicizeNumberStatically("352661-123456", "352661123456", isaMobileNumber = false);
        // the same as above for voicemail numbers
        /*
         canonicizeNumberStatically("+352668-123456", "352668123456", isaMobileNumber = true);
         canonicizeNumberStatically("352668-123456", "352668123456", isaMobileNumber = true);
         canonicizeNumberStatically("668-123456", "352668123456", isaMobileNumber = true);
         canonicizeNumberStatically("+352668-123456", "352668123456", isaMobileNumber = false);
         canonicizeNumberStatically("352668-123456", "352668123456", isaMobileNumber = false);
         */
        // compare "661 is mobile" and "661 is not necessarily mobile"
        canonicizeNumberStatically("661-123456", "352661123456", isaMobileNumber = true);
        canonicizeNumberStatically("661-123456", "661123456", isaMobileNumber = false); // this is a valid international phone number!!
    }

    @Test
    public void testStaticPhoneNumberCanonicizationLuxgsmNumbersOldStyle() {
        // note that 'old style 021' numbers are transformed to 'new style 621' numbers
        canonicizeNumberStatically("+352021-123456", "352621123456", isaMobileNumber = true);
        canonicizeNumberStatically("352021-123456", "352621123456", isaMobileNumber = true);
        canonicizeNumberStatically("021-123456", "352621123456", isaMobileNumber = true);
        canonicizeNumberStatically("+352021-123456", "352621123456", isaMobileNumber = false);
        canonicizeNumberStatically("352021-123456", "352621123456", isaMobileNumber = false);
        // the same as above for voicemail numbers
        /*
         canonicizeNumberStatically("+352028-123456", "352628123456", isaMobileNumber = true);
         canonicizeNumberStatically("352028-123456", "352628123456", isaMobileNumber = true);
         canonicizeNumberStatically("028-123456", "352628123456", isaMobileNumber = true);
         canonicizeNumberStatically("+352028-123456", "352628123456", isaMobileNumber = false);
         canonicizeNumberStatically("352028-123456", "352628123456", isaMobileNumber = false);
         */
        // compare "021 is mobile" and "021 is not necessarily mobile"
        canonicizeNumberStatically("021-123456", "352621123456", isaMobileNumber = true);
        canonicizeNumberStatically("021-123456", null, isaMobileNumber = false); // won't be recognized in case of 'not necessarily mobile'
    }

    @Test
    public void testStaticPhoneNumberCanonicizationLuxgsmNumbersNewStyle() {
        canonicizeNumberStatically("+352621-123456", "352621123456", isaMobileNumber = true);
        canonicizeNumberStatically("352621-123456", "352621123456", isaMobileNumber = true);
        canonicizeNumberStatically("621-123456", "352621123456", isaMobileNumber = true);
        canonicizeNumberStatically("+352621-123456", "352621123456", isaMobileNumber = false);
        canonicizeNumberStatically("352621-123456", "352621123456", isaMobileNumber = false);
        canonicizeNumberStatically("621-123456", "621123456", isaMobileNumber = false); // this is a valid international phone number!!
        // the same as above for voicemail numbers
        /*
         canonicizeNumberStatically("+352628-123456", "352628123456", isaMobileNumber = true);
         canonicizeNumberStatically("352628-123456", "352628123456", isaMobileNumber = true);
         canonicizeNumberStatically("628-123456", "352628123456", isaMobileNumber = true);
         canonicizeNumberStatically("+352628-123456", "352628123456", isaMobileNumber = false);
         canonicizeNumberStatically("352628-123456", "352628123456", isaMobileNumber = false);
         */
        // compare "621 is mobile" and "621 is not necessarily mobile"
        canonicizeNumberStatically("621-123456", "352621123456", isaMobileNumber = true);
        canonicizeNumberStatically("628-123456", "628123456", isaMobileNumber = false); // this is a valid international phone number!!
    }

    @Test
    public void testStaticPhoneNumberCanonicizationTangoNumbersOldStyle() {
        // note that 'old style 091' numbers are transformed to 'new style 691' numbers
        canonicizeNumberStatically("+352091-123456", "352691123456", isaMobileNumber = true);
        canonicizeNumberStatically("352091-123456", "352691123456", isaMobileNumber = true);
        canonicizeNumberStatically("091-123456", "352691123456", isaMobileNumber = true);
        canonicizeNumberStatically("+352091-123456", "352691123456", isaMobileNumber = false);
        canonicizeNumberStatically("352091-123456", "352691123456", isaMobileNumber = false);
        // the same as above for voicemail numbers
        /*
         canonicizeNumberStatically("+352098-123456", "352698123456", isaMobileNumber = true);
         canonicizeNumberStatically("352098-123456", "352698123456", isaMobileNumber = true);
         canonicizeNumberStatically("098-123456", "352698123456", isaMobileNumber = true);
         canonicizeNumberStatically("+352098-123456", "352698123456", isaMobileNumber = false);
         canonicizeNumberStatically("352098-123456", "352698123456", isaMobileNumber = false);
         */
        // compare "691 is mobile" and "691 is not necessarily mobile"
        canonicizeNumberStatically("091-123456", "352691123456", isaMobileNumber = true);
        canonicizeNumberStatically("091-123456", null, isaMobileNumber = false); // won't be recognized in case of 'not necessarily mobile'
    }

    @Test
    public void testStaticPhoneNumberCanonicizationTangoNumbersNewStyle() {
        canonicizeNumberStatically("+352691-123456", "352691123456", isaMobileNumber = true);
        canonicizeNumberStatically("352691-123456", "352691123456", isaMobileNumber = true);
        canonicizeNumberStatically("691-123456", "352691123456", isaMobileNumber = true);
        canonicizeNumberStatically("+352691-123456", "352691123456", isaMobileNumber = false);
        canonicizeNumberStatically("352691-123456", "352691123456", isaMobileNumber = false);
        canonicizeNumberStatically("00352691511466", "352691511466", isaMobileNumber = false);
        canonicizeNumberStatically("00352691511466", "352691511466", isaMobileNumber = true);
        // the same as above for voicemail numbers
        /*
         canonicizeNumberStatically("+352698-123456", "352698123456", isaMobileNumber = true);
         canonicizeNumberStatically("352698-123456", "352698123456", isaMobileNumber = true);
         canonicizeNumberStatically("698-123456", "352698123456", isaMobileNumber = true);
         canonicizeNumberStatically("+352698-123456", "352698123456", isaMobileNumber = false);
         canonicizeNumberStatically("352698-123456", "352698123456", isaMobileNumber = false);
         */
        // compare "691 is mobile" and "691 is not necessarily mobile"
        canonicizeNumberStatically("691-123456", "352691123456", isaMobileNumber = true);
        canonicizeNumberStatically("691-123456", "691123456", isaMobileNumber = false); // this is a valid international phone number!!

    }

    @Test
    public void testPhoneNumberInstantiationBadCases() {
        canonicizeNumberByInstantiationExpectingException("", isaMobileNumber = false);
        canonicizeNumberByInstantiationExpectingException(null, isaMobileNumber = false);
        canonicizeNumberByInstantiationExpectingException("ABC", isaMobileNumber = false);
        canonicizeNumberByInstantiationExpectingException("+352021ABC555", isaMobileNumber = false);
        canonicizeNumberByInstantiationExpectingException("", isaMobileNumber = true);
        canonicizeNumberByInstantiationExpectingException(null, isaMobileNumber = true);
        canonicizeNumberByInstantiationExpectingException("ABC", isaMobileNumber = true);
        canonicizeNumberByInstantiationExpectingException("+352021ABC555", isaMobileNumber = true);
    }

    @Test
    public void testPhoneNumberInstantiationBadLuxoNumbers() {
        canonicizeNumberByInstantiationExpectingException("+352071-123456", isaMobileNumber = true);
        canonicizeNumberByInstantiationExpectingException("352071-123456", isaMobileNumber = true);
        canonicizeNumberByInstantiationExpectingException("071-123456", isaMobileNumber = true);
    }

    @Test
    public void testPhoneNumberInstantiationShortThusBadLuxoNumbers() {
        canonicizeNumberByInstantiationExpectingException("+352091-23456", isaMobileNumber = true);
        canonicizeNumberByInstantiationExpectingException("352091-23456", isaMobileNumber = true);
        canonicizeNumberByInstantiationExpectingException("091-23456", isaMobileNumber = true);
    }

    @Test
    public void testPhoneNumberInstantiationAnyInternationalNumbers() {
        canonicizeNumberByInstantiation("441234-5679-910", DirectDialPrefixId.CC_44, "12345679910", isaMobileNumber = false);
        canonicizeNumberByInstantiation("00441234-5679-910", DirectDialPrefixId.CC_44, "12345679910", isaMobileNumber = false);
        canonicizeNumberByInstantiation("+441234-5679-910", DirectDialPrefixId.CC_44, "12345679910", isaMobileNumber = false);
    }

    @Test
    public void testPhoneNumberInstantiationVoxMobileNumbers() {
        canonicizeNumberByInstantiation("+352061-123456", DirectDialPrefixId.CC_352, "661123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("352061-123456", DirectDialPrefixId.CC_352, "661123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("061-123456", DirectDialPrefixId.CC_352, "661123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("+352061-123456", DirectDialPrefixId.CC_352, "661123456", isaMobileNumber = false);
        canonicizeNumberByInstantiation("352061-123456", DirectDialPrefixId.CC_352, "661123456", isaMobileNumber = false);
        canonicizeNumberByInstantiationExpectingException("061-123456", isaMobileNumber = false); // won't be recognized in case of 'not necessarily mobile'
    }

    @Test
    public void testPhoneNumberInstantiationVoxMobileNumbersNewStyle() {
        canonicizeNumberByInstantiation("+352661-123456", DirectDialPrefixId.CC_352, "661123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("352661-123456", DirectDialPrefixId.CC_352, "661123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("661-123456", DirectDialPrefixId.CC_352, "661123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("+352661-123456", DirectDialPrefixId.CC_352, "661123456", isaMobileNumber = false);
        canonicizeNumberByInstantiation("352661-123456", DirectDialPrefixId.CC_352, "661123456", isaMobileNumber = false);
        canonicizeNumberByInstantiation("661-123456", DirectDialPrefixId.CC_66, "1123456", isaMobileNumber = false); // this is a valid international phone number!!
    }

    @Test
    public void testPhoneNumberInstantiationLuxgsmNumbers() {
        canonicizeNumberByInstantiation("+352021-123456", DirectDialPrefixId.CC_352, "621123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("352021-123456", DirectDialPrefixId.CC_352, "621123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("021-123456", DirectDialPrefixId.CC_352, "621123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("+352021-123456", DirectDialPrefixId.CC_352, "621123456", isaMobileNumber = false);
        canonicizeNumberByInstantiation("352021-123456", DirectDialPrefixId.CC_352, "621123456", isaMobileNumber = false);
        canonicizeNumberByInstantiationExpectingException("021-123456", isaMobileNumber = false); // won't be recognized in case of 'not necessarily mobile'
    }

    @Test
    public void testPhoneNumberInstantiationLuxgsmNumbersNewStyle() {
        canonicizeNumberByInstantiation("+352621-123456", DirectDialPrefixId.CC_352, "621123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("352621-123456", DirectDialPrefixId.CC_352, "621123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("621-123456", DirectDialPrefixId.CC_352, "621123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("+352621-123456", DirectDialPrefixId.CC_352, "621123456", isaMobileNumber = false);
        canonicizeNumberByInstantiation("352621-123456", DirectDialPrefixId.CC_352, "621123456", isaMobileNumber = false);
        canonicizeNumberByInstantiation("621-123456", DirectDialPrefixId.CC_62, "1123456", isaMobileNumber = false); // this is a valid international phone number!!
    }

    @Test
    public void testPhoneNumberInstantiationTangoNumbers() {
        canonicizeNumberByInstantiation("+352091-123456", DirectDialPrefixId.CC_352, "691123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("352091-123456", DirectDialPrefixId.CC_352, "691123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("091-123456", DirectDialPrefixId.CC_352, "691123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("+352091-123456", DirectDialPrefixId.CC_352, "691123456", isaMobileNumber = false);
        canonicizeNumberByInstantiation("352091-123456", DirectDialPrefixId.CC_352, "691123456", isaMobileNumber = false);
        canonicizeNumberByInstantiationExpectingException("091-123456", isaMobileNumber = false); // won't be recognized in case of 'not necessarily mobile'
    }

    @Test
    public void testPhoneNumberInstantiationTangoNumbersNewStyle() {
        canonicizeNumberByInstantiation("+352691-123456", DirectDialPrefixId.CC_352, "691123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("352691-123456", DirectDialPrefixId.CC_352, "691123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("691-123456", DirectDialPrefixId.CC_352, "691123456", isaMobileNumber = true);
        canonicizeNumberByInstantiation("+352691-123456", DirectDialPrefixId.CC_352, "691123456", isaMobileNumber = false);
        canonicizeNumberByInstantiation("352691-123456", DirectDialPrefixId.CC_352, "691123456", isaMobileNumber = false);
        canonicizeNumberByInstantiation("691-123456", DirectDialPrefixId.CC_691, "123456", isaMobileNumber = false); // this is a valid international phone number!!
    }

    @Test
    public void testPhoneNumberInum() {
        String phoneNumberInum = "883510001850559";
        String phoneNumberInumCanonicized = PhoneNumber.canonicizePhoneNumber(phoneNumberInum);
        assertEquals(phoneNumberInumCanonicized, phoneNumberInum);
    }

}
