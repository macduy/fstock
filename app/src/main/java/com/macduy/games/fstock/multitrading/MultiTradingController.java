package com.macduy.games.fstock.multitrading;

import android.animation.TimeAnimator;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;

import com.macduy.games.fstock.Clock;
import com.macduy.games.fstock.StockData;
import com.macduy.games.fstock.money.Account;
import com.macduy.games.fstock.money.Holding;
import com.macduy.games.fstock.money.Transaction;
import com.macduy.games.fstock.stock.NameGenerator;

import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.map.LazyMap;

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
    @Deprecated
    private final Map<StockData, Holding> mHoldings = new HashMap<>();
    private final Map<StockData, StockState> mStockStates;
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
        mStockStates = LazyMap.lazyMap(new HashMap<StockData, StockState>(),
                new Factory<StockState>() {
                    @Override
                    public StockState create() {
                        return new StockState();
                    }
                });

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
        return mStockStates.get(stock).holding;
    }

    @Nullable
    public Transaction getTransaction(StockData stock) {
        return mStockStates.get(stock).transaction;
    }

    public void onStockClicked(StockData stockData) {
        StockState state = mStockStates.get(stockData);
        if (state.holding == null) {
            if (state.transaction == null) {
                // Stock not held and no pending transaction. Buy the stock.
                buy(stockData);
            } else {
                // Stock not held but being bought. Cancel the transaction.
                state.transaction.cancel();
                state.transaction = null;
            }
        } else {
            if (state.transaction == null) {
                // Stock is held, selling.
                sell(stockData);
            } else {
                // Stock is held but being sold. Cancel the transaction.
                state.transaction.cancel();
                state.transaction = null;
            }
        }
    }

    // Following methods require the game to be running.
    public void buy(StockData stockData) {
        final StockState state = mStockStates.get(stockData);
        state.transaction = new Transaction(Transaction.Type.BUY, stockData,
                new Transaction.Listener() {
                    @Override
                    public void onExecuted(Transaction transaction) {
                        StockData stockData = transaction.stock;
                        double purchasePrice = stockData.getLatest().price;
                        mAccount.withdraw(stockData.getLatest().price);

                        // Record new holding.
                        state.transaction = null;
                        state.holding = new Holding(stockData, purchasePrice);
                    }

                    @Override
                    public void onProgressUpdate(
                            Transaction transaction,
                            @FloatRange(from = 0f, to = 1f) float progress) {}
                });
    }

    public void sell(StockData stockData) {
        final StockState state = mStockStates.get(stockData);
        state.transaction = new Transaction(Transaction.Type.SELL, stockData,
                new Transaction.Listener() {
                    @Override
                    public void onExecuted(Transaction transaction) {
                        mAccount.deposit(transaction.stock.getLatest().price);
                        state.transaction = null;
                        state.holding = null;
                    }

                    @Override
                    public void onProgressUpdate(
                            Transaction transaction,
                            @FloatRange(from = 0f, to = 1f) float progress) {

                    }
                });
    }

    public interface Listener {
        void onGameUpdate();

        void onGameEnd();
    }

    private enum State {
        RUNNING,
        STOPPED
    }

    /**
     * Stores current in-game state for a particular stock.
     **/
    private class StockState {
        @Nullable Holding holding;
        @Nullable Transaction transaction;
    }
}
