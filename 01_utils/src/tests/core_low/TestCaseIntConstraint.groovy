package tests.core_low;

import com.mplify.junit.TestStarter;
import com.mplify.queryconstraints.IntConstraint
import com.mplify.queryconstraints.NoMatchException
import org.junit.Test;
import static org.junit.Assert.*;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Test the "IntConstraint" class
 *
 * 2011.10.10 - Created
 ******************************************************************************/

class TestCaseIntConstraint extends TestStarter {

    @Test
    void testInteger() {
        assertEquals('TSI1','123*123*',new IntConstraint('  123 ').toTestString())
        assertEquals('TSI2','123*123*',new IntConstraint(' +123 ').toTestString())
        assertEquals('TSI3','-123*-123*',new IntConstraint(' -123 ').toTestString())
        assertEquals('TSI4','0*0*',new IntConstraint('  0 ').toTestString())
        assertEquals('TSI5','12*12*',new IntConstraint('  0012 ').toTestString())
        try {
            new IntConstraint('  8879339218389 ').toTestString()
            fail("Expected NoMatchException because integer too large")
        } catch (NoMatchException exe) {
            // expected
        }
    }
    
    @Test
    void testIntegerWithXSuffix() {
        assertEquals('TSIX1','1230*1239*',new IntConstraint('  123X ').toTestString())
        assertEquals('TSIX2','12300*12399*',new IntConstraint(' +123xx ').toTestString())
        assertEquals('TSIX3','-12399*-12300*',new IntConstraint(' -123xx ').toTestString())
        assertEquals('TSIX4','0*9*',new IntConstraint('  0x ').toTestString())
        assertEquals('TSIX5','12000*12999*',new IntConstraint('  0012xxx ').toTestString())
        try {
            new IntConstraint('  8xxxxxxxxxxxxxxxx ').toTestString()
            fail("Expected NoMatchException because integer too large")
        } catch (NoMatchException exe) {
            // expected
        }
        try {
            new IntConstraint(' x ').toTestString()
            fail("Expected NoMatchException because 'x' is not an integer interval")
        } catch (NoMatchException exe) {
            // expected
        }
    }

    @Test
    void testIntegerWithComparisonOnTheRight() {
        assertEquals('TSRC1.1','123!N',new IntConstraint(' 123 < ').toTestString())
        assertEquals('TSRC1.2','123!N',new IntConstraint(' 123< ').toTestString())
        assertEquals('TSRC1.3','123*N',new IntConstraint(' 123 <= ').toTestString())
        assertEquals('TSRC1.4','123*N',new IntConstraint(' 123<= ').toTestString())
        assertEquals('TSRC1.5','123*N',new IntConstraint(' 123 =< ').toTestString())
        assertEquals('TSRC1.6','123*N',new IntConstraint(' 123=< ').toTestString())
        assertEquals('TSRC1.7','123*N',new IntConstraint(' 123 ≤ ').toTestString())
        assertEquals('TSRC1.8','123*N',new IntConstraint(' 123≤ ').toTestString())
        assertEquals('TSRC1.9','123*N',new IntConstraint(' 123 - ').toTestString())
        assertEquals('TSRC1.10','123*N',new IntConstraint(' 123- ').toTestString())
        assertEquals('TSRC1.11','123*N',new IntConstraint(' 123 .. ').toTestString())
        assertEquals('TSRC1.12','123*N',new IntConstraint(' 123 ... ').toTestString())
        assertEquals('TSRC1.13','123*N',new IntConstraint(' 123.. ').toTestString())
        assertEquals('TSRC1.14','123*N',new IntConstraint(' 123... ').toTestString())

        assertEquals('TSRC2.1','-123!N',new IntConstraint(' -123 < ').toTestString())
        assertEquals('TSRC2.2','-123*N',new IntConstraint(' -123 <= ').toTestString())
        assertEquals('TSRC2.3','-123*N',new IntConstraint(' -123 =< ').toTestString())
        assertEquals('TSRC2.4','-123*N',new IntConstraint(' -123 ≤ ').toTestString())
        assertEquals('TSRC2.5','-123*N',new IntConstraint(' -123 - ').toTestString())
        assertEquals('TSRC2.6','-123*N',new IntConstraint(' -123- ').toTestString())
        assertEquals('TSRC2.7','-123*N',new IntConstraint(' -123.. ').toTestString())
        assertEquals('TSRC2.8','-123*N',new IntConstraint(' -123... ').toTestString())
        
        assertEquals('TSRC3.1','123!N',new IntConstraint(' +123 < ').toTestString())
        assertEquals('TSRC3.2','123*N',new IntConstraint(' +123 <= ').toTestString())
        assertEquals('TSRC3.3','123*N',new IntConstraint(' +123 =< ').toTestString())
        assertEquals('TSRC3.4','123*N',new IntConstraint(' +123 ≤ ').toTestString())
        assertEquals('TSRC3.5','123*N',new IntConstraint(' +123 - ').toTestString())
        assertEquals('TSRC3.6','123*N',new IntConstraint(' +123- ').toTestString())
        assertEquals('TSRC3.7','123*N',new IntConstraint(' +123.. ').toTestString())
        assertEquals('TSRC3.8','123*N',new IntConstraint(' +123... ').toTestString())
        
        assertEquals('TSRC4.1','N123!' ,new IntConstraint(' 123 > ').toTestString())
        assertEquals('TSRC4.1','N123*',new IntConstraint(' 123 >= ').toTestString())
        assertEquals('TSRC4.1','N123*',new IntConstraint(' 123 => ').toTestString())
        assertEquals('TSRC4.1','N123*',new IntConstraint(' 123 ≥ ').toTestString())
        
    }
    
    @Test
    void testIntegerWithComparisonOnTheLeft() {
        assertEquals('TSLC1.1','N123!',new IntConstraint(' < 123 ').toTestString())
        assertEquals('TSLC1.2','N123!',new IntConstraint(' <123 ').toTestString())
        assertEquals('TSLC1.3','N123*',new IntConstraint(' <= 123 ').toTestString())
        assertEquals('TSLC1.4','N123*',new IntConstraint(' <=123 ').toTestString())
        assertEquals('TSLC1.5','N123*',new IntConstraint(' =< 123 ').toTestString())
        assertEquals('TSLC1.6','N123*',new IntConstraint(' =<123 ').toTestString())
        assertEquals('TSLC1.7','N123*',new IntConstraint(' ≤ 123 ').toTestString())
        assertEquals('TSLC1.8','N123*',new IntConstraint(' ≤123 ').toTestString())
        assertEquals('TSLC1.11','N123*',new IntConstraint(' .. 123 ').toTestString())
        assertEquals('TSLC1.12','N123*',new IntConstraint(' ... 123 ').toTestString())
        assertEquals('TSLC1.13','N123*',new IntConstraint(' ..123 ').toTestString())
        assertEquals('TSLC1.14','N123*',new IntConstraint(' ...123 ').toTestString())

        assertEquals('TSLC2.1','N-123!',new IntConstraint(' < -123 ').toTestString())
        assertEquals('TSLC2.2','N-123*',new IntConstraint(' <= -123 ').toTestString())
        assertEquals('TSLC2.3','N-123*',new IntConstraint(' =< -123 ').toTestString())
        assertEquals('TSLC2.4','N-123*',new IntConstraint(' ≤ -123  ').toTestString())
        assertEquals('TSLC2.7','N-123*',new IntConstraint(' .. -123 ').toTestString())
        assertEquals('TSLC2.8','N-123*',new IntConstraint(' ... -123 ').toTestString())

        assertEquals('TSLC2.9','N-123!',new IntConstraint(' <-123 ').toTestString())
        assertEquals('TSLC2.10','N-123*',new IntConstraint(' <=-123 ').toTestString())
        assertEquals('TSLC2.11','N-123*',new IntConstraint(' =<-123 ').toTestString())
        assertEquals('TSLC2.12','N-123*',new IntConstraint(' ≤-123  ').toTestString())
        assertEquals('TSLC2.13','N-123*',new IntConstraint(' ..-123 ').toTestString())
        assertEquals('TSLC2.14','N-123*',new IntConstraint(' ...-123 ').toTestString())

        
        assertEquals('TSLC3.1','N123!',new IntConstraint(' < +123 ').toTestString())
        assertEquals('TSLC3.2','N123*',new IntConstraint(' <= +123 ').toTestString())
        assertEquals('TSLC3.3','N123*',new IntConstraint(' =< +123 ').toTestString())
        assertEquals('TSLC3.4','N123*',new IntConstraint(' ≤ +123  ').toTestString())
        assertEquals('TSLC3.7','N123*',new IntConstraint(' .. +123 ').toTestString())
        assertEquals('TSLC3.8','N123*',new IntConstraint(' ... +123 ').toTestString())

        assertEquals('TSLC3.9','N123!',new IntConstraint(' <+123 ').toTestString())
        assertEquals('TSLC3.10','N123*',new IntConstraint(' <=+123 ').toTestString())
        assertEquals('TSLC3.11','N123*',new IntConstraint(' =<+123 ').toTestString())
        assertEquals('TSLC3.12','N123*',new IntConstraint(' ≤+123  ').toTestString())
        assertEquals('TSLC3.13','N123*',new IntConstraint(' ..+123 ').toTestString())
        assertEquals('TSLC3.14','N123*',new IntConstraint(' ...+123 ').toTestString())

        assertEquals('TSLC1.X1','N123*',new IntConstraint(' - 123 ').toTestString())
        assertEquals('TSLC1.X2','N-123*',new IntConstraint(' - -123 ').toTestString())
        assertEquals('TSLC1.X3','N123*',new IntConstraint(' - +123 ').toTestString())
        assertEquals('TSLC1.X4','N-123*',new IntConstraint(' --123 ').toTestString())
        assertEquals('TSLC1.X4','N123*',new IntConstraint(' -+123 ').toTestString())
    }
    
    @Test
    void testRangeWithoutBrackets() {
        assertEquals('R1.1','123*300*' ,new IntConstraint('   123 ..  300 ').toTestString())
        assertEquals('R1.2','123*300*',new IntConstraint('     300..  123 ').toTestString())
        assertEquals('R1.3','-123*300*',new IntConstraint('  -123..300 ').toTestString())
        assertEquals('R1.4','-300*-123*',new IntConstraint(' -123..-300 ').toTestString())
        assertEquals('R1.5','-300*123*',new IntConstraint('    123 ..-300 ').toTestString())
        assertEquals('R1.6','123*300*',new IntConstraint('    123..+300 ').toTestString())
        assertEquals('R1.7','123*300*',new IntConstraint('   +123 .. +300 ').toTestString())
        assertEquals('R1.8','-123*300*',new IntConstraint('   -123..+300 ').toTestString())

        assertEquals('R2.1','123*300*' ,new IntConstraint('   123 .. 300 ').toTestString())
        assertEquals('R2.2','123*300*' ,new IntConstraint('   123 ... 300 ').toTestString())
        assertEquals('R2.3','-123*300*' ,new IntConstraint('   -123 ... 300 ').toTestString())
        
        assertEquals('R3.1','123*123*' ,new IntConstraint('   123 ... 123 ').toTestString())
        assertEquals('R3.2','123*124*' ,new IntConstraint('   123 ... 124 ').toTestString())
        assertEquals('R3.3','123*124*' ,new IntConstraint('   123 ... 124 ').toTestString())
        
        assertEquals('R4.1','123*123*' ,new IntConstraint('   123 - 123 ').toTestString())
        assertEquals('R4.2','123*124*' ,new IntConstraint('   123 - +124 ').toTestString())
        assertEquals('R4.3','-124*123*' ,new IntConstraint('   123 - -124 ').toTestString())
        assertEquals('R4.4','-123*123*' ,new IntConstraint('   123 --123 ').toTestString())
        assertEquals('R4.5','123*124*' ,new IntConstraint('   123 -+124 ').toTestString())
        
        try {
            assertEquals('R4.1','124*123*' ,new IntConstraint('   123 -124 ').toTestString())
            fail('Expected no match exception')
        }
        catch (NoMatchException exe) {
            // OK
        }
    }

    @Test
    void testRangeWithBrackets() {
        assertEquals('B1.1','123*300*',new IntConstraint(' [ 123 - 300 ]').toTestString())
        assertEquals('B1.2','123*300*',new IntConstraint(' [ 123 .. 300 ]').toTestString())
        assertEquals('B1.3','-123*300*',new IntConstraint(' [ -123 ... 300 ]').toTestString())
        assertEquals('B1.4','-300*-123*',new IntConstraint(' [ -300 - -123 ]').toTestString())
        assertEquals('B1.5','-300*123*',new IntConstraint(' [ -300 .. 123 ]').toTestString())
        assertEquals('B1.6','123*300*',new IntConstraint(' [ 123..300 ]').toTestString())
        assertEquals('B1.7','123*300*',new IntConstraint(' [ 123 .. 300 ]').toTestString())
        assertEquals('B1.8','-123*300*',new IntConstraint(' [ -123 .. 300 ]').toTestString())
        
        assertEquals('B2.1','123!300!',new IntConstraint(' ] 123 - 300 [').toTestString())
        assertEquals('B2.2','123!300!',new IntConstraint(' ] 123 .. 300 [').toTestString())
        assertEquals('B2.3','-123*300!',new IntConstraint(' [ -123 ... 300 [').toTestString())
        assertEquals('B2.4','-300*-123!',new IntConstraint(' [ -300 - -123 [').toTestString())
        assertEquals('B2.5','-300!123*',new IntConstraint(' ] -300 .. 123 ]').toTestString())
        assertEquals('B2.6','123!300*',new IntConstraint(' ] 123..300 ]').toTestString())
        assertEquals('B2.7','123!300*',new IntConstraint(' ] 123 .. 300 ]').toTestString())
        assertEquals('B2.8','-123*300!',new IntConstraint(' [ -123 .. 300 [').toTestString())

        assertEquals('N1','123!123!',new IntConstraint(' ] 123 - 123 [').toTestString())
        assertTrue('N1e',new IntConstraint(' ] 123 - 123 [').isEmpty())
        assertEquals('N2','123!124!',new IntConstraint(' ] 123 - 124 [').toTestString())
        assertTrue('N2e',new IntConstraint(' ] 123 - 124 [').isEmpty())
        assertEquals('N3','123!125!',new IntConstraint(' ] 123 - 125 [').toTestString())
        assertFalse('N3e',new IntConstraint(' ] 123 - 125 [').isEmpty())
        assertEquals('N3f',1,new IntConstraint(' ] 123 - 125 [').size)        
    }
    
    @Test
    void testGenerateSqlFactor() {
        assertEquals('F1','(-123<=F) AND (F<300)',new IntConstraint(' [ -123 .. 300 [').generateSqlFactor('F'))
        assertEquals('F2','FALSE',new IntConstraint(' ] 123 - 123 [').generateSqlFactor('F'))
        assertEquals('F3','123<=F',new IntConstraint(' 123 - ').generateSqlFactor('F'))
        assertEquals('F4','F<=123',new IntConstraint(' ..123').generateSqlFactor('F'))
        assertEquals('F5','F=123',new IntConstraint(' 123 ').generateSqlFactor('F'))
        assertEquals('F6','(120<=F) AND (F<=129)',new IntConstraint(' 12X ').generateSqlFactor('F'))
    }
}
