package com.mplify.xml;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Exception thrown by XMLParsing if a document could not be properly
 * parse using the DOM parser
 *
 * 2004.11.22 - Created
 ******************************************************************************/

@SuppressWarnings("serial")
public class XMLParsingException extends Exception {

	public XMLParsingException() {
		super();
	}

	public XMLParsingException(String message) {
		super(message);
	}

	public XMLParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public XMLParsingException(Throwable cause) {
		super(cause);
	}
}
