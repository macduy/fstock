package com.macduy.games.fstock.graph;

import com.macduy.games.fstock.FstockTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(FstockTestRunner.class)
@Config(emulateSdk = 18)
public class MinimumSpanRangeTest {
    private static final float DELTA = 0.00001f;
    MinimumSpanRange mRange;

    @Test
    public void testNoMinimumSpan() {
        mRange = new MinimumSpanRange(0);
        mRange.set(5, 5);
        assertEquals(5, mRange.start(), DELTA);
        assertEquals(5, mRange.end(), DELTA);
    }

    @Test
    public void testMinimumSpanSet() {
        mRange = new MinimumSpanRange(50);

        mRange.set(5,20);
        assertEquals(50, mRange.end() - mRange.start(), DELTA);

        mRange.set(5, 100);
        assertEquals(5, mRange.start(), DELTA);
        assertEquals(100, mRange.end(), DELTA);
    }

    @Test
    public void testNoRange() {
        mRange = new MinimumSpanRange(50);
        mRange.set(0, 0);

        assertEquals(50, mRange.end() - mRange.start(), DELTA);
    }

    @Test
    public void testInversedSpan() {
        mRange = new MinimumSpanRange(50);
        mRange.set(100,98);
        assertEquals(50, mRange.start() - mRange.end(), DELTA);

        mRange.set(100, 0);
        assertEquals(100, mRange.start(), DELTA);
        assertEquals(0, mRange.end(), DELTA);
    }
}