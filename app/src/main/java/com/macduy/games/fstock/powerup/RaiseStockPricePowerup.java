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
    public void apply(Applicator applicator) {
        // Raise the price.
        StockPrice stock = applicator.getStockPrice();
        stock.pushPrice(stock.getLatest() * 2f);
    }
}
