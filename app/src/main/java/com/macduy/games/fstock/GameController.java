package com.macduy.games.fstock;

import android.animation.TimeAnimator;

/**
 * Runs the show!
 *
 * Responsibilities:
 *  - Keep track of current time
 *  - Decide when the stock price should be updated.
 *  - Track when the game should end.
 */
public class GameController {
    private static final int UPDATE_MS = 64;
    private static final int GAME_DURATION_MS = 30 * 1000;

    private enum State {
        /** Current game is running. */
        RUNNING,

        /** The game has finished. */
        FINISHED,
    }

    private final Listener mListener;

    private TimeAnimator mAnimator;
    private long mGameStart;
    private long mCurrentGameTime;
    private Clock mClock = new Clock();

    public GameController(Listener listener) {
        mListener = listener;

        // Set up animator.
        mAnimator = new TimeAnimator();
        mAnimator.setRepeatCount(TimeAnimator.INFINITE);
        mAnimator.setRepeatMode(TimeAnimator.RESTART);
        mAnimator.setDuration(1000);

        mAnimator.setTimeListener(new TimeAnimator.TimeListener() {
            private long nextUpdate = 0;
            @Override
            public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
                mCurrentGameTime = totalTime;
                while (nextUpdate < totalTime) {
                    mListener.onGameTick();
                    nextUpdate += UPDATE_MS;
                }
                mListener.onGameTimeUpdated();

                if (mCurrentGameTime > GAME_DURATION_MS) {
                    // Stop timer.
                    mAnimator.end();

                    mListener.onGameEnded();
                }
            }
        });
    }

    public void start() {
        mGameStart = mClock.getTime();
        mListener.onGameStarted();
        mAnimator.start();
    }

    public long getCurrentGameTime() {
        return mCurrentGameTime;
    }

    public long getRemainingTime() {
        return GAME_DURATION_MS - mCurrentGameTime;
    }

    // TODO: Remove this, shouldn't exist
    public void stop() {
        mAnimator.end();
    }

    public interface Listener {
        void onGameStarted();
        void onGameEnded();
        void onGameTick();
        void onGameTimeUpdated();
    }
}
