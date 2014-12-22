package com.macduy.games.fstock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class GameStateTest {
    private static final float DELTA = 0.001f;

    @Mock StockPrice mStock;

    GameState mState;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mState = new GameState();
    }

    @Test
    public void testMoney() {
        mState.setCurrentMoney(1000f);
        assertEquals(1000f, mState.getCurrentMoney(), DELTA);
    }

    @Test
    public void testBuy() {
        assertEquals(0, mState.getTrades());
        assertFalse(mState.hasTrades());

        mState.setCurrentMoney(1000f);
        when(mStock.getLatest()).thenReturn(500f);
        assertTrue(mState.maybeBuy(mStock));

        assertEquals(mState.getCurrentMoney(), 500f, DELTA);
        assertTrue(mState.hasTrades());
        assertEquals(1, mState.getTrades());
    }

    @Test
    public void testBuy_notEnoughMoney() {
        mState.setCurrentMoney(1999.99f);
        when(mStock.getLatest()).thenReturn(2000f);

        assertFalse(mState.maybeBuy(mStock));

        assertFalse(mState.hasTrades());
        assertEquals(0, mState.getTrades());
    }

    @Test
    public void fail() {
        assertTrue(true);
    }

}