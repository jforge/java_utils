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
 * Exception thrown when a DOM document or JDOM cannot be reconstructed into 
 * some in-memory structure.
 * 
 * 2003.XX.XX - Created
 ******************************************************************************/

@SuppressWarnings("serial")
public class ConstructionException extends Exception {

	public ConstructionException() {
		super();
	}

	public ConstructionException(String message) {
		super(message);
	}

	public ConstructionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConstructionException(Throwable cause) {
		super(cause);
	}
}
