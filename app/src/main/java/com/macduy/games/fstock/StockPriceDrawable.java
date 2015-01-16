package com.macduy.games.fstock;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.macduy.games.fstock.graph.Range;
import com.macduy.games.fstock.graph.RangeMapper;

public class StockPriceDrawable extends Drawable {
    private final Range mVBoundsRange;
    private final RangeMapper mYMapper;

    private StockPrice mStockPrice;
    private Paint mPaint = new Paint();
    private Paint mFillPaint = new Paint();
    private Path mPath = new Path();
    private Path mShadePath = new Path();
    private Rect mDrawBounds = new Rect();

    private float mOffset;

    public StockPriceDrawable(Resources res, StockPrice stockPrice, Range yRange) {
        mStockPrice = stockPrice;
        mPaint.setAntiAlias(true);
        mPaint.setColor(res.getColor(R.color.graphEdge));
        mPaint.setStrokeWidth(res.getDimensionPixelSize(R.dimen.stock_line_thickness));
        mPaint.setStyle(Paint.Style.STROKE);

        mVBoundsRange = new Range() {
            @Override
            public float start() {
                return getBounds().bottom;
            }

            @Override
            public float end() {
                return getBounds().top;
            }
        };

        mYMapper = new RangeMapper(yRange, mVBoundsRange);

        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(res.getColor(R.color.graphFill));
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
            y = mYMapper.get(price);

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
        return mYMapper.get(mStockPrice.getLatest());
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
