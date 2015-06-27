package com.macduy.games.fstock.multitrading;

import android.animation.TimeAnimator;

import com.macduy.games.fstock.Clock;
import com.macduy.games.fstock.StockData;
import com.macduy.games.fstock.money.Account;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import static android.animation.TimeAnimator.TimeListener;

/**
 * Game Controller for Multi-trading game
 */
public class MultiTradingController {
    private final TimeAnimator mGameTick;
    private final List<StockData> mStocks = new LinkedList<>();
    private final Clock mClock;
    private final Account mAccount;

    private Listener mListener;
    private long mGameStart;

    @Inject
    MultiTradingController(Clock clock, Account account) {
        mGameTick = new TimeAnimator();
        mClock = clock;
        mAccount = account;

        // Generate stocks
        for (int i = 0; i < 10; i++) {
            mStocks.add(new StockData());
        }

        mGameTick.setTimeListener(new TimeListener() {
            @Override
            public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
                onGameTick();
            }
        });
    }

    public Iterable<StockData> stocks() {
        return mStocks;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    private void onGameTick() {
        long time = mClock.getTime();
        for (StockData stockData : mStocks) {
            float price = stockData.getLatest().price;
            float delta = ((float) Math.random() - 0.5f)
                    * Math.min(0.5f * price, (float) (Math.pow(Math.random(), 5f) * 80f) + 15f);
            price += delta;
            stockData.pushPrice(mClock.getTime(), price);
        }

        mListener.onGameUpdate();
    }

    public void start() {
        mGameStart = mClock.getTime();
        // Generate initial price for all stocks.
        for (StockData stockData : mStocks) {
            stockData.pushPrice(mGameStart, 100);
        }
        mGameTick.start();
    }

    public void buy(StockData stockData) {
        mAccount.withdraw(stockData.getLatest().price);
    }

    public interface Listener {
        void onGameUpdate();
    }
}
