package com.macduy.games.fstock.graph;

import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * Maps a given source range to a destination range, using a given interpolator.
 * Currently only the default, linear interpolator is supported.
 *
 * TODO: offer protection from NaN and divisions by 0.
 */
public class RangeMapper {
    private final Interpolator mInterpolator;
    private final Range mSource;
    private final Range mDestination;

    public RangeMapper(Range source, Range destination) {
        mSource = source;
        mDestination = destination;
        mInterpolator = new LinearInterpolator();
    }

    public float get(float x) {
        float f = (x - mSource.start()) / (mSource.end() - mSource.start());
        f = mInterpolator.getInterpolation(f);
        return (mDestination.end() - mDestination.start()) * f + mDestination.start();
    }

    @Override
    public String toString() {
        return String.format("RangeMapper: %s -> %s", mSource.toString(), mDestination.toString());
    }
}
