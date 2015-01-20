package com.macduy.games.fstock.graph;

/**
 * A range with fixed span. Once the span is given, the range can be defined by setting the start
 * or the end.
 */
public class FixedRange implements Range {
    private final float mSpan;
    private float mStart;

    public FixedRange(float span) {
        mSpan = span;
    }

    public void setStart(float start) {
        mStart = start;
    }

    public void setEnd(float end) {
        setStart(end - mSpan);
    }

    @Override
    public float start() {
        return mStart;
    }

    @Override
    public float end() {
        return mStart + mSpan;
    }
}
