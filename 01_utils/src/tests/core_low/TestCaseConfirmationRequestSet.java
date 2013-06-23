package tests.core_low;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jdom.Element;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mplify.junit.TestStarter;
import com.mplify.msgserver.enums.ConfirmationRequestSet;
import com.mplify.msgserver.enums.ConfirmationRequestSet.EndDeviceAck;
import com.mplify.msgserver.enums.ConfirmationRequestSet.IntermediateAck;
import com.mplify.msgserver.enums.ConfirmationRequestSet.ManualOrUserAck;
import com.mplify.msgserver.enums.ConfirmationRequestSet.StoreAndFwdSystemAck;
import com.mplify.xml.XMLParsingException;

//@SuppressWarnings("static-method")
public class TestCaseConfirmationRequestSet extends TestStarter {

    private final static String CLASS = TestCaseConfirmationRequestSet.class.getName();
    
    @Test
    public void testAll() throws XMLParsingException {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testAll");
        for (IntermediateAck ia : IntermediateAck.values()) {
            for (StoreAndFwdSystemAck ssa : StoreAndFwdSystemAck.values()) {
                for (EndDeviceAck eda : EndDeviceAck.values()) {
                    for (ManualOrUserAck mua : ManualOrUserAck.values()) {
                        ConfirmationRequestSet crs = new ConfirmationRequestSet(ia, ssa, eda, mua);                        
                        logger.info("Testing with " + crs);
                        for (boolean elideDefaults : new boolean[]{ false, true }) {
                            for (boolean returnEmptyIfFullyDefault : new boolean[]{ false, true }) {
                                //
                                // Make an XML string
                                //
                                String xml = crs.xmlize(elideDefaults, returnEmptyIfFullyDefault);
                                logger.info("   XML: " + xml);
                                //
                                // Extract from XML string
                                //
                                ConfirmationRequestSet crsBack = new ConfirmationRequestSet(xml);
                                //
                                // Check
                                //
                                assertEquals(crs,crsBack);                                
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Test
    public void testDefault() {        
        assertEquals(ConfirmationRequestSet.DEFAULT, new ConfirmationRequestSet(IntermediateAck.Default, StoreAndFwdSystemAck.Default, EndDeviceAck.Default, ManualOrUserAck.Default));
        boolean elideDefaults, returnEmptyIfFullyDefault;
        assertEquals("", ConfirmationRequestSet.DEFAULT.xmlize(elideDefaults = true, returnEmptyIfFullyDefault = true));
        assertEquals("<conf_req_set />", ConfirmationRequestSet.DEFAULT.xmlize(elideDefaults = true, returnEmptyIfFullyDefault = false));
        assertEquals("<conf_req_set intermediate_acks=\"default\" store_and_fwd_system_acks=\"default\" end_device_acks=\"default\" manual_or_user_acks=\"default\" />", ConfirmationRequestSet.DEFAULT.xmlize(elideDefaults = false, returnEmptyIfFullyDefault = false));
        assertTrue(ConfirmationRequestSet.DEFAULT.isDefault());
    }

    @Test
    public void testInjection() {
        Logger logger = LoggerFactory.getLogger(CLASS + ".testInjection");        
        for (IntermediateAck ia : IntermediateAck.values()) {
            for (StoreAndFwdSystemAck ssa : StoreAndFwdSystemAck.values()) {
                for (EndDeviceAck eda : EndDeviceAck.values()) {
                    for (ManualOrUserAck mua : ManualOrUserAck.values()) {
                        for (boolean elideDefaults : new boolean[]{ false, true }) {
                            ConfirmationRequestSet crs = new ConfirmationRequestSet(ia, ssa, eda, mua);                        
                            logger.info("Testing with " + crs);                            
                            //
                            // Create JDOM element
                            //
                            Element element = new Element("myelement");
                            crs.injectJdom(element, elideDefaults);
                            //
                            // Extract from JDOM element
                            //
                            ConfirmationRequestSet crsBack = new ConfirmationRequestSet(element);
                            //
                            // Check
                            //
                            assertEquals(crs,crsBack);  
                        }
                    }
                }
            }
        }
    }
}
