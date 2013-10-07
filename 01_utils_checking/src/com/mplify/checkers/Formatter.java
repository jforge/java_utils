package com.mplify.checkers;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2010, M-PLIFY S.A.
 *                     68, avenue de la Liberté
 *                     L-1930 Luxembourg
 *
 * 2013-01: Released under the MIT License (http://opensource.org/licenses/MIT) 
 *******************************************************************************
 *******************************************************************************
 * Formatting of messages on behalf of "Check"
 * 
 * TODO: Extend "formatForMeLow" so that it accepts SLF4J parameters '{}'
 ******************************************************************************/

class Formatter {


    /**
     * Create a string to "save your ass". formatForMe() should be called rarely, so this is called rarely,
     * no need to be fast
     */
    
    private static String buildSaveMyAssFormatStr(Object... args) {
        assert args != null;
        StringBuilder buf = new StringBuilder("Default error message (" + args.length + " arguments)");
        for (int i = 0; i < args.length; i++) {
            buf.append(" '%s'");
        }        
        return buf.toString();
    }
    
    /**
     * Format using a printf-like formatter. The "formatStrIn" can be null, meaning "use a default".
     * See http://docs.oracle.com/javase/7/docs/api/java/util/Formatter.html for formatting via Formattter
     * See http://slf4j.org/faq.html#string_contents for formatting via SLF4J style
     */

    private static String formatForMeLow(String formatStrIn, Object... args) {
        assert args != null;
        StringBuilder res = new StringBuilder();
        String formatStr = (formatStrIn != null ? formatStrIn : buildSaveMyAssFormatStr(args));        
        try (java.util.Formatter formatter = new java.util.Formatter(res)) {
            formatter.format(formatStr, args).toString();
        }
        catch (Exception exe) {
            String msg = "Exception occurred during formatting of log message: ";
            msg += exe.getClass().getName();
            if (exe.getMessage() != null) {
                msg += ": ";
                msg += exe.getMessage().trim();
            }
            System.err.println(msg);
        }
        
        return res.toString();
    }
    
    /**
     * Helper to format a "txt" (formatStrIn) with "objects".
     */

    public static String formatForMe(String formatStrIn, Object... argsIn) {
        //
        // In a particularly rare case of a call from Groovy, "args" can be null!
        //
        Object[] args = (argsIn != null ? argsIn : new Object[]{});
        //
        // Now format. Problems may occur - in particular, args[] may be too short for the format specs.
        // A (null) args[i] yields "null". For objects and the %s format specifier, Java invokes .toString()
        // on the object (which could throw).
        //
        String output = formatForMeLow(formatStrIn, args);
        //
        // If the returned value is "null", assume "formatStrIn" was bad and call again with
        // "null", thus choosing a default formatting string. 
        //
        if (output == null) {
            output = formatForMeLow(null, args);
        }
        //
        // If that still doesn't work, forget about the formatting business
        //
        if (output == null) {
            output = "Could not properly format a string (formatting string was '" + formatStrIn + "')";
        }
        assert output!=null;
        return output;
    }
}
