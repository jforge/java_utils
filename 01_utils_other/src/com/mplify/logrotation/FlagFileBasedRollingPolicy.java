package com.mplify.logrotation;

import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.spi.ContextAwareBase;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2011, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Rolling a logfile based on a flag file, in conjunction with the Red Hat
 * "logrotated" which does all the actual work. Basically, this 
 * class does nothing at all.  
 * 
 * 2011.10.27 - Created
 ******************************************************************************/

public class FlagFileBasedRollingPolicy extends ContextAwareBase implements RollingPolicy {
    
    private FileAppender<?> parent;
    private boolean started;

    /**
     * Return the compression mode, which is always "none"
     */

    @Override
    public CompressionMode getCompressionMode() {
        return CompressionMode.NONE;
    }

    /**
     * Has it been started?
     */

    @Override
    public boolean isStarted() {
        return started;
    }

    /**
     * Start it
     */

    @Override
    public void start() {
        started = true;
    }

    /**
     * Stop it
     */

    @Override
    public void stop() {
        started = false;
    }

    /**
     * Set the parent FileAppender
     */

    @Override
    public void setParent(FileAppender appender) {
        this.parent = appender;
    }

    /**
     * Rolls over log files according to implementation policy.
     * In this case, this just means closing the output file, and opening it again.
     * But that is completely done by "RollingFileAppender.rollover()", so
     * we do not need to do anything at all!
     */

    @Override
    public void rollover() {
        assert parent!=null;
        addInfo("Rolling over file " + parent.getFile());
    }

    /**
     * Get the name of the active log file.
     * Always returns the name currently configured in the parent FileAppender!
     */

    @Override
    public String getActiveFileName() {
        assert parent!=null;
        return parent.rawFileProperty();
    }
}