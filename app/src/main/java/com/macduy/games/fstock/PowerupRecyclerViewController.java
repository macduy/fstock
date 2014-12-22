package com.macduy.games.fstock;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.macduy.games.fstock.powerup.Powerup;

import java.util.List;

/**
 * Maps powerups to a RecyclerView.
 */
public class PowerupRecyclerViewController {
    private final Context mContext;
    private final RecyclerView mView;
    private final LinearLayoutManager mLayoutManager;
    private final RecyclerView.Adapter mAdapter;
    private final Listener mListener;

    public PowerupRecyclerViewController(RecyclerView view, List<Powerup> powerups,
            Listener listener) {
        mContext = view.getContext();
        mView = view;
        mListener = listener;

        // Set up layout manager.
        mLayoutManager = new LinearLayoutManager(mContext);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mView.setLayoutManager(mLayoutManager);

        // Set up adapter.
        mAdapter = new PowerupViewAdapter(powerups);
        mView.setAdapter(mAdapter);
    }

    class PowerupViewAdapter extends RecyclerView.Adapter<PowerupViewHolder> {
        private List<Powerup> mPowerups;

        public PowerupViewAdapter(List<Powerup> powerups) {
            mPowerups = powerups;
        }

        @Override
        public PowerupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView view = new TextView(mContext);
            LayoutParams lp = new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(lp);

            return new PowerupViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PowerupViewHolder holder, int position) {
            final Powerup powerup = mPowerups.get(position);
            holder.mView.setText(powerup.getName());
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onPowerupSelected(powerup);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPowerups.size();
        }
    }

    class PowerupViewHolder extends RecyclerView.ViewHolder {
        public TextView mView;

        public PowerupViewHolder(TextView view) {
            super(view);
            mView = view;
        }
    }

    public interface Listener {
        void onPowerupSelected(Powerup powerup);
    }
}
