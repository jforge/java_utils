package com.mplify.logic

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertNull
import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail

import java.util.Map;

import org.jdom.Element
import org.junit.Test
import org.slf4j.Logger;

import com.mplify.checkers.CheckFailedException;
import com.mplify.checkers._check;
import com.mplify.enums.Troolean;
import com.mplify.id.MessageId
import com.mplify.junit.*
import com.mplify.logging.LogFacilities;
import com.mplify.logging.LogFacilitiesForThrowables;
import com.mplify.logging.LoglevelAid.Loglevel;
import com.mplify.logging.Story
import com.mplify.logging.storyhelpers.Indent;
import com.mplify.logic.EvalTrace.Mode;
import com.mplify.properties.PropertyName;
import com.mplify.resources.ResourceHelpers
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2013, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Testing the various possibilities
 * 
 * 2013.01.29 - Done
 ******************************************************************************/
 
class TestCaseLogicExpr extends TestStarter {
    
    private final static String CLASS = TestCaseLogicExpr.class.getName()
    
    /**
     * A map used in all tests
     */
    
    private static final Map DEFAULT_STUFF = [
        (new PropertyName('true_value')) : Boolean.TRUE,
        (new PropertyName('false_value')) : Boolean.FALSE,
        (new PropertyName('true_string')) : 'true',
        (new PropertyName('false_string')) : 'false',
        (new PropertyName('empty_string')) : '',
        (new PropertyName('star_string')) : '*',
        (new PropertyName('null_value')) : null,
        (new PropertyName('troolean_true_value')) : Troolean.TRUE,
        (new PropertyName('troolean_false_value')) : Troolean.FALSE,
        (new PropertyName('troolean_mu_value')) : Troolean.MU,
        (new PropertyName('some_integer')) : 112,
        (new PropertyName('some_id_888')) : new MessageId(888),
        (new PropertyName('match_empty')) : '',
        (new PropertyName('match_whitespace')) : '     ',
        (new PropertyName('match_word')) : 'zugh7ZGNJ099',
        (new PropertyName('match_inside')) : '~~~INSIDE~~~',
        (new PropertyName('membership_string')) : 'hello'
    ]
    
    /**
     * The default handling loop
     * Give it a list of lists [ "Expression-as-XML-String", Expected-Result-As-Troolean ]
     */
    
    private static void logicExprTestLoop(List tests, Logger logger) {
        assert tests != null
        assert logger != null
        for (def test : tests) {
            Element elem = XMLParsing.xmlStringToJdomDocument(test[0]).rootElement
            Troolean expectedResult = test[1]
            for (Mode mode : [ Mode.VERIFICATION, Mode.EVALUATION ]) {
                //
                // Announce what's up
                //
                logger.info("Running test with mode = ${mode}, expected result = ${expectedResult}")
                logger.info("....Expression: '${test[0]}'")
                //
                // Run the test, catching EvalException, which indicates something is wrong with the expression
                // Any other Exception is left uncaught and will fail the test
                //
                EvalTrace evalTrace = new EvalTrace(mode, Loglevel.DEBUG)                
                try {
                    //
                    // 1) Call eval()
                    //
                    boolean result = Evaluator.eval(elem, 'TOP', DEFAULT_STUFF, evalTrace)
                    //
                    // In Evaluation mode,
                    //   - errors should not be observed (EvalException should happen instead)
                    //   - warnings can be observed
                    //   - the result should be as expected in any case
                    //
                    // In Verification mode,
                    //   - errors should only be observed <=> the expected result is MU
                    //  
                    //    - warnings should not be observed (ok, they might be observed, but in this test, no)
                    //    - don't care about the results
                    //
                    if (mode == Mode.EVALUATION) {
                        assertFalse("In mode ${mode}, all errors should yield ${EvalException.class.getName()}",evalTrace.errorsOccurred)
                        assertEquals("In mode ${mode}, did not find expected result", expectedResult.booleanValue(), result)
                    }
                    else {
                        assert mode == Mode.VERIFICATION
                        if (evalTrace.errorsOccurred) {
                            assertEquals("In mode ${mode}, an error (signalled by a flag) <=> the expected result is MU", expectedResult, Troolean.MU)
                        }
                        else {                        
                            assertEquals("In mode ${mode}, did not find expected result", expectedResult.booleanValue(), result)
                        }
                    }
                }
                catch (EvalException exe) {
                    //                    
                    // Check that we expected the failure
                    //  
                    evalTrace.registerError(exe) // this means the exe will be printed in the finally.
                    if (mode == Mode.EVALUATION) {
                        assertEquals("In mode ${mode}, an error (signalled by an exception) <=> the expected result is MU", expectedResult, Troolean.MU)                        
                    }
                    else {
                        assert mode == Mode.VERIFICATION
                        fail("There should not be an ${EvalException.class.getName()} in mode ${mode}")
                    }                    
                }
                finally {                    
                    // Dump the contents of the "trace" 
                    Story story = new Story()
                    story.add("Result for: '${test[0]}'")
                    story.add(Indent.CI)
                    story.add("Errors: ${evalTrace.errorCount}, Warnings: ${evalTrace.warningCount}, Mode: ${evalTrace.mode}")
                    story.add(Indent.CI)
                    story.add(evalTrace.story)
                    if (evalTrace.errorsOccurred) {
                        story.write(logger, Loglevel.ERROR)
                    }   
                    else if (evalTrace.warningsOccurred) {
                        story.write(logger, Loglevel.WARN)
                    }                 
                    else {
                        story.write(logger, Loglevel.INFO)
                    }
                }
            }
        }
    }
    
    /**
     * Tests
     */
    
    @Test
    void testLogicExprTruthValue() {
        Logger logger = LoggerFactory.getLogger(CLASS + '.testLogicExprTruthValue')
        def tests =  [
            [ '<true/>', Troolean.TRUE ],
            [ '<false/>', Troolean.FALSE ],
            [ '<true attribute="true_value" />', Troolean.TRUE ],
            [ '<true attribute="false_value" />', Troolean.FALSE ],
            [ '<true attribute="true_string" />', Troolean.TRUE ],
            [ '<true attribute="false_string" />', Troolean.FALSE ],
            [ '<true attribute="null_value" />', Troolean.MU ],
            [ '<true attribute="empty_string" />', Troolean.MU ],
            [ '<true attribute="star_string" />', Troolean.MU ],
            [ '<true attribute="troolean_true_value" />', Troolean.TRUE ],
            [ '<true attribute="troolean_false_value" />', Troolean.FALSE ],
            [ '<true attribute="troolean_mu_value" />', Troolean.FALSE ],
            [ '<false attribute="true_value" />', Troolean.FALSE ],
            [ '<false attribute="false_value" />', Troolean.TRUE ],
            [ '<false attribute="true_string" />', Troolean.FALSE ],
            [ '<false attribute="false_string" />', Troolean.TRUE ],
            [ '<false attribute="null_value" />', Troolean.MU ],
            [ '<false attribute="empty_string" />', Troolean.MU ],
            [ '<false attribute="star_string" />', Troolean.MU ],
            [ '<false attribute="troolean_true_value" />', Troolean.FALSE ],
            [ '<false attribute="troolean_false_value" />', Troolean.TRUE ],
            [ '<false attribute="troolean_mu_value" />', Troolean.FALSE ] ]
        logicExprTestLoop(tests, logger)
    }

    @Test
    void testLogicExprCasingIsIrrelevant() {
        Logger logger = LoggerFactory.getLogger(CLASS + '.testLogicExprCasingIsIrrelevant')
        def tests =  [
            [ '<true/>', Troolean.TRUE ],
            [ '<false/>', Troolean.FALSE ],
            [ '<trUE/>', Troolean.TRUE ],
            [ '<FAlse/>', Troolean.FALSE ],
            [ '<true attribute="true_value" />', Troolean.TRUE ],
            [ '<TRue attribute="true_VALUE" />', Troolean.TRUE ],
            [ '<true attribute="TRUE_vAlUe" />', Troolean.TRUE ],
            [ '<trUE attribute="truE_Value" />', Troolean.TRUE ] ]
        logicExprTestLoop(tests, logger)
    }
    
    @Test
    void testLogicExprSetUnset() {
        Logger logger = LoggerFactory.getLogger(CLASS + '.testLogicExprSetUnset')
        def tests =  [
            [ '<is_set />', Troolean.MU ],
            [ '<is_set attribute="true_value" />', Troolean.TRUE ],
            [ '<is_set attribute="moo_value" />', Troolean.FALSE ],
            [ '<is_set attribute="null_value" />', Troolean.TRUE ],
            [ '<is_unset />', Troolean.MU ],
            [ '<is_unset attribute="true_value" />', Troolean.FALSE ],
            [ '<is_unset attribute="moo_value" />', Troolean.TRUE ],
            [ '<is_unset attribute="null_value" />', Troolean.FALSE ],
            [ '<is_set attribute="true_value" null_means_unset="true" />', Troolean.TRUE ],
            [ '<is_set attribute="true_value" null_means_unset="false" />', Troolean.TRUE ],
            [ '<is_set attribute="moo_value" null_means_unset="true" />', Troolean.FALSE ],
            [ '<is_set attribute="moo_value" null_means_unset="false" />', Troolean.FALSE ],
            [ '<is_set attribute="null_value" null_means_unset="false" />', Troolean.TRUE ],
            [ '<is_set attribute="null_value" null_means_unset="true" />', Troolean.FALSE ],
            [ '<is_unset attribute="null_value" null_means_unset="false" />', Troolean.FALSE ],
            [ '<is_unset attribute="null_value" null_means_unset="true" />', Troolean.TRUE ] ]
        logicExprTestLoop(tests, logger)
    }
    
    @Test
    void testLogicExprMatch() {
        Logger logger = LoggerFactory.getLogger(CLASS + '.testLogicExprMatch')
        def tests = [
            [ '<match />', Troolean.MU ],
            [ '<match attribute="match_empty" />', Troolean.MU ],
            [ '<match pattern=".*" />', Troolean.MU ],
            [ '<match pattern=".*" attribute="null_value" />', Troolean.MU ],
            [ '<match pattern=".*" attribute="some_integer" />', Troolean.TRUE ],
            [ '<match pattern="[0-9]+" attribute="some_integer" />', Troolean.TRUE ],
            [ '<match pattern="[1-9][0-9]+" attribute="some_integer" />', Troolean.TRUE ],
            [ '<match pattern=".*" attribute="no such stuff" />', Troolean.MU ],
            [ '<match pattern=".*" attribute="match_empty" matchmode="MATCH" />', Troolean.TRUE ],
            [ '<match pattern=".*" attribute="match_empty" matchmode="x" />', Troolean.MU ],
            [ '<match pattern=".*" attribute="match_word" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="^$" attribute="match_empty" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="^$" attribute="match_word" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern="\\w*" attribute="match_word" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="\\w*" attribute="match_empty" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="\\w+" attribute="match_empty" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern="^\\w*$" attribute="match_word" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="^\\w*$" attribute="match_empty" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="^\\w+$" attribute="match_empty" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern="^\\w+$" attribute="match_word" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="\\s*" attribute="match_whitespace" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="^\\s*$" attribute="match_whitespace" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="^\\s+$" attribute="match_whitespace" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="^\\s+$" attribute="match_empty" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern="INSIDE" attribute="match_inside" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern="INSIDE" attribute="match_inside" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern="INSIDE" attribute="match_inside" />', Troolean.TRUE ],
            [ '<match pattern=".*INSIDE" attribute="match_inside" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern=".*INSIDE" attribute="match_inside" matchmode="find" />', Troolean.TRUE ],
            [ '<match pattern=".*?INSIDE" attribute="match_inside" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern=".*?INSIDE.*" attribute="match_inside" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="^~*?INSIDE" attribute="match_inside" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern="^~*?INSIDE" attribute="match_inside" />', Troolean.TRUE ],
            [ '<match pattern="^~*?INSIDE.*" attribute="match_inside" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="^~*INSIDE.*" attribute="match_inside" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="^~+INSIDE.*" attribute="match_inside" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="~+INSIDE~+$" attribute="match_inside" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="^~+INSIDE~+$" attribute="match_inside" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="~+INSIDE~+" attribute="match_inside" matchmode="match" />', Troolean.TRUE ],
            [ '<match pattern="~INSIDE~" attribute="match_inside" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern="~INSIDE~" attribute="match_inside" matchmode="find" />', Troolean.TRUE ],
            [ '<match pattern="INSIDE$" attribute="match_inside" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern="INSIDE$" attribute="match_inside" matchmode="find" />', Troolean.FALSE ],
            [ '<match pattern="INSIDE$" attribute="match_inside" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern="INSIDE~+$" attribute="match_inside" matchmode="match" />', Troolean.FALSE ],
            [ '<match pattern="INSIDE~+$" attribute="match_inside" matchmode="find" />', Troolean.TRUE ],
            [ '<match pattern="INSIDE~+$" attribute="match_inside" />', Troolean.TRUE ],
            [ '<match pattern="inside~+$" attribute="match_inside" />', Troolean.FALSE ],
            [ '<match pattern="inside~+$" attribute="match_inside" prepromode="namify" />', Troolean.TRUE ],
            [ '<match pattern="inside~+$" attribute="match_inside" prepromode="lowercase" />', Troolean.TRUE ],
            [ '<match pattern="inside~+$" attribute="match_inside" prepromode="nop" />', Troolean.FALSE ],
            [ '<match pattern="inside~+$" attribute="match_inside" prepromode="trim" />', Troolean.FALSE ]
            ]
        logicExprTestLoop(tests, logger)
    }

    @Test
    void testLogicExprStage() {
        Logger logger = LoggerFactory.getLogger(CLASS + '.testLogicExprStage')
        def tests = [
            [ '<stage />', Troolean.MU ],
            [ '<stage value="" />', Troolean.FALSE ],
            [ '<stage value="testcase" />', Troolean.TRUE ],
            [ '<stage value="testCASE" />', Troolean.TRUE ],
            [ '<stage value="TESTCASE" />', Troolean.TRUE ],
            [ '<stage value="\'\',\'x\'" />', Troolean.FALSE ],
            [ '<stage value="\'\',\'testcase\'" />', Troolean.FALSE ],
            [ '<stage value="prod, test, foo, test, testcase" />', Troolean.TRUE ]
        ]
        logicExprTestLoop(tests, logger)
    }
    
    @Test
    void testLogicExprMemberOfSet() {
        Logger logger = LoggerFactory.getLogger(CLASS + '.testLogicExprMemberOfSet')
        def tests = [
            [ '<member_of_set />', Troolean.MU ],
            [ '<member_of_set attribute="hurr" />', Troolean.MU ],
            [ '<member_of_set attribute="null_value" />', Troolean.MU ],
            [ '<member_of_set attribute="membership_string" />', Troolean.FALSE ],
            [ '<member_of_set attribute="membership_string" ><member>alpha</member><member>beta</member></member_of_set>', Troolean.FALSE ],
            [ '<member_of_set attribute="membership_string" ><member>alpha</member><member></member></member_of_set>', Troolean.FALSE ],
            [ '<member_of_set attribute="membership_string" ><member>hello</member><member></member></member_of_set>', Troolean.TRUE ],
            [ '<member_of_set attribute="membership_string" ><member>hello</member><member>beta</member><member>beta</member></member_of_set>', Troolean.TRUE ],
            [ '<member_of_set attribute="membership_string" value="alpha, beta"/>', Troolean.FALSE ],
            [ '<member_of_set attribute="membership_string" value="alpha, beta, hello, true"/>', Troolean.TRUE ],
            [ '<member_of_set attribute="membership_string" value="alpha, beta, HELLO, true"/>', Troolean.FALSE ],
            [ '<member_of_set attribute="membership_string" value="alpha, true"><member>beta</member></member_of_set>', Troolean.FALSE ],
            [ '<member_of_set attribute="membership_string" value="alpha, true"><member>hello</member></member_of_set>', Troolean.TRUE ],
            [ '<member_of_set attribute="membership_string" value="alpha, true"><member>HELLO</member></member_of_set>', Troolean.FALSE ],
            [ '<member_of_set attribute="membership_string" value="alpha, true"><member>HELLO</member><a></a></member_of_set>', Troolean.FALSE ],
            [ '<member_of_set attribute="membership_string" value="alpha, true"><member>hello</member><a></a></member_of_set>', Troolean.TRUE ],
            
        ]
        logicExprTestLoop(tests, logger)
    }
    
    @Test
    void testLogicExprMemberOfIdSet() {
        Logger logger = LoggerFactory.getLogger(CLASS + '.testLogicExprMemberOfIdSet')
        def tests = [
            [ '<member_of_id_set />', Troolean.MU ],
            [ '<member_of_id_set attribute="hurr" />', Troolean.MU ],
            [ '<member_of_id_set attribute="hurr" value="12,13" />', Troolean.MU ],
            [ '<member_of_id_set attribute="null_value" value="12,13" />', Troolean.MU ],
            [ '<member_of_id_set attribute="membership_string" value="12,13" />', Troolean.MU ],
            [ '<member_of_id_set attribute="some_integer" value="12,13" />', Troolean.MU ],
            [ '<member_of_id_set attribute="troolean_mu_value" value="12,13" />', Troolean.MU ],
            [ '<member_of_id_set attribute="null_value" value="12,13" />', Troolean.MU ],
            [ '<member_of_id_set attribute="some_id_888" value="12,13" />', Troolean.FALSE ],
            [ '<member_of_id_set attribute="some_id_888" value="12,13,888" />', Troolean.TRUE ],            
            [ '<member_of_id_set attribute="some_id_888" value="12,888"><member>7788</member></member_of_id_set>', Troolean.TRUE ],
            [ '<member_of_id_set attribute="some_id_888"><member>100</member><member>200</member><member>888</member></member_of_id_set>', Troolean.TRUE ],
            
        ]
        logicExprTestLoop(tests, logger)
    }

    @Test
    void testLogicExprEquals() {
        Logger logger = LoggerFactory.getLogger(CLASS + '.testLogicExprEquals')
        def tests = [
            [ '<equals />', Troolean.MU ],
            [ '<equals attribute="??" value="false" />', Troolean.MU ],
            [ '<equals attribute="null_value" value="" />', Troolean.MU ],

            [ '<equals attribute="true_string" value="true" />', Troolean.TRUE ],
            [ '<equals attribute="true_string" value="false" />', Troolean.FALSE ],
            [ '<equals attribute="true_string" value="TRUE" />', Troolean.FALSE ],
            
            [ '<equals attribute="true_value" value="true" />', Troolean.TRUE ],
            [ '<equals attribute="false_value" value="false" />', Troolean.TRUE ],

            [ '<equals attribute="true_value" value="TRUE" />', Troolean.TRUE ],
            [ '<equals attribute="false_value" value="FALSE" />', Troolean.TRUE ],

            [ '<equals attribute="true_value" value="YES" />', Troolean.TRUE ],
            [ '<equals attribute="false_value" value="NO" />', Troolean.TRUE ],

            [ '<equals attribute="troolean_true_value" value="true" />', Troolean.TRUE ],
            [ '<equals attribute="troolean_false_value" value="false" />', Troolean.TRUE ],            
            [ '<equals attribute="troolean_mu_value" value="MU" />', Troolean.TRUE ],
            
            [ '<equals attribute="troolean_true_value" value="mu" />', Troolean.FALSE ],
            [ '<equals attribute="troolean_false_value" value="mu" />', Troolean.FALSE ],
            [ '<equals attribute="troolean_mu_value" value="true" />', Troolean.FALSE ],

            [ '<equals attribute="empty_string" value="" />', Troolean.TRUE ],
                        
            [ '<equals attribute="some_id_888" value="888" />', Troolean.TRUE ],
            [ '<equals attribute="some_id_888" value="666" />', Troolean.FALSE ],
            [ '<equals attribute="some_id_888" value="0" />', Troolean.FALSE ],
        ]
        logicExprTestLoop(tests, logger)
    }

    @Test
    void testLogicExprEqualsWithMod() {
        Logger logger = LoggerFactory.getLogger(CLASS + '.testLogicExprEqualsWithMod')
        def tests = [
            [ '<equals attribute="true_string" value="true" />', Troolean.TRUE ],
            [ '<equals attribute="true_string" value="TRUE" />', Troolean.FALSE ],
            [ '<equals attribute="true_string" value=" true " />', Troolean.FALSE ],
            [ '<equals attribute="true_string" value=" true " prepromode="trim" />', Troolean.TRUE ],
            [ '<equals attribute="true_string" value=" true " prepromode="namify" />', Troolean.TRUE ],
            [ '<equals attribute="true_string" value=" true " prepromode="nop" />', Troolean.FALSE ],
            [ '<equals attribute="true_string" value=" true " prepromode="lowercase" />', Troolean.FALSE ],
            [ '<equals attribute="true_string" value="TRUE" prepromode="trim" />', Troolean.FALSE ],
            [ '<equals attribute="true_string" value="TRUE" prepromode="namify" />', Troolean.TRUE ],
            [ '<equals attribute="true_string" value="TRUE" prepromode="nop" />', Troolean.FALSE ],
            [ '<equals attribute="true_string" value="TRUE" prepromode="lowercase" />', Troolean.TRUE ],
            [ '<equals attribute="true_string" value=" TRUE " prepromode="trim" />', Troolean.FALSE ],
            [ '<equals attribute="true_string" value=" TRUE " prepromode="namify" />', Troolean.TRUE ],
            [ '<equals attribute="true_string" value=" TRUE " prepromode="nop" />', Troolean.FALSE ],
            [ '<equals attribute="true_string" value=" TRUE " prepromode="lowercase" />', Troolean.FALSE ]
        ]
        logicExprTestLoop(tests, logger)
    }

    @Test
    void testLogicExprOp() {
        Logger logger = LoggerFactory.getLogger(CLASS + '.testLogicExprOp')
        def tests =  [ [ '<or></or>', Troolean.FALSE ],
                       [ '<and></and>', Troolean.TRUE ],
                       [ '<and name="top"><true/><false/><true/></and>', Troolean.FALSE ],
                       [ '<or name="top"><true/><false/><true/></or>', Troolean.TRUE ],
                       [ '<not name="top"><true/></not>', Troolean.FALSE ],
                       [ '<not name="top"><false/></not>', Troolean.TRUE ],
                       [ '<not></not>', Troolean.MU ],
                       [ '<not><true/><false/></not>', Troolean.MU ],
                       [ '<xor></xor>', Troolean.MU ],
                       [ '<xor><true/></xor>', Troolean.MU ],
                       [ '<xor><true/><true/></xor>', Troolean.FALSE ],
                       [ '<xor><true/><true/><true/></xor>', Troolean.TRUE ],
                       [ '<impl></impl>', Troolean.MU ],
                       [ '<impl><true /></impl>', Troolean.MU ],
                       [ '<impl><true /><true /></impl>', Troolean.TRUE ],
                       [ '<impl><true /><true /><true /></impl>', Troolean.MU ],
                       [ '<impl><true /><false /></impl>', Troolean.FALSE ],
                       [ '<impl><false /><false /></impl>', Troolean.TRUE ],
                       [ '<impl><false /><true /></impl>', Troolean.TRUE ],
                       [ '<xor> <or> <true/> <not> <false/> </not> </or> <and> <or> <and> <or> <true/><true/><true/> </or> <or> <true/> <not> <true/> </not> </or> </and> </or> </and> </xor>', Troolean.FALSE ] 
                       ]        
        logicExprTestLoop(tests, logger)
    }

}
