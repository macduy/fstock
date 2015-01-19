package com.macduy.games.fstock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.macduy.games.fstock.graph.MinimumSpanRange;
import com.macduy.games.fstock.powerup.CashInjectionPowerup;
import com.macduy.games.fstock.powerup.Powerup;
import com.macduy.games.fstock.powerup.RaiseStockPricePowerup;
import com.macduy.games.fstock.ui.Format;

import java.util.ArrayList;
import java.util.List;

public class TradingActivity extends Activity {
    private static final float STARTING_MONEY = 2500;
    private static final float STOP_TIME = 30 * 1000;
    private static final float MINIMUM_Y_AXIS_SPAN = 50;

    private final GameController mController;
    private final StockPrice mStockPrice;
    private final List<Powerup> mPowerups = new ArrayList<>();
    private final MinimumSpanRange mRange;

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
    private StockPriceChartDrawable mStockPriceDrawable;
    private Button mBuyButton;
    private Button mSellButton;

    private HoldingsRecyclerViewController mHoldingsViewController;
    private PowerupRecyclerViewController mPowerupViewController;

    private GameState mCurrentGame;
    private float mHighScore;

    private Powerup.Applicator mPowerupApplicator = new Powerup.Applicator() {
        @Override
        public GameState getGameState() {
            return mCurrentGame;
        }

        @Override
        public StockPrice getStockPrice() {
            return mStockPrice;
        }
    };

    private PowerupRecyclerViewController.Listener mPowerupSelectedListener = new
            PowerupRecyclerViewController.Listener() {
                @Override
                public void onPowerupSelected(Powerup powerup) {
                    powerup.apply(mPowerupApplicator);
                    mPowerups.remove(powerup);
                }
            };

    public TradingActivity() {
        mRange = new MinimumSpanRange(MINIMUM_Y_AXIS_SPAN);
        mController = new GameController(new GameControllerListener());
        mStockPrice = new StockPrice();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        // graph.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Update high score.
        mHighScore = getPreferences(MODE_PRIVATE).getFloat("highscore", 0);
        updateHighscoreView();

        // Set up tick update

        // Create game
        mCurrentGame = new GameState();
        mCurrentGame.setCurrentMoney(STARTING_MONEY);
        mStockPrice.reset(400);

        // Powerups.
        mPowerups.add(new RaiseStockPricePowerup());
        mPowerups.add(new CashInjectionPowerup(250));

        // Create view controllers.
        mHoldingsViewController = new HoldingsRecyclerViewController(
                mHoldingsView, mCurrentGame.getHoldings());
        mPowerupViewController = new PowerupRecyclerViewController(
                mPowerupsView, mPowerups, mPowerupSelectedListener);

        // Create graph.
        mStockPriceDrawable = new StockPriceChartDrawable(getResources(), mStockPrice, mRange);
        graph.setBackground(mStockPriceDrawable);

        updateViews();
    }

    private void updateHighscoreView() {
        mHighScoreView.setText(String.format("Highscore: %s", Format.monetary(mHighScore)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mController.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mController.stop();
    }

    public void onBuy(View view) {
        if (mCurrentGame.maybeBuy(mStockPrice)) {
            updateViews();
        }
    }

    public void onSell(View view) {
        if (mCurrentGame.maybeSell(mStockPrice)) {
            updateViews();
        }
    }

    /**
     * Update all views.
     */
    private void updateViews() {
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
        mCurrentMoneyView.setText(Format.monetary(mCurrentGame.getCurrentMoney()));
        // update buttons
        mSellButton.setEnabled(mCurrentGame.hasTrades());
        mBuyButton.setEnabled(mCurrentGame.getTrades() <= 10);

        if (mCurrentGame.hasTrades()) {
            mSellButton.setText("Sell (" + mCurrentGame.getTrades() + ")");
        } else {
            mSellButton.setText("Sell");
        }
    }

    class GameControllerListener implements GameController.Listener {

        @Override
        public void onGameStarted() {

        }

        @Override
        public void onGameEnded() {

        }

        @Override
        public void onGameTick() {
            // Generate random price.Cas
            float price = mStockPrice.getLatest();
            float delta = ((float) Math.random() - 0.5f) * Math.min(0.5f * price, (float) (Math.pow(Math.random(), 5f) * 80f) + 15f);
            price += delta;
            mStockPrice.pushPrice(price);

            // Update range.
            float min = price;
            float max = price;
            for (float p : mStockPrice) {
                if (p < min) {
                    min = p;
                } else if (p > max) {
                    max = p;
                }
            }
            mRange.set(min, max);

            mStockPriceDrawable.invalidateSelf();
            mCurrentPriceView.setText(Format.monetary(price));

            // Update portfolio view.
            float portfolioValue = mCurrentGame.getPortfolioValue(mStockPrice);
            mPortfolioValueView.setText(Format.monetary(portfolioValue));
        }

        @Override
        public void onGameTimeUpdated(long totalElapsed, float sinceLast) {
            mStockPriceDrawable.setTimeOffset(sinceLast);
            mStockPriceDrawable.invalidateSelf();

            int remaining = (int)(STOP_TIME - totalElapsed) / 1000;
            mTimeRemainingView.setText(String.format("%01d:%02d", remaining / 60, remaining % 60));
            if (totalElapsed > STOP_TIME) {
                // Stop game.
                mController.stop();
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
}
