package com.macduy.games.fstock;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.Gravity;
import android.view.ViewGroup;

import com.macduy.games.fstock.dependency.DaggerFStockComponent;
import com.macduy.games.fstock.dependency.FStockComponent;
import com.macduy.games.fstock.multitrading.MultiTradingController;
import com.macduy.games.fstock.view.MiniStockView;

import java.util.IdentityHashMap;
import java.util.Map;

import static android.view.ViewGroup.*;

public class MultiTradingActivity extends Activity implements MultiTradingController.Listener {

    private final Map<StockData, MiniStockView> mStockToView = new IdentityHashMap<>();
    private FStockComponent mFStockComponent;
    private MultiTradingController mController;
    private GridLayout mStocksGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_trading);

        mStocksGrid = (GridLayout) findViewById(R.id.stocks_grid);

        mFStockComponent = DaggerFStockComponent.create();
        mController = mFStockComponent.multiTradingController();
        mController.setListener(this);

        // Instantiate views and set up mapping to each stock.
        mStockToView.clear();
        for (StockData stock : mController.stocks()) {
            // Use inflation with StocksGrid, otherwise the layout params specified in the xml are ignored.
            MiniStockView view = (MiniStockView)
                    getLayoutInflater().inflate(R.layout.mini_stock_view_internal, mStocksGrid, false);
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
            mStockToView.get(stock).update(stock);
        }
    }
}
