package com.macduy.games.fstock;

import android.animation.TimeAnimator;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.macduy.games.fstock.powerup.Powerup;
import com.macduy.games.fstock.powerup.RaiseStockPricePowerup;

import java.util.ArrayList;
import java.util.List;

public class TradingActivity extends Activity {
    private static final int UPDATE_MS = 64;
    private static final float STARTING_MONEY = 2500;
    private static final float STOP_TIME = 30 * 1000;

    private final StockPrice mStockPrice;
    private final TimeAnimator mAnimator;

    private ViewGroup mContainer;
    private TextView mCurrentPriceView;
    private TextView mCurrentMoneyView;
    private TextView mRatingView;
    private TextView mPerformanceView;
    private TextView mPortfolioValueView;
    private TextView mTimeRemainingView;
    private TextView mHighScoreView;
    private RecyclerView mHoldingsView;
    private RecyclerView mPowerupsView;
    private StockPriceDrawable mStockPriceDrawable;
    private Button mBuyButton;
    private Button mSellButton;

    private HoldingsRecyclerViewController mHoldingsViewController;
    private PowerupRecyclerViewController mPowerupViewController;

    private GameState mCurrentGame;
    private float mHighScore;


    public TradingActivity() {
        mAnimator = new TimeAnimator();
        mAnimator.setRepeatCount(TimeAnimator.INFINITE);
        mAnimator.setRepeatMode(TimeAnimator.RESTART);
        mAnimator.setDuration(1000);

        mStockPrice = new StockPrice();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trading);

        mContainer = (ViewGroup) findViewById(R.id.container);
        mCurrentPriceView = (TextView) findViewById(R.id.current_price);
        mCurrentMoneyView = (TextView) findViewById(R.id.current_money);
        mRatingView = (TextView) findViewById(R.id.rating);
        mPerformanceView = (TextView) findViewById(R.id.performance);
        mPortfolioValueView = (TextView) findViewById(R.id.portfolio_value);
        mBuyButton = (Button) findViewById(R.id.buy);
        mSellButton = (Button) findViewById(R.id.sell);
        mTimeRemainingView = (TextView) findViewById(R.id.time_remaining);
        mHighScoreView = (TextView) findViewById(R.id.highscore);
        mHoldingsView = (RecyclerView) findViewById(R.id.holdings);
        mPowerupsView = (RecyclerView) findViewById(R.id.powerups);
        View graph = findViewById(R.id.graph);

        // Update high score.
        mHighScore = getPreferences(MODE_PRIVATE).getFloat("highscore", 0);
        updateHighscoreView();

        // Set up tick update
        mAnimator.setTimeListener(new TimeAnimator.TimeListener() {
            private long nextUpdate = 0;
            @Override
            public void onTimeUpdate(TimeAnimator timeAnimator, long l, long l1) {
                while (nextUpdate < l) {
                    onTradeUpdate();
                    nextUpdate += UPDATE_MS;
                }
                onUpdate(((float)(nextUpdate - l)) / UPDATE_MS);
                onTimerUpdate(l);
            }
        });

        // Create game
        mCurrentGame = new GameState();
        mCurrentGame.setCurrentMoney(STARTING_MONEY);
        mStockPrice.reset(400);

        // Powerups.
        List<Powerup> mPowerups = new ArrayList<>();
        mPowerups.add(new RaiseStockPricePowerup());

        // Create view controllers.
        mHoldingsViewController =
                new HoldingsRecyclerViewController(mHoldingsView, mCurrentGame.getHoldings());
        mPowerupViewController =
                new PowerupRecyclerViewController(mPowerupsView, mPowerups, mStockPrice);

        mStockPriceDrawable = new StockPriceDrawable(getResources(), mStockPrice);
        graph.setBackground(mStockPriceDrawable);

        updateCurrentMoneyView();
    }

    private void updateHighscoreView() {
        mHighScoreView.setText(String.format("Hightscore: %.2f", mHighScore));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAnimator.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAnimator.cancel();
    }

    protected void onTradeUpdate() {
        // Generate random price.
        float price = mStockPrice.getLatest();
        float delta = ((float) Math.random() - 0.5f) * Math.min(0.5f * price, (float) (Math.pow(Math.random(), 5f) * 80f) + 15f);
        price += delta;
        mStockPrice.pushPrice(price);

        mStockPriceDrawable.invalidateSelf();
        mCurrentPriceView.setText(String.format("£%.2f", price));

        // Update portoflio view.
        float portfolioValue = mCurrentGame.getPortfolioValue(mStockPrice);
        mPortfolioValueView.setText(String.format("(£%.2f)", portfolioValue));
    }

    public void onBuy(View view) {
        if (mCurrentGame.maybeBuy(mStockPrice)) {
            updateCurrentMoneyView();
        }
    }

    public void onSell(View view) {
        if (mCurrentGame.maybeSell(mStockPrice)) {
            updateCurrentMoneyView();
        }
    }

    /**
     * Updates probably everything due to a trade being made.
     * Should also probably be renamed.
     */
    private void updateCurrentMoneyView() {
        mHoldingsViewController.notifyDataSetChanged();

        float performance = (mCurrentGame.getCurrentMoney() - STARTING_MONEY) / STARTING_MONEY;

        int color;
        String prefix = "";
        String performanceString = String.format("%.2f%%", performance * 100);
        if (performance >= 0) {
            color = Color.rgb(20, 180, 20);
            prefix = "+";
        } else {
            color = Color.rgb(180, 20, 20);
        }

        String rating;
        if (performance > 0.5) {
             rating = "Whoa! Are you a banker?";
        } else if (performance > 0.2) {
            rating = "Damn! NICE!!";
        } else if (performance > 0.05) {
            rating = "Not bad";
        } else if (performance > -0.1) {
            rating = "";
        } else if (performance > -0.3) {
            rating = "You suck at this.";
        } else {
            rating = "Wow, you're bad!";
        }

        mPerformanceView.setTextColor(color);
        mPerformanceView.setText(prefix + performanceString);
        mRatingView.setText(rating);
        mCurrentMoneyView.setText(String.format("£%.2f", mCurrentGame.getCurrentMoney()));

        // update buttons
        mSellButton.setEnabled(mCurrentGame.hasTrades());
        mBuyButton.setEnabled(mCurrentGame.getTrades() <= 10);

        if (mCurrentGame.hasTrades()) {
            mSellButton.setText("Sell (" + mCurrentGame.getTrades() + ")");
        } else {
            mSellButton.setText("Sell");
        }
    }

    public void onUpdate(float offset) {
        mStockPriceDrawable.setTimeOffset(offset);
        mStockPriceDrawable.invalidateSelf();

        mCurrentPriceView.setTranslationY(mStockPriceDrawable.getLatestPriceYOffset() - 30f);
    }

    public void onTimerUpdate(long elapsed) {
        int remaining = (int)(STOP_TIME - elapsed) / 1000;
        mTimeRemainingView.setText(String.format("%01d:%02d", remaining / 60, remaining % 60));
        if (elapsed > STOP_TIME) {
            // Stop game.
            mAnimator.end();
            mSellButton.setEnabled(false);
            mBuyButton.setEnabled(false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mContainer.getOverlay().add(mCurrentMoneyView);
                mContainer.animate().alpha(0.5f);
            }

            mCurrentMoneyView.animate().scaleX(2f).scaleY(2f);

            // Commit highscore.
            if (mCurrentGame.getCurrentMoney() > mHighScore) {
                mHighScore = mCurrentGame.getCurrentMoney();
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putFloat("highscore", mHighScore);
                editor.commit();
                updateHighscoreView();
            }
        }
    }
}
