package com.macduy.games.fstock;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class StockPriceDrawable extends Drawable {
    private StockPrice mStockPrice;
    private Paint mPaint = new Paint();
    private Paint mFillPaint = new Paint();
    private Path mPath = new Path();
    private Path mShadePath = new Path();
    private Rect mDrawBounds = new Rect();

    private float mOffset;

    public StockPriceDrawable(Resources resources, StockPrice stockPrice) {
        mStockPrice = stockPrice;
        mPaint.setAntiAlias(true);
        mPaint.setColor(resources.getColor(R.color.graphEdge));
        mPaint.setStrokeWidth(6f);
        mPaint.setStyle(Paint.Style.STROKE);

        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(resources.getColor(R.color.graphFill));
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mDrawBounds.set(bounds);
        mDrawBounds.right = mDrawBounds.right - 10;
    }

    @Override
    public void draw(Canvas canvas) {
        Rect b = mDrawBounds;
        mPath.reset();
        mShadePath.reset();

        int width = b.width();
        int i = 0;
        float step = (float) width / 100;

        mShadePath.moveTo(b.left, b.bottom);
        float x, y = 0;
        for (float price : mStockPrice) {
            x = ((float)i + mOffset) * step;
            y = b.bottom - price;

            if (i == 0) {
                mPath.moveTo(b.left, y);
                mShadePath.lineTo(b.left, y);
            }

            mPath.lineTo(x, y);
            mShadePath.lineTo(x, y);
            i++;
        }
        mPath.lineTo(b.right, y);
        mShadePath.lineTo(b.right, y);
        mShadePath.lineTo(b.right, b.bottom);

        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mShadePath, mFillPaint);

        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(b.right, y, 5f, mPaint);
    }

    public float getLatestPriceYOffset() {
        return getBounds().bottom - mStockPrice.getLatest();
    }

    public void setTimeOffset(float offset) {
        mOffset = offset;
    }

    @Override
    public void setAlpha(int i) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSPARENT;
    }
}
