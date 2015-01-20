package com.macduy.games.fstock;

import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Iterator;

public class StockData implements Iterable<StockData.Price> {
    private static final int MAX = 100;

    private Price[] mData = new Price[MAX];
    private int mPointer = 0;

    public void pushPrice(long time, float price) {
        mData[mPointer % MAX] = new Price(time, price);
        mPointer++;
    }

    @Nullable public Price getLatest() {
        return mPointer > 0 ? mData[((mPointer - 1) % MAX)] : null;
    }

    public void reset() {
        mPointer = 0;
    }

    /** Iterates over non-null price points. */
    @Override
    public Iterator<Price> iterator() {
        return new Iterator<Price>() {
            private int max = mPointer;
            private int pointer = Math.max(0, mPointer - MAX);

            @Override
            public boolean hasNext() {
                return pointer < max;
            }

            @Override
            public Price next() {
                Price price = mData[pointer % MAX];
                pointer++;
                return price;
            }

            @Override
            public void remove() {
                Log.wtf("StockData", "remove in iterator not supported!");
            }
        };
    }

    /** A single price point. */
    public static class Price {
        public final float price;
        public final long time;

        public Price(long time, float price) {
            this.price = price;
            this.time = time;
        }
    }
}
