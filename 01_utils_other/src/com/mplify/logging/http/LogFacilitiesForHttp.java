package com.mplify.logging.http;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import com.mplify.enums.Verbosity;
import com.mplify.logging.LogFacilities;
import com.mplify.logging.LoglevelAid;
import com.mplify.logging.Story;
import com.mplify.logging.LoglevelAid.Loglevel;
import com.mplify.logging.storyhelpers.AsIsString;
import com.mplify.logging.storyhelpers.Dedent;
import com.mplify.logging.storyhelpers.Doublet;
import com.mplify.logging.storyhelpers.Indent;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 ******************************************************************************
 * Odds and sods used in logging
 *
 * 2004.10.19 - Created due to the Mobilux project. If Http stuff shows up,
 *              the class path needs to include the servlet API; which is
 *              not necessaily recommended.
 * 2008.05.17 - Added httpServletRequestToString()
 * 2011.06.07 - Slight review while it's being used from Groovy
 ******************************************************************************/

public class LogFacilitiesForHttp {

    /**
     * Helper
     */

    private static void addVmSpaceData(Story story) {
        assert story != null;
        long vmmem = Runtime.getRuntime().totalMemory() / 1024;
        long freemem = Runtime.getRuntime().freeMemory() / 1024;
        long usedmem = (vmmem - freemem) / 1024;
        String expl;
        {
            StringBuffer buf = new StringBuffer();
            buf.append(vmmem + " KByte in VM, ");
            buf.append(freemem + " KByte available, free ratio: ");
            buf.append((freemem * 100) / vmmem);
            buf.append("%");
            expl = buf.toString();
        }
        story.add(new Doublet("Memory", expl));
    }

    /**
     * Here we log a HttpServletReques using Log4J at the given level. We return at once if the level is not high enough
     * for logging to 'logger', so no penality is incurred when calling this function with a 'high' level. The title of
     * the bunch of lines logged is passed, too. It may be null.
     */

    public static void logHttpServletRequest(HttpServletRequest req, Logger logger, Loglevel level, String title) {
        logHttpServletRequest(req, logger, level, title, null);
    }

    /**
     * Here we log a HttpServletReques using Log4J at the given level. We return at once if the level is not high enough
     * for logging to 'logger', so no penality is incurred when calling this function with a 'high' level. The title of
     * the bunch of lines logged is passed, too. It may be null. Additionally, a possibly null Exception can be passed.
     */

    public static void logHttpServletRequest(HttpServletRequest req, Logger logger, Loglevel level, String title, Exception exe) {
        if (LoglevelAid.isEnabledFor(logger, level)) {
            Story story = new Story();
            if (title != null) {
                story.add(new AsIsString(title));
            }
            // VM space data can't hurt
            addVmSpaceData(story);
            // Add servlet request data, indented
            story.add(Indent.CI);
            story.add(httpServletRequestToStory(req));
            story.add(Dedent.CI);        
            story.write(logger, level, exe); // exe can be null, no problem...
        }
    }

    /**
     * Transform a HttpServletRequest into a 'story' i.e. a Vector of {String,Indent,Dedent,AsIsString} structures at
     * highest detail level.
     */

    public static Story httpServletRequestToStory(HttpServletRequest req) {
        return httpServletRequestToStory(req, Verbosity.HIGH);
    }

    /**
     * Transform a HttpServletRequest into a beautified 'string'
     */

    public static String httpServletRequestToString(HttpServletRequest req) {
        return httpServletRequestToStory(req).toString();
    }

    /**
     * Transform a HttpServletRequest into a 'story' i.e. a Vector of {String,Indent,Dedent,AsIsString} structures at
     * the passed detail level.
     */

    public static Story httpServletRequestToStory(HttpServletRequest req, Verbosity detail) {
        Story story = new Story();
        if (req.getRemoteUser() != null) {
            story.add(new Doublet("Remote user", req.getRemoteUser()));
        }
        if (req.getRemoteHost() != null) {
            story.add(new Doublet("Remote host", req.getRemoteHost()));
        }
        try {
            InetAddress addr = InetAddress.getByName(req.getRemoteAddr());
            story.add("InetAddress of remote address");
            story.add(Indent.CI);
            story.add(new Doublet("Canonical host name", addr.getCanonicalHostName()));
            story.add(new Doublet("Host address", addr.getHostAddress()));
            story.add(new Doublet("Host name", addr.getHostName()));
            story.add(Dedent.CI);
        } catch (UnknownHostException exe) {
            story.add("...InetAddress of remote address throws UnknownHostException");
        }
        story.add("Accessed " + req.getServerName() + " port=" + req.getServerPort() + " prot=" + req.getProtocol() + " method=" + req.getMethod() + " scheme=" + req.getScheme());
        story.add(new Doublet("Is secure", req.isSecure()));
        story.add(new Doublet("Character encoding", req.getCharacterEncoding()));
        story.add(new Doublet("Content length", req.getContentLength()));
        story.add(new Doublet("Content type", req.getContentType()));
        story.add(new Doublet("Auth type", req.getAuthType()));
        story.add(new Doublet("Context path", req.getContextPath()));
        story.add(new Doublet("Path info", req.getPathInfo()));
        story.add(new Doublet("Path translated", req.getPathTranslated()));
        story.add(new Doublet("Query string", req.getQueryString()));
        story.add(new Doublet("Requested session id", req.getRequestedSessionId()));
        story.add(new Doublet("Request URI", req.getRequestURI()));
        try {
            story.add(new Doublet("Request URL", req.getRequestURL()));
        } catch (NullPointerException exe) {
            story.add("...Getting the Request URL throws NullPointerException");
        }
        story.add(new Doublet("Servlet path", req.getServletPath()));
        {
            boolean create;
            HttpSession session = req.getSession(create = false);
            if (session != null) {
                story.add(new Doublet("Session", session.getId()));
            }
        }
        story.add(new Doublet("Session id from cookie", req.isRequestedSessionIdFromCookie()));
        story.add(new Doublet("Session id from URL", req.isRequestedSessionIdFromURL()));
        story.add(new Doublet("Session id valid", req.isRequestedSessionIdValid()));
        //
        // Headers, only printed if highest detail
        //
        if (detail.compareTo(Verbosity.HIGH) >= 0) {            
            story.add("Headers");
            story.add(Indent.CI);
            Enumeration<String> iter = req.getHeaderNames();
            while (iter.hasMoreElements()) {
                String name = iter.nextElement();
                Enumeration<String> subEnum = req.getHeaders(name);
                while (subEnum.hasMoreElements()) {
                    story.add(new Doublet("'" + name + "'", subEnum.nextElement()));
                }
            }
            story.add(Dedent.CI);
        }
        //
        // Attributes, only printed if highest detail
        //
        if (detail.compareTo(Verbosity.HIGH) >= 0) {
            story.add("Attributes");
            story.add(Indent.CI);
            Enumeration<String> iter = req.getAttributeNames();            
            while (iter.hasMoreElements()) {
                String name = iter.nextElement();
                Object obj = req.getAttribute(name);
                if (obj == null) {
                    story.add(new Doublet("'" + name + "'", null));
                } else {
                    if (obj instanceof String) {
                        story.add(new Doublet("'" + name + "'", obj));
                    } else {
                        story.add(new Doublet("'" + name + "'", obj.getClass().getName()));
                    }
                }
            }
            story.add(Dedent.CI);
        }
        //
        // Locales; only printed if highest detail
        //
        if (detail.compareTo(Verbosity.HIGH) >= 0) {
            Enumeration<Locale> iter = req.getLocales();
            while (iter.hasMoreElements()) {
                Locale locale = iter.nextElement();
                story.add(new Doublet("Locale", locale));
            }
        }
        //
        // Parameters; only printed if medium detail at least
        //
        if (detail.compareTo(Verbosity.MEDIUM) >= 0) {
            story.add(new AsIsString("Parameters"));
            story.add(Indent.CI);
            Enumeration<String> iter = req.getParameterNames();
            while (iter.hasMoreElements()) {
                String name = iter.nextElement();
                String[] values = req.getParameterValues(name);
                if (values == null) {
                    // can't happen, but be paranoid
                    story.add(new Doublet("'" + name + "'", "*** DOES NOT EXIST ***"));
                } else if (values.length == 1) {
                    story.add(new Doublet("'" + name + "'", "'" + LogFacilities.mangleString(values[0]) + "'"));
                } else {
                    // multiple values
                    story.add(new Doublet("'" + name + "'", values.length + " values"));
                    story.add(Indent.CI);
                    for (int i = 0; i < values.length; i++) {
                        story.add(new AsIsString("'" + LogFacilities.mangleString(values[i]) + "'"));
                    }
                    story.add(Dedent.CI);
                }
            }
            story.add(Dedent.CI);
        }
        //
        // Cookies; only printed if highest detail at least
        //
        if (detail.compareTo(Verbosity.HIGH) >= 0) {
            Cookie[] cookies = req.getCookies();
            if (cookies != null && cookies.length > 0) {
                for (int i = 0; i < cookies.length; i++) {
                    story.add(new AsIsString("Cookie " + i));
                    story.add(Indent.CI);
                    story.add(new Doublet("Comment", cookies[i].getComment()));
                    story.add(new Doublet("Domain", cookies[i].getDomain()));
                    story.add(new Doublet("Name", cookies[i].getName()));
                    story.add(new Doublet("Path", cookies[i].getPath()));
                    story.add(new Doublet("Value", cookies[i].getValue()));
                    story.add(new Doublet("Secure", cookies[i].getSecure()));
                    story.add(new Doublet("Max age", cookies[i].getMaxAge()));
                    story.add(new Doublet("Version", cookies[i].getVersion()));
                    story.add(Dedent.CI);
                }
            }
        }
        return story;
    }

    public static String getRequestParameterTrace(HttpServletRequest request) {
        StringBuffer trace = new StringBuffer();
        trace.append("Request trace\n");
        Enumeration<String> parametersEnum = request.getParameterNames();
        String s = null;
        while (parametersEnum.hasMoreElements()) {
            s = parametersEnum.nextElement();
            trace.append(s.toString());
            trace.append(":" + request.getParameter(s.toString()));
            trace.append("\n");
        }
        return trace.toString();
    }

    public static String getSessionParameterTrace(HttpSession session) {
        StringBuffer trace = new StringBuffer();
        trace.append("Session trace\n");
        Enumeration<String> iter = session.getAttributeNames();
        while (iter.hasMoreElements()) {
            String currentElement = iter.nextElement();
            trace.append(currentElement);
            trace.append(":" + session.getAttribute(currentElement));
            trace.append("\n");
        }
        return trace.toString();
    }
}
