package com.macduy.games.fstock.money;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;

import com.macduy.games.fstock.StockData;

/**
 * Represents a transaction. A transaction can take time before being executed.
 */
public class Transaction {
    private static final int TRANSACTION_DURATION_MS = 500;

    public enum Type {
        BUY, SELL
    }

    public final Type type;
    public final StockData stock;
    /**
     * Simulates the duration of the transaction.
     */
    private ValueAnimator mAnimator = new ValueAnimator();
    @Nullable private Listener mListener;

    private boolean mDone;
    private boolean mCancelled;

    public Transaction(Type type, StockData stock, Listener listener) {
        this.type = type;
        this.stock = stock;
        mListener = listener;

        AnimatorCallbacks callbacks = new AnimatorCallbacks();
        mAnimator.setFloatValues(0f, 1f);
        mAnimator.setDuration(TRANSACTION_DURATION_MS);
        mAnimator.addUpdateListener(callbacks);
        mAnimator.addListener(callbacks);
        mAnimator.start();
    }

    public void cancel() {
        if (!mCancelled && !mDone) {
            mCancelled = true;
            mDone = true;
            mAnimator.cancel();
        }
    }

    public float getProgress() {
        if (mDone) {
            return 1f;
        }
        return mAnimator.getAnimatedFraction();
    }

    private class AnimatorCallbacks extends AnimatorListenerAdapter
            implements ValueAnimator.AnimatorUpdateListener {

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            if (mListener != null) {
                mListener.onProgressUpdate(Transaction.this, animation.getAnimatedFraction());
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (mListener != null) {
                mListener.onExecuted(Transaction.this);
            }
            mDone = true;
        }
    }

    public interface Listener {
        void onExecuted(Transaction transaction);

        void onProgressUpdate(
                Transaction transaction,
                @FloatRange(from = 0f, to = 1f) float progress);
    }
}

