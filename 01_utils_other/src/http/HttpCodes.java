package http;

import java.net.HttpURLConnection;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * A simple class exporting text for the codes exported by HttpURLConnection
 *
 * 2001.10.25 - Created
 * 2001.11.16 - Added fudge code for Quios
 * 2003.10.03 - Added the 'isOk()' method for easy checking. Fudge code for
 *              Quios is gone. VERSION string added.
 *              ....there should be a textification somewhere in the servlets?          
 ******************************************************************************/

public class HttpCodes {
	
	/**
	 * Is this the "ok" code
	 */
	
	public static boolean isOk(int x) {
		return x == HttpURLConnection.HTTP_OK;
	}
	
	/**
	 * Transform a HTTP response to a string. Never throws, but may say 'unknown response X' as return value.
	 */

	public static String httpStatusToString(int x) {
		switch (x) {
			case HttpURLConnection.HTTP_OK :
				return "HTTP_OK";
			case HttpURLConnection.HTTP_CREATED :
				return "HTTP_CREATED";
			case HttpURLConnection.HTTP_ACCEPTED :
				return "HTTP_ACCEPTED";
			case HttpURLConnection.HTTP_NOT_AUTHORITATIVE :
				return "HTTP_NOT_AUTHORITATIVE";
			case HttpURLConnection.HTTP_NO_CONTENT :
				return "HTTP_NO_CONTENT";
			case HttpURLConnection.HTTP_RESET :
				return "HTTP_RESET";
			case HttpURLConnection.HTTP_PARTIAL :
				return "HTTP_PARTIAL";
			case HttpURLConnection.HTTP_MULT_CHOICE :
				return "HTTP_MULT_CHOICE";
			case HttpURLConnection.HTTP_MOVED_PERM :
				return "HTTP_MOVED_PERM";
			case HttpURLConnection.HTTP_MOVED_TEMP :
				return "HTTP_MOVED_TEMP";
			case HttpURLConnection.HTTP_SEE_OTHER :
				return "HTTP_SEE_OTHER";
			case HttpURLConnection.HTTP_NOT_MODIFIED :
				return "HTTP_NOT_MODIFIED";
			case HttpURLConnection.HTTP_USE_PROXY :
				return "HTTP_USE_PROXY";
			case HttpURLConnection.HTTP_BAD_REQUEST :
				return "HTTP_BAD_REQUEST";
			case HttpURLConnection.HTTP_UNAUTHORIZED :
				return "HTTP_UNAUTHORIZED";
			case HttpURLConnection.HTTP_PAYMENT_REQUIRED :
				return "HTTP_PAYMENT_REQUIRED";
			case HttpURLConnection.HTTP_FORBIDDEN :
				return "HTTP_FORBIDDEN";
			case HttpURLConnection.HTTP_NOT_FOUND :
				return "HTTP_NOT_FOUND";
			case HttpURLConnection.HTTP_BAD_METHOD :
				return "HTTP_BAD_METHOD";
			case HttpURLConnection.HTTP_NOT_ACCEPTABLE :
				return "HTTP_NOT_ACCEPTABLE";
			case HttpURLConnection.HTTP_PROXY_AUTH :
				return "HTTP_PROXY_AUTH";
			case HttpURLConnection.HTTP_CLIENT_TIMEOUT :
				return "HTTP_CLIENT_TIMEOUT";
			case HttpURLConnection.HTTP_CONFLICT :
				return "HTTP_CONFLICT";
			case HttpURLConnection.HTTP_GONE :
				return "HTTP_GONE";
			case HttpURLConnection.HTTP_LENGTH_REQUIRED :
				return "HTTP_LENGTH_REQUIRED";
			case HttpURLConnection.HTTP_PRECON_FAILED :
				return "HTTP_PRECON_FAILED";
			case HttpURLConnection.HTTP_ENTITY_TOO_LARGE :
				return "HTTP_ENTITY_TOO_LARGE";
			case HttpURLConnection.HTTP_REQ_TOO_LONG :
				return "HTTP_REQ_TOO_LONG";
			case HttpURLConnection.HTTP_UNSUPPORTED_TYPE :
				return "HTTP_UNSUPPORTED_TYPE";
			case HttpURLConnection.HTTP_BAD_GATEWAY :
				return "HTTP_BAD_GATEWAY";
			case HttpURLConnection.HTTP_UNAVAILABLE :
				return "HTTP_UNAVAILABLE";
			case HttpURLConnection.HTTP_GATEWAY_TIMEOUT :
				return "HTTP_GATEWAY_TIMEOUT";
			case HttpURLConnection.HTTP_VERSION :
				return "HTTP_VERSION";
			/* stuff deprecated in 1.3:
			case HttpURLConnection.HTTP_SERVER_ERROR :
				return "HTTP_SERVER_ERROR";
			*/			
			case HttpURLConnection.HTTP_INTERNAL_ERROR:
				// only for JDK > 1.2 
				return "HTTP_INTERNAL_ERROR";
			case HttpURLConnection.HTTP_NOT_IMPLEMENTED:
				// only for JDK > 1.2 
				return "HTTP_NOT_IMPLEMENTED";
			default :
				// we don't know what this is. Maybe an extension?
				return extendedHttpStatusToString(x);
		}
	}
	
	/**
	 * If we have any 'homegrown' extensions, we would add them here.
	 */
	
	private static String extendedHttpStatusToString(int x) {
		switch (x) {
			default :
				return "<unknown code " + x + ">";
		}
	}
}