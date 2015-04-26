package com.macduy.games.fstock.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.macduy.games.fstock.R;
import com.macduy.games.fstock.ui.Format;

/**
 * TODO: Javadoc
 */
public class EffectsView extends FrameLayout {
    private static final String TAG = EffectsView.class.getSimpleName();

    private final LayoutInflater mInflater = LayoutInflater.from(getContext());
    private final Rect mRect = new Rect();

    private ViewGroup mAnchorParent;

    public EffectsView(Context context) {
        this(context, null);
    }

    public EffectsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EffectsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** Sets the parent view to which all anchors are offset against. */
    public void setAnchorParent(ViewGroup parent) {
        mAnchorParent = parent;
    }

    public void spawnFloatingMoney(float money, View anchor) {
        Log.v(TAG, "Creating floating money view.");
        // Create and attach view.
        final TextView tv = (TextView) mInflater.inflate(R.layout.floating_money, null);
        tv.setBackgroundColor(0xff0000);
        this.addView(tv);

        // Set up the view.
        tv.setText(Format.monetary(money));

        // Compute offset.
        mRect.setEmpty();
        mAnchorParent.offsetDescendantRectToMyCoords(anchor, mRect);

        // Set initial position.
        tv.setTranslationX(mRect.left);
        tv.setTranslationY(mRect.top);

        // Animate.
        tv.animate()
                .setDuration(700)
                .alpha(0)
                .translationYBy(-500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        EffectsView.this.removeView(tv);
                    }
                })
        ;
    }

}
