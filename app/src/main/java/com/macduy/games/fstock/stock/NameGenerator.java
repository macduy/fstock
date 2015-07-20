package com.macduy.games.fstock.stock;

import java.util.Random;

import javax.inject.Inject;

/**
 * Generates random names and ticker symbols for stocks.
 */
public class NameGenerator {
    private Random mRandom;

    @Inject
    public NameGenerator() {
        mRandom = new Random();
    }

    public String generateStockSymbol() {
        int length = generateStockSymbolLength();
        StringBuffer sb = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            sb.append((char)(mRandom.nextInt(26) + 'A'));
        }
        return sb.toString();
    }

    /** Returns a random length for a stock symbol, with certain distribution. */
    private int generateStockSymbolLength() {
        float r = mRandom.nextFloat();
        if (r < 0.25) {
            return 1;
        }
        if (r < 0.44) {
            return 2;
        }
        if (r < 0.82) {
            return 3;
        }
        return 4;
    }
}
