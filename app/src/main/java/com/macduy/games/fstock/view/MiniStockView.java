package com.macduy.games.fstock.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.macduy.games.fstock.R;
import com.macduy.games.fstock.StockData;
import com.macduy.games.fstock.StockPriceChartDrawable;
import com.macduy.games.fstock.graph.FixedRange;
import com.macduy.games.fstock.graph.SimpleRange;
import com.macduy.games.fstock.money.Transaction;
import com.macduy.games.fstock.ui.Format;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A view that represents concisely a single stock in the multi-trading game.
 */
public class MiniStockView extends FrameLayout {
    private static final int MAX_PROGRESS = 100;

    private StockData mStockData;
    private FixedRange mTimeRange = new FixedRange(3000);

    @Bind(R.id.text_price) TextView mTextPrice;
    @Bind(R.id.graph) View mGraphView;
    @Bind(R.id.name) TextView mTextName;
    @Bind(R.id.progress_bar) ProgressBar mProgressBar;

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

        mProgressBar.setMax(MAX_PROGRESS);
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

    public void update(@Nullable Transaction transaction) {
        if (transaction == null) {
            // Hide the progress bar.
            if (mProgressBar.getVisibility() == VISIBLE) {
                mProgressBar.setVisibility(GONE);
                animate().cancel();
                animate().scaleX(1f).scaleY(1f).rotationX(0f);
            }

        } else {
            // Show the progress bar and then update it
            if (mProgressBar.getVisibility() != VISIBLE) {
                mProgressBar.setVisibility(VISIBLE);
                animate().cancel();
                animate().scaleX(0.9f).scaleY(0.9f).rotationX(10f);
            }

            mProgressBar.setProgress((int) (transaction.getProgress() * MAX_PROGRESS));
        }
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
