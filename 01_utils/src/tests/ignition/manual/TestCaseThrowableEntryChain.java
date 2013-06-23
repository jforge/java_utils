package tests.ignition.manual;

import org.junit.Test;

import com.mplify.logging.Story;
import com.mplify.logging.ThrowableEntryChain;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Not really a test case, more a test program to see whether writing an
 * exception works.
 * 
 * 2011.10.17 - Created
 ******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseThrowableEntryChain {

    @Test
    public void testStory2() {
        try {
            throw new IllegalStateException("It happened here");
        } catch (Exception exe) {
            ThrowableEntryChain tec = new ThrowableEntryChain(exe);
            Story story = tec.toStory2();
            story.write(System.out);
            System.out.flush();
        }
    }

    @Test
    public void testStory2Multi() {
        try {
            try {
                throw new IllegalStateException("It happened here");
            } catch (Exception exe) {
                throw new IllegalArgumentException("That was bad", exe);
            }
        } catch (Exception exe) {
            ThrowableEntryChain tec = new ThrowableEntryChain(exe);
            Story story = tec.toStory2();
            story.write(System.out);
            System.out.flush();
        }
    }
}
