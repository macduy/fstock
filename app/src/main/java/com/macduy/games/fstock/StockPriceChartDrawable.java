package com.macduy.games.fstock;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.macduy.games.fstock.graph.Range;
import com.macduy.games.fstock.graph.RangeMapper;

public class StockPriceChartDrawable extends Drawable {
    // TODO: Unharcode these.
    private static final int GRIDLINE_SPACING = 50;
    private static final int TIMESPAN = 100;

    // Data range.
    private final Range mYRange;
    private final Range mVBoundsRange;
    private final RangeMapper mYMapper;
    private final float mShadowOffset;

    private StockPrice mStockPrice;
    private Paint mPaint = new Paint();
    private Paint mShadowPaint = new Paint();
    private Paint mFillPaint = new Paint();
    private Paint mGridlinePaint = new Paint();
    private Paint mGridlineLabelPaint = new Paint();

    private Path mPath = new Path();
    private Path mShadePath = new Path();
    private Path mGridlinePath = new Path();

    private Rect mDrawBounds = new Rect();

    private float mOffset;

    public StockPriceChartDrawable(Resources res, StockPrice stockPrice, Range yRange) {
        mStockPrice = stockPrice;
        mShadowOffset = res.getDimensionPixelOffset(R.dimen.line_chart_shadow_offest);

        mPaint.setAntiAlias(true);
        mPaint.setColor(res.getColor(R.color.line_chart_line));
        mPaint.setStrokeWidth(res.getDimensionPixelSize(R.dimen.line_chart_thickness));
        mPaint.setStyle(Paint.Style.STROKE);

        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(res.getColor(R.color.line_chart_shadow));
        mShadowPaint.setStrokeWidth(mPaint.getStrokeWidth());
        mShadowPaint.setStyle(Paint.Style.STROKE);

        mGridlinePaint.setAntiAlias(true);
        mGridlinePaint.setColor(res.getColor(R.color.gridline));
        mGridlinePaint.setStrokeWidth(res.getDimensionPixelSize(R.dimen.gridline_thickness));
        mGridlinePaint.setStyle(Paint.Style.STROKE);
        mGridlinePaint.setPathEffect(new DashPathEffect(new float[] { 20, 10 }, 0));

        mGridlineLabelPaint.setAntiAlias(true);
        mGridlineLabelPaint.setColor(res.getColor(R.color.gridline));
        mGridlineLabelPaint.setTextSize(res.getDimension(R.dimen.gridline_label_font_size));
        mGridlineLabelPaint.setTextAlign(Paint.Align.LEFT);

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

        mYRange = yRange;
        mYMapper = new RangeMapper(yRange, mVBoundsRange);

        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(res.getColor(R.color.line_chart_fill));
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

        drawGridlines(canvas);

        // Draw shadow first.
        canvas.translate(0, mShadowOffset);
        canvas.drawPath(mPath, mShadowPaint);
        canvas.translate(0, -mShadowOffset);

        // Draw the path and shade.
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath(mShadePath, mFillPaint);

        // Draw circle at current price.
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(b.right, y, 5f, mPaint);
    }

    private void drawGridlines(Canvas canvas) {
        int excess = (int) mYRange.start() % GRIDLINE_SPACING;
        int gridline = (int) mYRange.start() - excess + GRIDLINE_SPACING;
        while (gridline < mYRange.end()) {
            float y = mYMapper.get(gridline);

            mGridlinePath.reset();
            mGridlinePath.moveTo(mDrawBounds.left, y);
            mGridlinePath.lineTo(mDrawBounds.right, y);

            canvas.drawPath(mGridlinePath, mGridlinePaint);

            // Value label.
            canvas.drawText(String.valueOf(gridline), mDrawBounds.left, y, mGridlineLabelPaint);

            gridline += GRIDLINE_SPACING;
        }
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
