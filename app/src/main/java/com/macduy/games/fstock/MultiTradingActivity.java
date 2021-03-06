package com.macduy.games.fstock;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.macduy.games.fstock.dependency.DaggerFStockComponent;
import com.macduy.games.fstock.dependency.FStockComponent;
import com.macduy.games.fstock.money.Account;
import com.macduy.games.fstock.money.Holding;
import com.macduy.games.fstock.multitrading.MultiTradingController;
import com.macduy.games.fstock.ui.Format;
import com.macduy.games.fstock.view.MiniStockView;

import java.util.IdentityHashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.Bind;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Activity to host the multi-trading game.
 */
public class MultiTradingActivity extends Activity implements MultiTradingController.Listener {
    private static final String TAG = "MultiTradingActv";
    private final Map<StockData, MiniStockView> mStockToView = new IdentityHashMap<>();
    private FStockComponent mFStockComponent;
    private MultiTradingController mController;
    private Account mAccount;

    @Bind(R.id.stocks_grid) GridLayout mStocksGrid;
    @Bind(R.id.cash) TextView mCashView;
    @Bind(R.id.timer) TextView mTimerView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_multi_trading);
        ButterKnife.bind(this);

        mFStockComponent = DaggerFStockComponent.create();
        mAccount = mFStockComponent.account();
        mAccount.deposit(2000);

        mController = mFStockComponent.multiTradingController();
        mController.setListener(this);

        mStockToView.clear();
        for (final StockData stock : mController.stocks()) {
            // Instantiate views and set up mapping to each stock.
            // Use inflation with StocksGrid, otherwise the layout params specified in the xml are ignored.
            MiniStockView view = (MiniStockView)
                    getLayoutInflater().inflate(R.layout.mini_stock_view_layout, mStocksGrid, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onStockClicked(stock);
                }
            });
            mStocksGrid.addView(view);
            mStockToView.put(stock, view);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mController.start();
    }

    @Override
    public void onGameUpdate() {
        // Update all views
        for (StockData stock : mController.stocks()) {
            MiniStockView view = mStockToView.get(stock);

            view.update(stock);

            Holding holding = mController.getHolding(stock);
            if (holding != null) {
                if (stock.getLatest().price > holding.getPurchasePrice()) {
                    // In the green
                    view.animateBackgroundTo(getResources().getColor(R.color.trading_green));
                } else {
                    // In the red
                    view.animateBackgroundTo(getResources().getColor(R.color.trading_red));
                }

            } else {
                // Not purchased
                view.animateBackgroundTo(Color.TRANSPARENT);
            }

            view.update(mController.getTransaction(stock));
        }

        mCashView.setText(Format.monetary(mAccount.getAmount()));

        mTimerView.setText(Format.minuteSeconds(mController.getTimeLeft()));
    }

    @Override
    public void onGameEnd() {

    }

    void onStockClicked(StockData stockData) {
        if (mController.getHolding(stockData) == null) {
            mController.buy(stockData);
        } else {
            mController.sell(stockData);
        }
    }
}
