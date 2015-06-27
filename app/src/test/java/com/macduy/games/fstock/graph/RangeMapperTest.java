package com.macduy.games.fstock.graph;

import com.macduy.games.fstock.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class RangeMapperTest {
    private static final float DELTA = 0.00001f;
    @Mock Range mSource;
    @Mock Range mDestination;

    RangeMapper mMapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mMapper = new RangeMapper(mSource, mDestination);
    }

    @Test
    public void testZeroClamped() {
        // Map [0,10] --> [0,30]
        when(mSource.start()).thenReturn(0f);
        when(mSource.end()).thenReturn(10f);

        when(mDestination.start()).thenReturn(0f);
        when(mDestination.end()).thenReturn(30f);

        assertEquals(0, mMapper.get(0), DELTA);
        assertEquals(15, mMapper.get(5), DELTA);
        assertEquals(30, mMapper.get(10), DELTA);
    }

    @Test
    public void testLinear() {
        // Map [-1,1] -> [2, 8]
        when(mSource.start()).thenReturn(-1f);
        when(mSource.end()).thenReturn(1f);

        when(mDestination.start()).thenReturn(2f);
        when(mDestination.end()).thenReturn(8f);

        assertEquals(5, mMapper.get(0), DELTA);
        assertEquals(6, mMapper.get(0.3333333f), DELTA);
    }

    @Test
    public void testInverted() {
        // Map [0, 1] -> [1, 0]
        when(mSource.start()).thenReturn(0f);
        when(mSource.end()).thenReturn(1f);

        when(mDestination.start()).thenReturn(1f);
        when(mDestination.end()).thenReturn(0f);

        assertEquals(0.5, mMapper.get(0.5f), DELTA);
        assertEquals(0.2, mMapper.get(0.8f), DELTA);
        assertEquals(0.9999, mMapper.get(0.0001f), DELTA);
    }
}