package com.macduy.games.fstock;

import android.os.SystemClock;

/**
 * Reliable clock implementation.
 */
public class Clock {
    public long getTime() {
        return SystemClock.uptimeMillis();
    }
}
