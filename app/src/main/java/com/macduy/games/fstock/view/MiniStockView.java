package com.macduy.games.fstock.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.macduy.games.fstock.R;
import com.macduy.games.fstock.StockData;
import com.macduy.games.fstock.ui.Format;

/**
 * A view that represents concisely a single stock in the multi-trading game.
 */
public class MiniStockView extends RelativeLayout {
    private TextView mTextPrice;

    public MiniStockView(Context context) {
        this(context, null);
    }

    public MiniStockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiniStockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        mTextPrice = (TextView) findViewById(R.id.text_price);
    }

    public void update(StockData stock) {
        mTextPrice.setText(Format.monetary(stock.getLatest().price));
    }

}
