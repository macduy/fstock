package com.macduy.games.fstock;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.macduy.games.fstock.money.Holding;

import java.util.List;

/**
 * Maps Holdings to a RecyclerView.
 */
public class HoldingsRecyclerViewController {
    private final Context mContext;
    private final RecyclerView mView;
    private final LinearLayoutManager mLayoutManager;
    private final RecyclerView.Adapter mAdapter;

    public HoldingsRecyclerViewController(RecyclerView view, List<Holding> holdings) {
        mContext = view.getContext();
        mView = view;

        // Set up layout manager.
        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mView.setLayoutManager(mLayoutManager);

        // Set up adapter.
        mAdapter = new HoldingsViewAdapter(holdings);
        mView.setAdapter(mAdapter);
    }

    public void notifyDataSetChanged() {
        mAdapter.notifyDataSetChanged();
    }

    class HoldingsViewAdapter extends RecyclerView.Adapter<HoldingsViewViewHolder> {
        private List<Holding> mHoldings;

        public HoldingsViewAdapter(List<Holding> holdings) {
            mHoldings = holdings;
        }

        @Override
        public HoldingsViewViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            TextView view = new TextView(mContext);
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);

            return new HoldingsViewViewHolder(view);
        }

        @Override
        public void onBindViewHolder(HoldingsViewViewHolder holdingsViewViewHolder, int i) {
            holdingsViewViewHolder.mView.setText("H");
        }

        @Override
        public int getItemCount() {
            return mHoldings.size();
        }
    }

    class HoldingsViewViewHolder extends RecyclerView.ViewHolder {
        public TextView mView;

        public HoldingsViewViewHolder(TextView view) {
            super(view);
            mView = view;
        }

    }
}
