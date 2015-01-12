package com.macduy.games.fstock;

import android.animation.TimeAnimator;

/**
 *
 */
public class GameController {
    private static final int UPDATE_MS = 64;

    private enum State {
        /** Current game is running. */
        RUNNING,

        /** The game has finished. */
        FINISHED,
    }

    private final Listener mListener;
    private TimeAnimator mAnimator;

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
                while (nextUpdate < totalTime) {
                    mListener.onGameTick();
                    nextUpdate += UPDATE_MS;
                }
                mListener.onGameTimeUpdated(
                        totalTime, ((float) (nextUpdate - totalTime)) / UPDATE_MS);
            }
        });
    }

    public void start() {
        mAnimator.start();
    }

    // TODO: Remove this, shouldn't exist
    public void stop() {
        mAnimator.end();
    }

    public interface Listener {
        void onGameStarted();
        void onGameEnded();
        void onGameTick();
        void onGameTimeUpdated(long totalElapsed, float sinceLast);
    }
}
