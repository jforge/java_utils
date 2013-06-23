package tests.core_low

import junit.framework.Assert

import org.junit.Test

import com.mplify.junit.TestStarter
import com.mplify.mutable.MutableBoolean

class TestCaseMutableBoolean extends TestStarter {

    @Test
    void testMutableBoolean() {
        def MY_TRUE = new MutableBoolean(true)
        def MY_FALSE = new MutableBoolean(false)
        Assert.assertTrue("Test 1", MY_TRUE as Boolean)
        Assert.assertFalse("Test 2", MY_FALSE as Boolean)
        Assert.assertTrue("Test 3", MY_TRUE.get())
        Assert.assertFalse("Test 4", MY_FALSE.get())
        Assert.assertTrue("Test 5", MY_TRUE.true)
        Assert.assertFalse("Test 6", MY_FALSE.true)
        Assert.assertFalse("Test 7", MY_TRUE.false)
        Assert.assertTrue("Test 8", MY_FALSE.false)
        Assert.assertTrue("Test 9", MY_TRUE.isTrue())
        Assert.assertFalse("Test 10", MY_FALSE.isTrue())
        Assert.assertFalse("Test 11", MY_TRUE.isFalse())
        Assert.assertTrue("Test 12", MY_FALSE.isFalse())
        Assert.assertTrue("Test 13", MY_TRUE.booleanValue())
        Assert.assertFalse("Test 14", MY_FALSE.booleanValue())

    }

}
