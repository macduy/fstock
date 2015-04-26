package com.macduy.games.fstock.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

/**
 * TODO: Javadoc
 */
public class TradingView extends LinearLayout {
    private static final String TAG = "TradingView";
    private final int mScaledTouchSlop;

    private int mLastTouchDownY;
    private boolean mIntercepted;
    private boolean mOverscroll;

    @Nullable
    private OverscrollListener mListener;

    public TradingView(Context context) {
        this(context, null);
    }

    public TradingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TradingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public void setListener(OverscrollListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        mIntercepted = false;

        Log.v(TAG, "onITE, event " + action);

        if (action == MotionEvent.ACTION_DOWN) {
            mLastTouchDownY = (int) event.getY();
            Log.v(TAG, "onITE, touch down " + mLastTouchDownY);
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (Math.abs(mLastTouchDownY - event.getY()) > mScaledTouchSlop) {
                mIntercepted = true;
                mOverscroll = true;
                Log.v(TAG, "onITE, Intercept!");
            }
        }

        return mIntercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int eventY = (int) event.getY();
        if (action == MotionEvent.ACTION_DOWN) {
            mLastTouchDownY = (int) event.getY();
            return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (Math.abs(mLastTouchDownY - eventY) > mScaledTouchSlop) {
                mOverscroll = true;
            }
            if (mOverscroll) {
                if (mListener != null) {
                    mListener.onOverscroll(eventY - mLastTouchDownY);
                }
            }
        } else if (action == MotionEvent.ACTION_UP) {
            mOverscroll = false;
            if (mListener != null) {
                mListener.onOverscrollFinished();
            }
        }
        Log.v(TAG, "onTOuchEvent " + mIntercepted);
        return mIntercepted;
    }

    public interface OverscrollListener {
        void onOverscroll(int distance);
        void onOverscrollFinished();
    }
}
