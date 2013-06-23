package com.mplify.tools;

import java.util.HashMap;
import java.util.Map;

import com.mplify.checkers.Check;
import com.mplify.logging.LogFacilities;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *******************************************************************************
 *******************************************************************************
 * Base class for all PDU elements. This collects common methods for
 * parsing or command creation. You may also want to consider the 'Hex'
 * class in org.apache.commons.codec.*
 *
 * 2002.08.13 - Finished
 * 2005.01.17 - Slight modification with the usage of enumeration types
 * 2005.05.23 - Moved to com.mplify.utils.bytecoding to have these facilities
 *              in 'common code', not only in the 'comm' code subtree
 * 2005.12.03 - Added method to test bits
 * 2006.09.30 - Slight text modifications
 * 2007.05.17 - Slight mods to 'toByteStream()'
 * 2008.05.05 - A few Compiler warnings eliminated
 * 2011.05.11 - Added compareByteArrays()
 * 
 * Bugs: This is not very efficient
 ******************************************************************************/

public class ByteCoding {

//	private final static String CLASS = ByteCoding.class.getName();

	/**
	 * Structures used in fast text<->byte conversions
	 */

	private final static String[] hexLookupByteToString;
	private final static String[] hexLookupNibbleToString;
	private final static Map<String,Integer> hexLookupStringToByte;

	static {
		{
			// do a mapping like array[255] -> "FF", result is uppercase
			hexLookupByteToString = new String[256];
			for (int i = 0; i < 256; i++) {
				if (i < 16) {
					StringBuffer buf = new StringBuffer("0");
					buf.append(Integer.toHexString(i));
					hexLookupByteToString[i] = buf.toString().toUpperCase();
				} else {
					hexLookupByteToString[i] = Integer.toHexString(i).toUpperCase();
				}
			}
		}
		{
			// do mapping like map{"A0"} -> 160, lowercase and uppercase
			hexLookupStringToByte = new HashMap<String,Integer>();
			for (int i = 0; i < 256; i++) {
				hexLookupStringToByte.put(hexLookupByteToString[i].toUpperCase(), new Integer(i));
			}
			for (int i = 0; i < 256; i++) {
				hexLookupStringToByte.put(hexLookupByteToString[i].toLowerCase(), new Integer(i));
			}
		}
		{
			// do a mapping like array[10] -> "A", the result is uppercase
			hexLookupNibbleToString = new String[16];
			for (int i = 0; i < 16; i++) {
				hexLookupNibbleToString[i] = Integer.toHexString(i).toUpperCase();
			}
		}
	}

	/**
	 * Unreachable constructor
	 */

	private ByteCoding() {
		// NOP
	}

	/**
	 * Transform a string of hex-represented bytes ....0AFDE231...) into an array of bytes.
	 * The case of the input string is unimportant, it may even be mixed. A hexStringIn of trimmed length
	 * 0 returns a byte array of length 0 (as expected).
	 * An IllegalArgumentException is thrown if the passed "hex coded string" turns out to actually not be
	 * a "hex coded string". 
	 * Whitespace at the start and the end of the "hex coded string" is trimmed away. 
	 */

	public static byte[] toByteArray(String hexStringIn) {
		if (hexStringIn == null) {
			throw new IllegalArgumentException("The passed 'hex coded string' is (null)");
		}
		//
		// In order to grok mixed case, do 'toUpperCase()':
		//
		String hexString = hexStringIn.toUpperCase().trim();
		int textLength = hexString.length();
		if (textLength % 2 != 0) {
			throw new IllegalArgumentException("The passed 'hex coded string' has an odd length of " + textLength + ", i.e. there is one nibble missing or too many");
		}
		byte[] res = new byte[textLength / 2];
		int indexIntoText = 0;
		for (int i = 0; i < res.length; i++) {
			String hex = hexString.substring(indexIntoText, indexIntoText + 2);
			Integer x = hexLookupStringToByte.get(hex);
			// System.err.println(hex + "->" + x);
			if (x == null) {
				throw new IllegalArgumentException("Could not translate the digraph '" + hex + "' at position " + indexIntoText + " of the passed 'hex coded string'" + LogFacilities.mangleString(hexString));
			}
			res[i] = x.byteValue();
			indexIntoText += 2;
		}
		return res;
	}

	/**
	 * Transform a stream of bytes into a "command string" (e.g. "0FDE445E23")   
	 */

	public static String toHexString(byte[] byteArray) {
	    Check.notNull(byteArray,"byte array");
	    int nibbleCount = byteArray.length * 2;
		return toHexString(byteArray, 0, nibbleCount, false);
	}

	/**
	 * Transform a stream of bytes into a "command string".  We count in nibbles, and may pad with 'F' at the end.
	 */

	public static String toHexString(byte[] byteArray, int startInStream, int nibbleCountIn, boolean reverseNibbles) {
		StringBuffer buf = new StringBuffer();
		int nibbleCount = nibbleCountIn;
		int i = startInStream;
		while (nibbleCount > 1) {
			int val = byteArray[i] & 0xFF;
			if (reverseNibbles) {
				buf.append(hexLookupNibbleToString[val & 0x0F]);
				buf.append(hexLookupNibbleToString[val >> 4]);
			} else {
				buf.append(hexLookupNibbleToString[val >> 4]);
				buf.append(hexLookupNibbleToString[val & 0x0F]);
			}
			i++;
			nibbleCount -= 2;
		}
		if (nibbleCount == 1) {
			int val = byteArray[i] & 0xFF;
			// padding
			if (reverseNibbles) {
				buf.append("F");
				buf.append(hexLookupNibbleToString[val >> 4]);
			} else {
				buf.append(hexLookupNibbleToString[val >> 4]);
				buf.append("F");
			}
		}
		return buf.toString();
	}

	/**
	 * Facility: Promote the byte to integer with proper masking to get rid of sign extension,
	 * yielding a positive int.
	 */

	public static int pi(byte x) {
		return x & 0xFF;
	}

	/**
	 * Transform a byte into a 2-char hex representation with leading zero, the result is appended to 'buf'
	 */

	public static void hexifyByte(byte val, StringBuffer buf) {
		buf.append(hexLookupByteToString[val & 0xFF]);
	}

	/**
	 * Transform a byte into a 2-char hex representation with leading zero, the result being returned
	 */

	public static String hexifyByte(byte val) {
		return hexLookupByteToString[val & 0xFF];
	}

	/**
	 * Transform a nibble into a 1-char hex representation, the result is appended to 'buf'
	 */

	public static void hexifyNibble(byte val, StringBuffer buf) {
		buf.append(hexLookupNibbleToString[val & 0x0F]);
	}

	/**
	 * Transform the lower nibble of 'val' into a 1-char hex representation, the result is appended to 'buf'. 
	 * Additionally, if the value of the lower nibble is above 'limit', an exception is raised. This is
	 * useful if one wants to check whether it's a decimal digit.
	 */

	public static void hexifyNibble(byte val, StringBuffer buf, int limit) {
		int x = val & 0x0F;
		if (x > limit) {
			throw new IllegalArgumentException("The passed value is " + x + " which is above " + limit);
		} else {
			buf.append(hexLookupNibbleToString[x]);
		}
	}

	/**
	 * Transform the numeric value of the passed char into a 2-char hex representation with leading zero
	 */

	protected static void hexify(char ch, StringBuffer buf) {
		hexifyByte((byte) ch, buf);
	}

	/**
	 * Transform the integer into a 2-char hex representation with leading zero. 
	 */

	protected static void hexify(int val, StringBuffer buf) {
		hexify((byte) val, buf);
	}

	protected static String hexify(int val) {
		StringBuffer buf = new StringBuffer();
		hexify((byte) val, buf);
		return buf.toString();
	}

	/**
	 * Helper method to textify a byte into a bit pattern
	 */

	public static String tbp(byte xIn, int numBits, boolean lsbFirst) {
		StringBuffer buf = new StringBuffer();
		byte x = xIn;
		if (lsbFirst) {
			for (int i = 0; i < numBits; i++) {
				if ((x & 0x01) != 0) {
					buf.append("1");
				} else {
					buf.append("0");
				}
				x = (byte) (x >> 1);
			}
		} else {
			for (int i = 0; i < numBits; i++) {
				if ((x & 0x80) != 0) {
					buf.append("1");
				} else {
					buf.append("0");
				}
				x = (byte) (x << 1);
			}
		}
		return buf.toString();
	}

	/**
	 * Encode a value between 0 and 255 into BCD, with Most Significant Nibble first. The
	 * result is appended to 'buf'.
	 */

	public static void encode_BCD_MSN(int x, StringBuffer buf) {
		if (x < 0 || 99 < x) {
			throw new IllegalArgumentException("Cannot BCD-encode " + x);
		}
		if (x < 10) {
			buf.append("0");
		} else {
			buf.append(x / 10);
		}
		buf.append(x % 10);
	}

	/**
	 * Bit testing
	 */

	public static boolean isBitSet(byte x, int bit) {
		switch (bit) {
		case 0:
			return ((x & 0x01) != 0);
		case 1:
			return ((x & 0x02) != 0);
		case 2:
			return ((x & 0x04) != 0);
		case 3:
			return ((x & 0x08) != 0);
		case 4:
			return ((x & 0x10) != 0);
		case 5:
			return ((x & 0x20) != 0);
		case 6:
			return ((x & 0x40) != 0);
		case 7:
			return ((x & 0x80) != 0);
		default:
			throw new IllegalArgumentException("Cannot test bit " + bit + " of an 8-bit byte, dude!");
		}
	}
	
    /**
     * Compare two arrays, return an 'equal' boolean. The arrays may be null, the
     * comparison says 'ok' if both are null at the same time. 
     */

    public static boolean compareByteArrays(byte[] left, byte[] right) {
        if (left == null && right == null) {
            return true;
        }
        if (left == null) {
            return false;
        }
        if (right == null) {
            return false;
        }
        if (left.length != right.length) {
            return false;
        }
        int i;
        for (i = 0; i < left.length && left[i] == right[i]; i++) {
            // NOP
        }
        return i == left.length;
    }
}