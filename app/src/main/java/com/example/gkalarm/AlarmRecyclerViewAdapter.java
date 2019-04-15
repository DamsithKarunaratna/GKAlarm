package com.example.gkalarm;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gkalarm.AlarmListFragment.OnListFragmentInteractionListener;
import com.example.gkalarm.data.AlarmData.AlarmItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link AlarmItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class AlarmRecyclerViewAdapter extends RecyclerView.Adapter<AlarmRecyclerViewAdapter.AlarmItemViewHolder> {

    private final List<AlarmItem> mValues;
    private final OnListFragmentInteractionListener mListener;

    public AlarmRecyclerViewAdapter(List<AlarmItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public AlarmItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_alarm_item, parent, false);
        return new AlarmItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AlarmItemViewHolder holder, final int position) {

        final AlarmItem currentItem = mValues.get(position);
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(String.valueOf(currentItem.id));
        holder.mNameView.setText(currentItem.alarmName);
        holder.mTimeView.setText(currentItem.alarmTime);

        holder.mDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onDeleteClicked(holder.mItem, currentItem.id);
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class AlarmItemViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mIdView;
        public final TextView mNameView;
        public final TextView mTimeView;
        public ImageView mDeleteImage;
        public AlarmItem mItem;

        public AlarmItemViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.tv_alarm_id);
            mNameView = view.findViewById(R.id.tv_alarm_name);
            mTimeView = view.findViewById(R.id.tv_alarm_time);
            mDeleteImage = view.findViewById(R.id.image_delete);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
