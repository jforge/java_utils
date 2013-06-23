package com.mplify.tools;

import java.util.Date;

/**
 * Helper. The timer starts at creation date and stops at the first "getDuration"
 */

public class Timer {

    private final long start = System.currentTimeMillis();
    private Long stop;

    public int getDuration_ms() {
        if (stop == null) {
            stop = Long.valueOf(System.currentTimeMillis());
        }
        return (int) (stop.longValue() - start);
    }

    public Integer getDurationAsInteger_ms() {
        return Integer.valueOf(getDuration_ms());
    }

    public Date getStartDate() {
        return new Date(start);
    }
}