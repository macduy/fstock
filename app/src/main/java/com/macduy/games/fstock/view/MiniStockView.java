package com.macduy.games.fstock.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.macduy.games.fstock.R;
import com.macduy.games.fstock.StockData;
import com.macduy.games.fstock.StockPriceChartDrawable;
import com.macduy.games.fstock.graph.FixedRange;
import com.macduy.games.fstock.graph.SimpleRange;
import com.macduy.games.fstock.ui.Format;

import butterknife.ButterKnife;
import butterknife.Bind;

/**
 * A view that represents concisely a single stock in the multi-trading game.
 */
public class MiniStockView extends FrameLayout {
    @Bind(R.id.text_price) TextView mTextPrice;
    @Bind(R.id.graph) View mGraphView;
    @Bind(R.id.name) TextView mTextName;

    private StockData mStockData;
    private FixedRange mTimeRange = new FixedRange(3000);

    private ValueAnimator mBackgroundAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), Color.TRANSPARENT);
    @ColorInt private int mCurrentBackgroundColor = Color.TRANSPARENT;
    @ColorInt private int mDestinationColor = 0;

    public MiniStockView(Context context) {
        this(context, null);
    }

    public MiniStockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MiniStockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBackgroundAnimator.setDuration(200);
        mBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentBackgroundColor = (int) animation.getAnimatedValue();
                setBackgroundColor(mCurrentBackgroundColor);
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void update(StockData stock) {
        if (mStockData != stock) {
            mStockData = stock;

            // Refresh chart drawable
            mGraphView.setBackground(
                    new StockPriceChartDrawable(
                            getResources(),
                            mStockData,
                            new SimpleRange(0, 200),
                            mTimeRange));

            // Update name shown.
            mTextName.setText(mStockData.getName());
        }

        mTimeRange.setEnd(stock.getLatest().time);
        mGraphView.invalidate();

        mTextPrice.setText(Format.monetary(stock.getLatest().price));
    }

    public void animateBackgroundTo(@ColorInt int color) {
        if (mDestinationColor == color) {
            return;
        }

        mDestinationColor = color;
        mBackgroundAnimator.cancel();
        mBackgroundAnimator.setObjectValues(mCurrentBackgroundColor, color);
        mBackgroundAnimator.start();
    }
}
