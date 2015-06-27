package com.macduy.games.fstock;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.macduy.games.fstock.graph.FixedRange;
import com.macduy.games.fstock.graph.MinimumSpanRange;
import com.macduy.games.fstock.powerup.CashInjectionPowerup;
import com.macduy.games.fstock.powerup.Powerup;
import com.macduy.games.fstock.powerup.RaiseStockPricePowerup;
import com.macduy.games.fstock.ui.Format;
import com.macduy.games.fstock.view.EffectsView;
import com.macduy.games.fstock.view.GameSummaryView;
import com.macduy.games.fstock.view.TradingView;

import java.util.ArrayList;
import java.util.List;

public class TradingActivity extends Activity implements TradingView.OverscrollListener {
    private static final float STARTING_MONEY = 2500;
    private static final float MINIMUM_Y_AXIS_SPAN = 50;

    private final GameController mController;
    private final StockData mStockData;
    private final List<Powerup> mPowerups = new ArrayList<>();
    private final MinimumSpanRange mRange;
    private final FixedRange mTimeRange;

    /** Pixels to overscroll to buy/sell. */
    private int mOverscrollPurchaseThreshold;

    private TradingView mTradingView;
    private EffectsView mEffectsView;
    private GameSummaryView mGameSummaryView;
    private View mTestView;

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
    private int mOverscrollDistance;

    private float mHighScore;

    private Powerup.Applicator mPowerupApplicator = new Powerup.Applicator() {
        @Override
        public GameState getGameState() {
            return mCurrentGame;
        }

        @Override
        public StockData getStockPrice() {
            return mStockData;
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
        mTimeRange = new FixedRange(6400); // Computed as 100 points * UPDATE_MS (64)
        mController = new GameController(new GameControllerListener());
        mStockData = new StockData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Resources r = getResources();
        mOverscrollPurchaseThreshold = r.getDimensionPixelSize(R.dimen.drag_to_purchase);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_trading);

        mTradingView = (TradingView) findViewById(R.id.trading_layout);
        mEffectsView = (EffectsView) findViewById(R.id.effects_view);
        mGameSummaryView = (GameSummaryView) findViewById(R.id.game_summary);
        mTestView = findViewById(R.id.test);

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

        // Hook up listener.
        mTradingView.setListener(this);

        // Set up larger views.
        mEffectsView.setAnchorParent(mTradingView);
        mGameSummaryView.setVisibility(View.INVISIBLE);

        // Update high score.
        mHighScore = getPreferences(MODE_PRIVATE).getFloat("highscore", 0);
        updateHighscoreView();

        // Set up tick update

        // Create game
        mCurrentGame = new GameState();
        mCurrentGame.setCurrentMoney(STARTING_MONEY);
        mStockData.reset();

        // Powerups.
        mPowerups.add(new RaiseStockPricePowerup());
        mPowerups.add(new CashInjectionPowerup(250));

        // Create view controllers.
        mHoldingsViewController = new HoldingsRecyclerViewController(
                mHoldingsView, mCurrentGame.getHoldings());
        mPowerupViewController = new PowerupRecyclerViewController(
                mPowerupsView, mPowerups, mPowerupSelectedListener);

        // Create graph.
        mStockPriceDrawable = new StockPriceChartDrawable(
                getResources(), mStockData, mRange, mTimeRange);
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
        if (mCurrentGame.maybeBuy(mStockData)) {
            mEffectsView.spawnFloatingMoney(mStockData.getLatest().price, mCurrentMoneyView);
            updateViews();
        }
    }

    public void onSell(View view) {
        if (mCurrentGame.maybeSell(mStockData)) {
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

    @Override
    public void onOverscroll(int distance) {
        mOverscrollDistance = distance;
        boolean far_enough = Math.abs(distance) > mOverscrollPurchaseThreshold;
        mTestView.setTranslationY(distance);
        mTestView.setBackgroundColor(far_enough ? Color.GREEN : Color.RED);
    }

    @Override
    public void onOverscrollFinished() {
        if (mOverscrollDistance > mOverscrollPurchaseThreshold) {
            mCurrentGame.maybeBuy(mStockData);
            updateViews();
        } else if (mOverscrollDistance < -mOverscrollPurchaseThreshold) {
            mCurrentGame.maybeSell(mStockData);
            updateViews();
        }
        mTestView.animate().translationY(0);
        mOverscrollDistance = 0;
    }

    class GameControllerListener implements GameController.Listener {

        @Override
        public void onGameStarted() {
            mStockData.pushPrice(0L, 400f);
        }

        @Override
        public void onGameEnded() {
            mSellButton.setEnabled(false);
            mBuyButton.setEnabled(false);

            mGameSummaryView.animateIn();

            // Commit highscore.
            if (mCurrentGame.getCurrentMoney() > mHighScore) {
                mHighScore = mCurrentGame.getCurrentMoney();
                SharedPreferences.Editor editor = getPreferences(MODE_PRIVATE).edit();
                editor.putFloat("highscore", mHighScore);
                editor.commit();
                updateHighscoreView();
            }
        }

        @Override
        public void onGameTick() {
            // Generate random price.
            float price = mStockData.getLatest().price;
            float delta = ((float) Math.random() - 0.5f)
                    * Math.min(0.5f * price, (float) (Math.pow(Math.random(), 5f) * 80f) + 15f);
            price += delta;
            mStockData.pushPrice(mController.getCurrentGameTime(), price);

            // Update range.
            float min = price;
            float max = price;
            for (StockData.Price p : mStockData) {
                if (p.price < min) {
                    min = p.price;
                } else if (p.price > max) {
                    max = p.price;
                }
            }
            mRange.set(min, max);

            mStockPriceDrawable.invalidateSelf();
            mCurrentPriceView.setText(Format.monetary(price));

            // Update portfolio view.
            float portfolioValue = mCurrentGame.getPortfolioValue();
            mPortfolioValueView.setText(Format.monetary(portfolioValue));
        }

        @Override
        public void onGameTimeUpdated() {
            // Update time range.
            mTimeRange.setEnd(mController.getCurrentGameTime());
            mStockPriceDrawable.invalidateSelf();

            mTimeRemainingView.setText(Format.minuteSeconds(mController.getRemainingTime()));
        }
    }
}
