package com.macduy.games.fstock.graph;

/**
 * Simple implementation of Range, where the values are simply set.
 */
public class SimpleRange implements Range {
    private float mStart;
    private float mEnd;

    public SimpleRange() {}
    public SimpleRange(float start, float end) {
        set(start, end);
    }

    public void set(float start, float end) {
        mStart = start;
        mEnd = end;
    }

    @Override
    public float start() {
        return mStart;
    }

    @Override
    public float end() {
        return mEnd;
    }
}
