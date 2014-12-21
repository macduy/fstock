package com.macduy.games.fstock;

import java.util.Iterator;

public class StockPrice implements Iterable<Float> {
    private static final int MAX = 100;

    private float[] mData = new float[MAX];
    private int mPointer = MAX;

    public void pushPrice(float price) {
        mData[mPointer % MAX] = price;
        mPointer++;
    }

    public float getLatest() {
        return mData[((mPointer - 1) % MAX)];
    }

    public void fill(float price) {
        for (int i = 0; i < MAX; i++) {
            mData[i] = price;
        }
    }

    public void reset(float defaultValue) {
        mPointer = MAX;
        fill(defaultValue);
    }

    @Override
    public Iterator<Float> iterator() {
        return new Iterator<Float>() {
            private int max = mPointer;
            private int pointer = mPointer - MAX;

            @Override
            public boolean hasNext() {
                return pointer < max;
            }

            @Override
            public Float next() {
                float data = mData[pointer % MAX];
                pointer++;
                return data;
            }

            @Override
            public void remove() {
                // Not supported.
            }
        };
    }
}
