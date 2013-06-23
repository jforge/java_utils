package com.mplify.logrotation;

import java.io.File;

import ch.qos.logback.core.rolling.TriggeringPolicyBase;

/* 34567890123456789012345678901234567890123456789012345678901234567890123456789
 * *****************************************************************************
 * Copyright (c) 2008, M-PLIFY S.A.
 *                     21, rue Glesener
 *                     L-1631 LUXEMBOURG
 *
 * All rights reserved.
 *******************************************************************************
 *******************************************************************************
 * Triggering Policy based on Flag File
 * 
 * 2011.10.27 - Created
 ******************************************************************************/

public class FlagFileBasedTriggeringPolicy<E> extends TriggeringPolicyBase<E> {

    private File flagFile;
    private String flagFileRaw;
    private Object triggerCheckLock = new Object();

    // IMPORTANT: This field can be updated by multiple threads. It follows that
    // its values may *not* be incremented sequentially. However, we don't care
    // about the actual value of the field except that from time to time the
    // expression (invocationCounter++ & 0xF) == 0xF) should be true.
    private int invocationCounter = 0xF;

    /**
     * Constructor
     */

    public FlagFileBasedTriggeringPolicy() {
        // NOP
    }

    /**
     * Constructor
     */

    public FlagFileBasedTriggeringPolicy(final String flagFileRaw) {
        setFlagFile(flagFileRaw);
    }

    /**
     * Multiple threads may invoke this!
     */

    @Override
    public boolean isTriggeringEvent(final File activeFile, final E event) {
        // for performance reasons, check for changes every 16 invocations
        if (((invocationCounter++) & 0xF) != 0xF) {
            return false;
        }
        // if the flag file exists and can be removed, it is a triggering event
        synchronized (triggerCheckLock) {
            if (flagFile != null && flagFile.exists() && flagFile.isFile() && flagFile.length() == 0) {
                addInfo("Flag file '" + flagFile.getAbsolutePath() + "' exists and is a file of length 0");
                if (flagFile.delete()) {
                    addInfo("Flag file '" + flagFile.getAbsolutePath() + "' could be deleted; this means log rotation");
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    /**
     * Getter for the flag file
     */

    public String getFlagFile() {
        synchronized (triggerCheckLock) {
            return flagFileRaw;
        }
    }

    /**
     * Setter for the flag file. If it is not absolute, assume it's relative to /tmp
     */

    public void setFlagFile(String flagFileRaw) {
        synchronized (triggerCheckLock) {
            this.flagFileRaw = flagFileRaw;
            if (flagFileRaw == null) {
                this.flagFile = null;
            } else {
                this.flagFile = new File(this.flagFileRaw);
                if (!this.flagFile.isAbsolute()) {
                    String tmpDir = System.getProperty("java.io.tmpdir");
                    if (tmpDir == null) {
                        tmpDir = "/tmp";
                    }
                    this.flagFile = new File(tmpDir, this.flagFileRaw);
                }
                assert this.flagFile.isAbsolute();
            }
        }
    }
}