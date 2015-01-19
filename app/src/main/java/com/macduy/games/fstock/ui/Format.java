package com.macduy.games.fstock.ui;

/**
 * Convenience class for formatting stuff.
 */
public class Format {
    public static String monetary(float value) {
        return String.format("%.2f", value);
    }

    /** Format time as MM:SS */
    public static String minuteSeconds(long timeMs) {
        int timeS = (int) timeMs / 1000;
        return String.format("%01d:%02d", timeS / 60, timeS % 60);
    }
}
