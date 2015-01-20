package com.macduy.games.fstock.powerup;

import com.macduy.games.fstock.StockData;

/**
 * TODO: Javadoc
 */
public class RaiseStockPricePowerup implements Powerup {
    @Override
    public String getName() {
        return "Raise";
    }

    @Override
    public void apply(Applicator applicator) {
        // Raise the price.
        StockData stock = applicator.getStockPrice();
        // TODO: This is currently broken.
//        stock.pushPrice(stock.getLatest().price * 2f);
    }
}
