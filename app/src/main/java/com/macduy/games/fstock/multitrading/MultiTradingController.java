package com.macduy.games.fstock.multitrading;

import android.animation.TimeAnimator;
import android.support.annotation.Nullable;

import com.macduy.games.fstock.Clock;
import com.macduy.games.fstock.StockData;
import com.macduy.games.fstock.money.Account;
import com.macduy.games.fstock.money.Holding;
import com.macduy.games.fstock.stock.NameGenerator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static android.animation.TimeAnimator.TimeListener;

/**
 * Game Controller for Multi-trading game.
 */
public class MultiTradingController {
    private static final String TAG = "MultiTradingCtrl";

    private static final long GAME_DURATION_MS = 60 * 1000;

    private final TimeAnimator mGameTick;
    private final List<StockData> mStocks = new LinkedList<>();
    private final Map<StockData, Holding> mHoldings = new HashMap<>();
    private final Clock mClock;
    private final Account mAccount;
    private final NameGenerator mNameGenerator;

    private State mState = State.STOPPED;
    private Listener mListener;
    private long mGameStart;

    @Inject
    MultiTradingController(Clock clock, Account account, NameGenerator nameGenerator) {
        mGameTick = new TimeAnimator();
        mClock = clock;
        mAccount = account;
        mNameGenerator = nameGenerator;

        // Generate stocks
        for (int i = 0; i < 9; i++) {
            StockData stockData = new StockData();
            stockData.setName(mNameGenerator.generateStockSymbol());
            mStocks.add(stockData);
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
            stockData.pushPrice(time, price);
        }

        mListener.onGameUpdate();

        if (getTimeLeft() < 0) {
            stop();
        }
    }

    private void stop() {
        mState = State.STOPPED;
        mGameTick.end();
        mListener.onGameEnd();
    }

    public void start() {
        mState = State.RUNNING;
        mGameStart = mClock.getTime();
        // Generate initial price for all stocks.
        for (StockData stockData : mStocks) {
            stockData.pushPrice(mGameStart, 100);
        }
        mGameTick.start();
    }

    public long getElapsedTime() {
        return mClock.getTime() - mGameStart;
    }

    public long getTimeLeft() {
        return GAME_DURATION_MS - getElapsedTime();
    }

    @Nullable
    public Holding getHolding(StockData stock) {
        return mHoldings.get(stock);
    }

    // Following methods require the game to be running.
    public void buy(StockData stockData) {
        // Each stock can only be purchased once.
        double purchasePrice = stockData.getLatest().price;
        mHoldings.put(stockData, new Holding(stockData, purchasePrice));
        mAccount.withdraw(stockData.getLatest().price);
    }

    public void sell(StockData stockData) {
        mHoldings.remove(stockData);
        mAccount.deposit(stockData.getLatest().price);
    }

    public interface Listener {
        void onGameUpdate();
        void onGameEnd();
    }

    private enum State {
        RUNNING,
        STOPPED
    }
}
