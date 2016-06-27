package com.nsak.android.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.R;
import com.nsak.android.utils.GlobalConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Namashko
 */

public class CommonResultAdapter extends RecyclerView.Adapter<CommonResultAdapter.ItemViewHolder> {

    protected final List<String> mItems = new ArrayList<>();

    public void addItem(String textLine) {
        mItems.add(textLine);
        notifyItemInserted(mItems.size());
    }

    @SuppressWarnings("deprecation")
    @Override
    public CommonResultAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView view = new TextView(parent.getContext());
        view.setTextAppearance(parent.getContext(), R.style.CommonResultFont);
        view.setPadding((int) (12 * GlobalConfiguration.DPI), (int) (4 * GlobalConfiguration.DPI),
                (int) (12 * GlobalConfiguration.DPI), (int) (4 * GlobalConfiguration.DPI));
        return new CommonResultAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CommonResultAdapter.ItemViewHolder holder, int position) {
        holder.textLine.setText(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void clearItems() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textLine;

        public ItemViewHolder(View view) {
            super(view);
            textLine = (TextView) view;
        }

        @Override
        public void onClick(View v) {

        }
    }
}