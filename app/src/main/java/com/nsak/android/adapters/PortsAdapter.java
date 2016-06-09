package com.nsak.android.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.R;
import com.nsak.android.network.Port;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Vlad Namashko.
 */
public class PortsAdapter extends RecyclerView.Adapter<PortsAdapter.ItemViewHolder> {

    protected final List<Port> mItems = new ArrayList<>();

    public void addItem(Port item) {
/*
        int pos = 0;
        for (; pos < mItems.size(); pos++) {
            if (item.ipAddressInt <= mItems.get(pos).ipAddressInt) {
                break;
            }
        }
*/
        mItems.add(item);
        notifyItemInserted(mItems.size());
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_port, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final Port host = mItems.get(position);
        holder.portNum.setText(Integer.toString(host.port));
        holder.portService.setText(host.service);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @InjectView(R.id.port_num)
        TextView portNum;
        @InjectView(R.id.port_service)
        TextView portService;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }
}