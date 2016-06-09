package com.nsak.android.adapters;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.App;
import com.nsak.android.NetworkScanActivity;
import com.nsak.android.R;
import com.nsak.android.event.HostSelectedEvent;
import com.nsak.android.fragments.NetworkScanFragment;
import com.nsak.android.network.Host;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Vlad Namashko.
 */
public class HostsAdapter extends RecyclerView.Adapter<HostsAdapter.ItemViewHolder> {

    protected final List<Host> mItems = new ArrayList<>();
    protected final SparseArray<Host> mItemsMap = new SparseArray<>();
    protected Handler mHandler;

    public HostsAdapter(Handler handler) {
        mHandler = handler;
    }

    public void addItem(Host item) {

        int pos = 0;
        for (; pos < mItems.size(); pos++) {
            if (item.ipAddressInt <= mItems.get(pos).ipAddressInt) {
                break;
            }
        }

        Host host = mItemsMap.get(item.ipAddressInt);
        if (host != null && host.macAddress.equals(item.macAddress)) {
            host.updateState(item);
            notifyItemChanged(pos);
        } else {
            mItems.add(pos, item);
            mItemsMap.put(item.ipAddressInt, item);
            notifyItemInserted(pos);
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.network_host, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder holder, int position) {
        final Host host = mItems.get(position);
        holder.hostIp.setText(host.ipAddress);
        holder.hostName.setText(host.getName());
        holder.hostMac.setText(host.macAddress);
        holder.hostVendor.setText(host.nicVendor);
        holder.bindedHost = host;

        @SuppressWarnings("deprecation")
        int color = App.sInstance.getResources().getColor(host.isReachable ? R.color.ListItemTextColorDark : R.color.ListItemDisabledTextColor);

        holder.hostIp.setTextColor(color);
        holder.hostName.setTextColor(color);
        holder.hostMac.setTextColor(color);
        holder.hostVendor.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(List<Host> hosts) {
        if (mItems.size() > 0) {
            mItems.clear();
            mItemsMap.clear();
            notifyDataSetChanged();
        }

        for (Host host : hosts) {
            addItem(host);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        @InjectView(R.id.host_ip)
        TextView hostIp;
        @InjectView(R.id.host_name)
        TextView hostName;
        @InjectView(R.id.host_mac)
        TextView hostMac;
        @InjectView(R.id.host_vendor)
        TextView hostVendor;

        Host bindedHost;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mHandler.sendMessage(Message.obtain(null, NetworkScanFragment.MSG_HOST_SELECTED, new HostSelectedEvent(bindedHost, v)));
        }
    }
}
