package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;


import com.mplify.checkers._check;
import com.mplify.junit.TestStarter;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Testing functionality of _check
 ******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseCheck extends TestStarter {

//    private final static String CLASS = TestCaseCheck.class.getName();

    @Test
    public void testCheckIsTrue() {
        try {
            _check.isTrue(false, "This is a test with static text");
            fail("Should not be here");
        } catch (Exception exe) {
            assertEquals("This is a test with static text", exe.getMessage());
        }
        try {
            _check.isTrue(false, "This is a test with %s text", "variant");
            fail("Should not be here");
        } catch (Exception exe) {
            assertEquals("This is a test with variant text", exe.getMessage());
        }
        try {
            _check.isTrue(false, "This is a test with %s text", "variant");
            fail("Should not be here");
        } catch (Exception exe) {
            assertEquals("This is a test with variant text", exe.getMessage());
        }
        try {
            _check.isTrue(false, "This is a test with %s text and an %d", "variant", Integer.valueOf(12));
            fail("Should not be here");
        } catch (Exception exe) {
            assertEquals("This is a test with variant text and an 12", exe.getMessage());
        }
    }

}
