package com.macduy.games.fstock;

import java.util.LinkedList;
import java.util.List;

/**
 * Model/Controller for a single game
 */
public class GameState {
    private float mCurrentMoney;
    private List<Holding> mHoldings = new LinkedList<>();

    public GameState() {
    }

    public void setCurrentMoney(float money) {
        mCurrentMoney = money;
    }

    public float getCurrentMoney() {
        return mCurrentMoney;
    }

    public int getTrades() {
        return mHoldings.size();
    }

    public boolean hasTrades() {
        return getTrades() > 0;
    }

    public List<Holding> getHoldings() {
        return mHoldings;
    }

    // Controller-y methods.

    /**
     * Attempt to buy a stock at given price.
     * @return {@code true} if purchase was successful.
     */
    public boolean maybeBuy(StockData stock) {
        float price = stock.getLatest().price;
        if (mCurrentMoney > price) {
            mCurrentMoney -= price;
            mHoldings.add(new Holding(stock));
            return true;
        }
        return false;
    }

    /**
     * Sells a stock at given price.
     * @return {@code true} if sell was successful.
     */
    public boolean maybeSell(StockData stock) {
        if (hasTrades()) {
            mCurrentMoney += stock.getLatest().price;
            mHoldings.remove(0);
            return true;
        }
        return false;
    }

    /**
     * @return The current portfolio value given current trades and stock price.
     */
    public float getPortfolioValue(StockData stock) {
        return mCurrentMoney + getTrades() * stock.getLatest().price;
    }
}
