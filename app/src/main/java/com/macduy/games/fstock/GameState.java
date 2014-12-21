package com.macduy.games.fstock;

/**
 * Model/Controller for a single game
 */
public class GameState {
    private float mCurrentMoney;
    private int mTrades;

    public GameState() {
    }

    public void setCurrentMoney(float money) {
        mCurrentMoney = money;
    }


    public float getCurrentMoney() {
        return mCurrentMoney;
    }

    public int getTrades() {
        return mTrades;
    }

    public boolean hasTrades() {
        return getTrades() > 0;
    }

    // Controller-y methods.

    /**
     * Attempt to buy a stock at given price.
     * @return {@code true} if purchase was successful.
     */
    public boolean maybeBuy(StockPrice stock) {
        float price = stock.getLatest();
        if (mCurrentMoney > price) {
            mCurrentMoney -= price;
            mTrades++;
            return true;
        }
        return false;
    }

    /**
     * Sells a stock at given price.
     * @return {@code true} if sell was successful.
     */
    public boolean maybeSell(StockPrice stock) {
        if (hasTrades()) {
            mCurrentMoney += stock.getLatest();
            mTrades--;
            return true;
        }
        return false;
    }

    /**
     * @return The current portfolio value given current trades and stock price.
     */
    public float getPortfolioValue(StockPrice stock) {
        return mCurrentMoney + mTrades * stock.getLatest();
    }
}
