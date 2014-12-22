package com.macduy.games.fstock.graph;

/**
 * Simple default implementation of range, where the range is simply set and the span is kept
 * above a given minimum.
 */
public class MinimumSpanRange extends SimpleRange {
    private final float mMinimumSpan;

    public MinimumSpanRange(float minimumSpan) {
        mMinimumSpan = minimumSpan;
        set(0, mMinimumSpan);
    }

    /**
     * Update range from given unsorted boundary values.
     */
    public void set(float a, float b) {
        float span = b - a;
        if (span > mMinimumSpan || span < -mMinimumSpan) {
            super.set(a, b);
            return;
        }

        float diff;
        if (span >= 0) {
            diff = mMinimumSpan - span;
        } else {
            diff = -mMinimumSpan - span;
        }

        // Expand the range by the difference needed to get to minimum span.
        a -= diff / 2;
        b += diff / 2;

        super.set(a, b);
    }
}
