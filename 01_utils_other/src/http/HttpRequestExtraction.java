package http;

import javax.servlet.http.HttpServletRequest;

import com.mplify.logging.Story;
import com.mplify.mutable.MutableBoolean;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Helpers to extract data from HTTP requests
 * 
 ******************************************************************************/

public class HttpRequestExtraction {

	/**
	 * Helper function used by subclasses
	 * Interpret String 'x' as a int; return (null) if an error occurred
	 * with a suitable error message appended to 'logVec' and the 'error' boolean set 
	 * to 'true'     
	 */
	
	public static Integer interpretAsInt(String x, Story story, MutableBoolean error) {
		try {
			int out = Integer.parseInt(x);
			return new Integer(out);
		} catch (NumberFormatException exe) {
			story.add("Error: Could not interpret '" + x + "' as integer");
			error.set(true);
			return null;
		}
	}

	/**
	 * A helper function to extract a value from a request.
	 */
	
	public static String extractOneValue(HttpServletRequest request, String name, Story story, MutableBoolean warningOccurred, MutableBoolean errorOccurred) {
		String[] array = request.getParameterValues(name);
		if (array == null) {
			story.add("Warning: No values for parameter '" + name + "'");
			warningOccurred.set(true);
			return null;
		}
		if (array.length != 1) {
			story.add("Warning: Not 1 but " + array.length + " values for parameter '" + name + "', using the first one");
			warningOccurred.set(true);
		}
		return array[0];
	}

	/**
	 * Helper function used by subclasses
	 * Interpret String 'x' as a long; return (null) if an error occurred
	 * with a suitable error message appended to 'logVec' and the 'error' boolean set
	 * to 'true'
	 */
	
	public static Long interpretAsLong(String x, Story story, MutableBoolean error) {
		try {
			long out = Long.parseLong(x);
			return new Long(out);
		} catch (NumberFormatException exe) {
			story.add("Error: Could not interpret '" + x + "' as a long\n");
			error.set(true);
			return null;
		}
	}

}
