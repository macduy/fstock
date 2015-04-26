package com.macduy.games.fstock.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.macduy.games.fstock.GameState;
import com.macduy.games.fstock.R;
import com.macduy.games.fstock.ui.Format;

/**
 * Game Summary View. Tied to game_summary.xml
 */
public class GameSummaryView extends LinearLayout {
    private GameState mGameState;

    // Child views.
    private TextView mFinalPortfolioValue;
    private TextView mProfitLoss;
    private TextView mProfitLossPercentage;

    public GameSummaryView(Context context) {
        this(context, null);
    }

    public GameSummaryView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public GameSummaryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.VERTICAL);
    }

    @Override
    protected void onFinishInflate() {
        mFinalPortfolioValue = (TextView) findViewById(R.id.final_score);
        mProfitLoss = (TextView) findViewById(R.id.profit_loss);
        mProfitLossPercentage = (TextView) findViewById(R.id.profit_loss_percentage);
    }

    public void setGameState(GameState gameState) {
        mGameState = gameState;
    }

    public void animateIn() {
        setAlpha(0f);
        setScaleX(0.8f);
        setScaleY(0.8f);
        setVisibility(View.VISIBLE);
        animate().alpha(1f).scaleX(1f).scaleY(1f);
    }

    public void updateFromGameState() {
        mFinalPortfolioValue.setText(Format.monetary(mGameState.getPortfolioValue()));
    }
}
