package com.macduy.games.fstock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(FstockTestRunner.class)
@Config(emulateSdk = 18)
public class GameStateTest {
    private static final float DELTA = 0.001f;

    @Mock
    StockData mStock;

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

        StockData.Price price = new StockData.Price(0, 500f);

        mState.setCurrentMoney(1000f);
        when(mStock.getLatest()).thenReturn(price);
        assertTrue(mState.maybeBuy(mStock));

        assertEquals(mState.getCurrentMoney(), 500f, DELTA);
        assertTrue(mState.hasTrades());
        assertEquals(1, mState.getTrades());
    }

    @Test
    public void testBuy_notEnoughMoney() {
        StockData.Price price = new StockData.Price(0, 2000f);

        mState.setCurrentMoney(1999.99f);
        when(mStock.getLatest()).thenReturn(price);

        assertFalse(mState.maybeBuy(mStock));

        assertFalse(mState.hasTrades());
        assertEquals(0, mState.getTrades());
    }

}