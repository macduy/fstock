package com.macduy.games.fstock.money;

import com.macduy.games.fstock.StockData;

/**
 * Represents a purchased stock.
 */
public class Holding {
    public final StockData stock;
    private final double mPurchasePrice;

    public Holding(StockData stock) {
        this(stock, stock.getLatest().price);
    }

    public Holding(StockData stock, double purchasePrice) {
        this.stock = stock;
        mPurchasePrice = purchasePrice;
    }

    public double getPurchasePrice() {
        return mPurchasePrice;
    }
}
