package com.macduy.games.fstock.powerup;

import com.macduy.games.fstock.StockPrice;

/**
 * TODO: Javadoc
 */
public class RaiseStockPricePowerup implements Powerup {
    @Override
    public String getName() {
        return "Raise";
    }

    @Override
    public void apply(StockPrice stock) {
        // Raise the price.
        stock.pushPrice(stock.getLatest() * 2f);
    }
}
