package com.macduy.games.fstock;

import android.content.res.Resources;
import android.graphics.BlurMaskFilter;
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
import com.macduy.games.fstock.graph.SimpleRange;

public class StockPriceChartDrawable extends Drawable {
    // TODO: Unharcode these.
    private static final int GRIDLINE_SPACING = 50;
    private static final int TIMESPAN = 100;

    // Data range.
    private final Range mYRange;
    private final Range mXRange;

    // Draw ranges for drawing the chart itself.
    private final Range mChartVBound;
    private final Range mChartHBound;
    private final RangeMapper mYMapper;
    private final RangeMapper mXMapper;

    private final int mShadowOffset;
    private final int mChartPadding;
    private final int mGridlineLabelOffset;
    private final int mChartLeftPaddingExtra;

    private StockPrice mStockPrice;
    private Paint mLinePaint = new Paint();
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
        mShadowOffset = res.getDimensionPixelOffset(R.dimen.line_chart_shadow_offset);
        mChartPadding = res.getDimensionPixelSize(R.dimen.line_chart_padding);
        mGridlineLabelOffset = res.getDimensionPixelOffset(R.dimen.gridline_label_offset);
        mChartLeftPaddingExtra = res.getDimensionPixelSize(R.dimen.line_chart_left_padding_extra);

        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(res.getColor(R.color.line_chart_line));
        mLinePaint.setStrokeWidth(res.getDimensionPixelSize(R.dimen.line_chart_thickness));
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeCap(Paint.Cap.ROUND);

        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(res.getColor(R.color.line_chart_shadow));
        mShadowPaint.setStrokeWidth(mLinePaint.getStrokeWidth());
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

        mYRange = yRange;
        // TODO: Make this based on time.
        mXRange = new SimpleRange(0f, (float) TIMESPAN);

        mChartVBound = new Range() {
            @Override public float start() { return mDrawBounds.bottom; }
            @Override public float end() { return mDrawBounds.top; }
        };

        mChartHBound = new Range() {
            @Override public float start() { return mDrawBounds.left; }
            @Override public float end() { return mDrawBounds.right; }
        };

        mYMapper = new RangeMapper(yRange, mChartVBound);
        mXMapper = new RangeMapper(mXRange, mChartHBound);

        mFillPaint.setAntiAlias(true);
        mFillPaint.setColor(res.getColor(R.color.line_chart_fill));
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        mDrawBounds.set(bounds);
        mDrawBounds.inset(mChartPadding, mChartPadding);

        // Apply extra margin
        mDrawBounds.left += mChartLeftPaddingExtra;

    }

    @Override
    public void draw(Canvas canvas) {
        Rect b = mDrawBounds;
        mPath.reset();
        mShadePath.reset();

        int i = 0;

        mShadePath.moveTo(b.left, b.bottom);
        float x, y = 0;
        for (float price : mStockPrice) {
            x = mXMapper.get(i + mOffset);
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
        mLinePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath, mLinePaint);
        canvas.drawPath(mShadePath, mFillPaint);

        // Draw circle at current price.
        mLinePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(b.right, y, 5f, mLinePaint);
    }

    private void drawGridlines(Canvas canvas) {
        Rect b = getBounds();
        int excess = (int) mYRange.start() % GRIDLINE_SPACING;
        int gridline = (int) mYRange.start() - excess + GRIDLINE_SPACING;
        while (gridline < mYRange.end()) {
            float y = mYMapper.get(gridline);

            mGridlinePath.reset();
            mGridlinePath.moveTo(b.left, y);
            mGridlinePath.lineTo(b.right, y);

            canvas.drawPath(mGridlinePath, mGridlinePaint);

            // Value label.
            canvas.drawText(String.valueOf(gridline),
                    b.left + mGridlineLabelOffset,
                    y - mGridlineLabelOffset,
                    mGridlineLabelPaint);

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
