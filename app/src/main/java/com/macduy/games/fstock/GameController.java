package com.macduy.games.fstock;

import android.animation.TimeAnimator;

/**
 *
 */
public class GameController {
    private enum State {
        /** Current game is running. */
        RUNNING,

        /** The game has finished. */
        FINISHED,
    }

    private Listener mListener;
    private State mCurrentState;
    private TimeAnimator mAnimator;

    public GameController() {
        // Set up animator.
        mAnimator = new TimeAnimator();
        mAnimator.setRepeatCount(TimeAnimator.INFINITE);
        mAnimator.setRepeatMode(TimeAnimator.RESTART);
        mAnimator.setDuration(1000);
    }

    public void start() {

    }



    public interface Listener {
        void onGameStarted();
        void onGameEnded();
    }
}
