package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mplify.junit.TestStarter;
import com.mplify.logging.LogFacilitiesForThrowables;
import com.mplify.names.DottedAbstractName;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Testing the "dotted abstract name" which is used in naming.
 ******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseDottedAbstractName extends TestStarter {

    private final static String CLASS = TestCaseDateParser.class.getName();

    static class MyDAN extends DottedAbstractName {

        public MyDAN(String name) {
            super(name);
        }

        public MyDAN(String... children) {
            super(children);
        }

        public MyDAN(MyDAN parent, String... children) {
            super(parent, children);
        }

        public MyDAN(MyDAN parent, MyDAN firstChild, String... children) {
            super(parent, firstChild, children);
        }

        public MyDAN(MyDAN parent, MyDAN firstChild, MyDAN secondChild, String... children) {
            super(parent, firstChild, secondChild, children);
        }
    }

    @Test
    public void testConstructorTakingOneStringOnValidStrings() {
        {
            MyDAN x = new MyDAN("alpha");
            assertEquals("alpha", x.toString());
        }
        {
            MyDAN x = new MyDAN("alpha.beta");
            assertEquals("alpha.beta", x.toString());
        }
        {
            MyDAN x = new MyDAN("ALPHA.beta");
            assertEquals("alpha.beta", x.toString());
        }
        {
            MyDAN x = new MyDAN("ALPHA.BETA");
            assertEquals("alpha.beta", x.toString());
        }
        {
            MyDAN x = new MyDAN("   ALPHA.BETA  ");
            assertEquals("alpha.beta", x.toString());
        }
        {
            MyDAN x = new MyDAN("   ALPHA. X . BETA  ");
            assertEquals("alpha.x.beta", x.toString());
        }
        {
            MyDAN x = new MyDAN("mu.(kappa).fi");
            assertEquals("mu.(kappa).fi", x.toString());
        }
        {
            MyDAN x = new MyDAN("   mu   .   (kappa)   .   fi");
            assertEquals("mu.(kappa).fi", x.toString());
        }
        {
            MyDAN x = new MyDAN("   mu      (kappa)   .   fi");
            assertEquals("mu      (kappa).fi", x.toString());
        }
        {
            MyDAN x = new MyDAN("   mu      (kappa)      fi  ");
            assertEquals("mu      (kappa)      fi", x.toString());
        }

    }

    @Test
    public void testConstructorTakingManyStringsOnValidStrings() {
        {
            MyDAN x = new MyDAN("alpha", "beta");
            assertEquals("alpha.beta", x.toString());
        }
        {
            MyDAN x = new MyDAN(" alpha ", " beta ");
            assertEquals("alpha.beta", x.toString());
        }
        {
            MyDAN x = new MyDAN("alpha", null, "beta", "DELTA");
            assertEquals("alpha.beta.delta", x.toString());
        }
        {
            MyDAN x = new MyDAN("alpha", "", "beta", "DELTA");
            assertEquals("alpha.beta.delta", x.toString());
        }
        {
            MyDAN x = new MyDAN("alpha", "", null, "", "DELTA");
            assertEquals("alpha.delta", x.toString());
        }
        {
            MyDAN x = new MyDAN("alpha", "", null, "", "DELTA");
            assertEquals("alpha.delta", x.toString());
        }
        {
            MyDAN x = new MyDAN("alpha", "beta.gamma", "DELTA.mu");
            assertEquals("alpha.beta.gamma.delta.mu", x.toString());
        }
    }

    @Test
    public void testConstructorTakingMyDan() {
        {
            MyDAN x = new MyDAN(new MyDAN("beta"), "alpha");
            assertEquals("beta.alpha", x.toString());
        }
        {
            MyDAN x = new MyDAN(new MyDAN("beta"), new MyDAN("gamma"), "alpha");
            assertEquals("beta.gamma.alpha", x.toString());
        }
        {
            MyDAN x = new MyDAN(new MyDAN("beta"), new MyDAN("gamma"), new MyDAN("mu"), "alpha");
            assertEquals("beta.gamma.mu.alpha", x.toString());
        }
    }

    @Test
    public void testConstructorOnInvalidStrings() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testConstructorOnInvalidStrings");
        try {
            MyDAN x = new MyDAN("");
            fail("Exception expected for " + x);
        } catch (Exception exe) {
            logger.info(LogFacilitiesForThrowables.throwableToSimpleMultilineStory("OK", exe).toString());
        }
        try {
            MyDAN x = new MyDAN(".");
            fail("Exception expected for " + x);
        } catch (Exception exe) {
            logger.info(LogFacilitiesForThrowables.throwableToSimpleMultilineStory("OK", exe).toString());
        }
        try {
            MyDAN x = new MyDAN(".alpha");
            fail("Exception expected for " + x);
        } catch (Exception exe) {
            logger.info(LogFacilitiesForThrowables.throwableToSimpleMultilineStory("OK", exe).toString());
        }
        try {
            MyDAN x = new MyDAN("alpha.");
            fail("Exception expected for " + x);
        } catch (Exception exe) {
            logger.info(LogFacilitiesForThrowables.throwableToSimpleMultilineStory("OK", exe).toString());
        }
        try {
            MyDAN x = new MyDAN(".alpha.");
            fail("Exception expected for " + x);
        } catch (Exception exe) {
            logger.info(LogFacilitiesForThrowables.throwableToSimpleMultilineStory("OK", exe).toString());
        }
        try {
            MyDAN x = new MyDAN(" .alpha. ");
            fail("Exception expected for " + x);
        } catch (Exception exe) {
            logger.info(LogFacilitiesForThrowables.throwableToSimpleMultilineStory("OK", exe).toString());
        }
        try {
            MyDAN x = new MyDAN("alpha..beta");
            fail("Exception expected for " + x);
        } catch (Exception exe) {
            logger.info(LogFacilitiesForThrowables.throwableToSimpleMultilineStory("OK", exe).toString());
        }
        try {
            MyDAN x = new MyDAN();
            fail("Exception expected for " + x);
        } catch (Exception exe) {
            logger.info(LogFacilitiesForThrowables.throwableToSimpleMultilineStory("OK", exe).toString());
        }
        try {
            MyDAN x = new MyDAN("", "", "");
            fail("Exception expected for " + x);
        } catch (Exception exe) {
            logger.info(LogFacilitiesForThrowables.throwableToSimpleMultilineStory("OK", exe).toString());
        }
        try {
            MyDAN x = new MyDAN((String) null, (String) null, (String) null);
            fail("Exception expected for " + x);
        } catch (Exception exe) {
            logger.info(LogFacilitiesForThrowables.throwableToSimpleMultilineStory("OK", exe).toString());
        }
        try {
            MyDAN x = new MyDAN("alpha", "beta", "gamma..delta");
            fail("Exception expected for " + x);
        } catch (Exception exe) {
            logger.info(LogFacilitiesForThrowables.throwableToSimpleMultilineStory("OK", exe).toString());
        }
        try {
            MyDAN x = new MyDAN("alpha", "beta", ".delta");
            fail("Exception expected for " + x);
        } catch (Exception exe) {
            logger.info(LogFacilitiesForThrowables.throwableToSimpleMultilineStory("OK", exe).toString());
        }
    }
}
