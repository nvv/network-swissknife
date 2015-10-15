package com.nsak.android.adapters;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.NetworkScanActivity;
import com.nsak.android.R;
import com.nsak.android.event.HostSelectedEvent;
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

        mItems.add(pos, item);

        notifyItemInserted(pos);
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
        holder.hostName.setText(host.hostname);
        holder.hostMac.setText(host.macAddress);
        holder.hostVendor.setText(host.nicVendor);
        holder.bindedHost = host;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
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
            mHandler.sendMessage(Message.obtain(null, NetworkScanActivity.MSG_HOST_SELECTED, new HostSelectedEvent(bindedHost)));
        }
    }
}
