package com.nsak.android.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.R;
import com.nsak.android.network.data.IpInfoData;
import com.nsak.android.utils.GlobalConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vlad Namashko
 */

public class IpInfoResultAdapter extends BaseCommonResultAdapter<IpInfoResultAdapter.ItemViewHolder, IpInfoData> {

    protected final List<IpInfoData> mItems = new ArrayList<>();

    public void addItem(IpInfoData textLine) {
        mItems.add(textLine);
        notifyItemInserted(mItems.size());
    }

    @SuppressWarnings("deprecation")
    @Override
    public IpInfoResultAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ip_info_item, parent, false);
        return new IpInfoResultAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        holder.name.setText(mItems.get(position).getName());
        holder.value.setText(mItems.get(position).getValue());
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

        public TextView name;
        public TextView value;

        public ItemViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            value = (TextView) view.findViewById(R.id.value);
        }

        @Override
        public void onClick(View v) {

        }
    }

}
