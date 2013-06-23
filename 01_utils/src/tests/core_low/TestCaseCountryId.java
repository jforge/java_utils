package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Iterator;

import org.junit.Test;


import com.mplify.countries.CountryId;
import com.mplify.junit.TestStarter;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Testing the CountryId class.
 * 
 * 2005.04.26 - Created
 * 2009.01.07 - Added testing of canonical names
 * 2010.10.10 - Simpler main() added. The new main() uses annotations.
 *******************************************************************************/

//@SuppressWarnings("static-method")
public class TestCaseCountryId extends TestStarter {

    @Test
    public void testStandardList() {
        boolean luxembourgFound = false;
        boolean undefinedFound = false;
        boolean nowhereFound = false;
        for (Iterator<CountryId> iter = CountryId.LIST_WITH_UNDEFINED.iterator(); iter.hasNext();) {
            CountryId cid = iter.next();
            luxembourgFound |= (cid.equals(CountryId.LUXEMBOURG));
            undefinedFound |= (cid.equals(CountryId._UNDEFINED));
            nowhereFound |= (cid.equals(CountryId._NOWHERE));
        }
        assertTrue("Standard list: Luxembourg should be in list", luxembourgFound);
        assertTrue("Standard list: Undefined should be in list", undefinedFound);
        assertFalse("Standard list: Nowhere should not be in list", nowhereFound);
    }

    @Test
    public void testListWithoutUndefined() {
        boolean luxembourgFound = false;
        boolean undefinedFound = false;
        boolean nowhereFound = false;
        for (Iterator<CountryId> iter = CountryId.LIST_WITHOUT_UNDEFINED.iterator(); iter.hasNext();) {
            CountryId cid = iter.next();
            luxembourgFound |= (cid.equals(CountryId.LUXEMBOURG));
            undefinedFound |= (cid.equals(CountryId._UNDEFINED));
            nowhereFound |= (cid.equals(CountryId._NOWHERE));
        }
        assertTrue("List without undefined: Luxembourg should be in list", luxembourgFound);
        assertFalse("List without undefined: Undefined should not be in list", undefinedFound);
        assertFalse("List without undefined: Nowhere should not be in list", nowhereFound);
    }

    @Test
    public void testListWithUndefinedAndNowhere() {
        boolean luxembourgFound = false;
        boolean undefinedFound = false;
        boolean nowhereFound = false;
        for (Iterator<CountryId> iter = CountryId.LIST_WITH_UNDEFINED_AND_NOWHERE.iterator(); iter.hasNext();) {
            CountryId cid = iter.next();
            luxembourgFound |= (cid.equals(CountryId.LUXEMBOURG));
            undefinedFound |= (cid.equals(CountryId._UNDEFINED));
            nowhereFound |= (cid.equals(CountryId._NOWHERE));
        }
        assertTrue("List complete: Luxembourg should be in list", luxembourgFound);
        assertTrue("List complete: Undefined should be in list", undefinedFound);
        assertTrue("List complete: Nowhere should be in list", nowhereFound);
    }

    @Test
    public void testObtain() {
        for (CountryId cid : CountryId.LIST_WITH_UNDEFINED_AND_NOWHERE) {
            int value = cid.getValue();
            CountryId back = CountryId.obtain(value);
            assertEquals("Problem with " + cid, cid, back);
        }
        try {
            CountryId.obtain(2000);
            fail("Obtaining 2000 should throw Exception");
        } catch (IllegalArgumentException exe) {
            // as expected
        }
    }

    @Test
    public void testIsValid() {
        for (CountryId cid : CountryId.LIST_WITH_UNDEFINED_AND_NOWHERE) {
            assertTrue("Problem with " + cid, CountryId.isValid(cid.getValue()));
        }
        assertFalse(CountryId.isValid(new Integer(2000)));
    }

    @Test
    public void testObtainByCanonicalName() {
        for (CountryId cid : CountryId.LIST_WITHOUT_UNDEFINED) {
            String canoName = cid.getCanonicalName();
            CountryId back = CountryId.obtainFromCanonicalName(canoName, true);
            assertEquals("Problem with " + cid, cid, back);
        }
        try {
            CountryId.obtain(2000);
            fail("Obtaining 2000 should throw Exception");
        } catch (IllegalArgumentException exe) {
            // as expected
        }
    }
}
