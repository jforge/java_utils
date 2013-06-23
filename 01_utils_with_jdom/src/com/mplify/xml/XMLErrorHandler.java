package com.mplify.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * An ErrorHandler implementation using in XML DOM/SAX parsing.
 * 
 * If an application does not  register an ErrorHandler, XML parsing errors
 * will go unreported, except that SAXParseExceptions will be thrown for 
 * fatal errors. In order to detect validity errors, an ErrorHandler that
 * does something with error() calls must be registered.
 *
 * 2002.03.28 - Extracted from DealerDeviceC and set up as a top-level class.
 * 2005.06.18 - Somewhat simplified, no longer uses ChangeableBoolean
 ******************************************************************************/

public class XMLErrorHandler implements ErrorHandler {

	private final static String CLASS = XMLErrorHandler.class.getName();

	private final static Logger LOGGER_error = LoggerFactory.getLogger(CLASS + ".error");
	private final static Logger LOGGER_warning = LoggerFactory.getLogger(CLASS + ".warning");
	
	private boolean somethingBadHappend = false;
	
	@Override
    public void error(SAXParseException exe) {		
		LOGGER_error.error("Error while parsing XML", exe);
		somethingBadHappend = true;
	}

	@Override
    public void fatalError(SAXParseException exe) {		
		// no need to print nor to throw, the exception will be thrown by the caller
		somethingBadHappend = true;
	}

	@Override
    public void warning(SAXParseException exe) {		
		LOGGER_warning.warn("Warning while parsing XML", exe);
	}
	
	public boolean isSomethingBadHappened() {
		return somethingBadHappend;
	}
	
}
