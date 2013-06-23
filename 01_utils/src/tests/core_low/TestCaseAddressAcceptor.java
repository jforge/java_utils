package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;


import com.mplify.junit.TestStarter;
import com.mplify.msgserver.addressing.Tvec;
import com.mplify.tools.AddressAcceptor;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Testing "AddressAcceptor", which accepts strings supposed to be e-mail 
 * addresses
 ******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseAddressAcceptor extends TestStarter {

    @Test
    public void testVariousAddresses() {
        assertFalse("Test 1", AddressAcceptor.acceptAddress(null));
        assertFalse("Test 2", AddressAcceptor.acceptAddress(""));
        assertFalse("Test 3", AddressAcceptor.acceptAddress("."));
        assertTrue("Test 4", AddressAcceptor.acceptAddress("a@b"));
        assertFalse("Test 5", AddressAcceptor.acceptAddress("x"));
        assertFalse("Test 6", AddressAcceptor.acceptAddress("@"));
        assertFalse("Test 7", AddressAcceptor.acceptAddress("@@"));
        assertFalse("Test 8", AddressAcceptor.acceptAddress("@c"));
        assertFalse("Test 9", AddressAcceptor.acceptAddress("d@"));
        assertFalse("Test 10", AddressAcceptor.acceptAddress("a@b@c"));
        assertTrue("Test 11", AddressAcceptor.acceptAddress("a@b.c.d"));
        assertTrue("Test 12", AddressAcceptor.acceptAddress("a@b.c.d."));
        assertFalse("Test 13", AddressAcceptor.acceptAddress("a.b.c.@b.c.d"));
        assertFalse("Test 14", AddressAcceptor.acceptAddress(".a.b.c@b.c.d"));
        assertTrue("Test 15", AddressAcceptor.acceptAddress("a.b.c@b.c.d"));
        assertTrue("Test 16", AddressAcceptor.acceptAddress("a*.b$.c%ALPHA{YES}@b.c.d"));
        assertTrue("Test 17", AddressAcceptor.acceptAddress("a0123456789.b.c@b.c.d"));
        assertFalse("Test 18", AddressAcceptor.acceptAddress("a.b.é@b.c.d"));
        assertFalse("Test 19", AddressAcceptor.acceptAddress("a.b.ké@b.c.d"));
        assertFalse("Test 20", AddressAcceptor.acceptAddress("a.b.kék@b.c.d"));
        assertTrue("Test 21", AddressAcceptor.acceptAddress("someone@at.somewhere.org"));
        assertTrue("Test 22", AddressAcceptor.acceptAddress("someone@at.somewhere.org."));
        assertTrue("Test 23", AddressAcceptor.acceptAddress("someone@1at.somewhere.org"));
        assertTrue("Test 24", AddressAcceptor.acceptAddress("someone@at.1somewhere.org"));
        assertTrue("Test 25", AddressAcceptor.acceptAddress("someone@3446.somewhere1.org"));
        assertTrue("Test 26", AddressAcceptor.acceptAddress("someone@3446.989777661.org"));
        assertFalse("Test 27", AddressAcceptor.acceptAddress("someone@3446.989777661.7org"));
        assertTrue("Test 28", AddressAcceptor.acceptAddress("someone@at.somewhere1234.org"));
        assertTrue("Test 29", AddressAcceptor.acceptAddress("someone@at.someWHERE1234.ORG"));
        assertTrue("Test 30", AddressAcceptor.acceptAddress("someone@at.so-meW--HERE1-234.ORG"));
        assertFalse("Test 31", AddressAcceptor.acceptAddress("someone@at.somew%here1.org"));
        assertFalse("Test 32", AddressAcceptor.acceptAddress("someone@at.somewhere1..org"));
        assertFalse("Test 33", AddressAcceptor.acceptAddress("someone@at.?.org"));
        assertTrue("Test 34", AddressAcceptor.acceptAddress("someone@at.12where.org"));
        assertFalse("Test 35", AddressAcceptor.acceptAddress("someone@at.wh---ere-.org"));
        assertTrue("Test 36", AddressAcceptor.acceptAddress("someone@at.wh---ere.org"));
        assertFalse("Test 37", AddressAcceptor.acceptAddress("email@bureau.?"));
    }

    @Test
    public void testVariousAddressesWithUnderscores() {
        assertTrue("Test U1", AddressAcceptor.acceptAddress("someone@at.somewhere.org", true));
        assertTrue("Test U2", AddressAcceptor.acceptAddress("someone@at.somewhere.org", false));
        assertTrue("Test U3", AddressAcceptor.acceptAddress("nagios@sup_centreon_01.m-plify.net", true));
        assertFalse("Test U4", AddressAcceptor.acceptAddress("nagios@sup_centreon_01.m-plify.net", false));
    }
    
    @Test
    public void testAddressMustBeFullyQualified() {
        try {
            Tvec.handleAddress("Hello", "World", true);
            fail();
        } catch (IllegalArgumentException exe) {
            // OK
        }
    }

    @Test
    public void testCorrectStuff() {
        assertEquals("Earth <Hello@World>", Tvec.handleAddress("Hello@World", "Earth", true).toString());
        assertEquals("Hello@World", Tvec.handleAddress("Hello@World", null, true).toString());
    }

    @Test
    public void testIfNullIsPassedAsValueThenNullIsReturned() {
        assertNull(Tvec.handleAddress(null, null, true));
        assertNull(Tvec.handleAddress("", "Earth", true));
        assertNull(Tvec.handleAddress(null, "Whatever", true));
        assertNull(Tvec.handleAddress(null, null, false));
    }

    @Test
    public void testIfEmptyStringIsPassedAsValueThenNullMayBeReturned() {
        assertNull(Tvec.handleAddress("", null, true));
        try {
            assertNull(Tvec.handleAddress("", null, false));
            fail();
        } catch (IllegalArgumentException exe) {
            // OK
        }
    }

    @Test
    public void testBadSyntax() {
        try {
            Tvec.handleAddress("x@mach.com.", "label", true);
            fail();
        } catch (IllegalArgumentException exe) {
            // OK
        }
    }
}
