package com.nsak.android.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.nsak.android.R;
import com.nsak.android.fragments.intf.NetworkScanActivityInterface;
import com.nsak.android.network.data.TracerouteData;
import com.nsak.android.network.utils.NetworkUtils;
import com.nsak.android.utils.CommandLineUtils;

import rx.Observable;

/**
 * @author Vlad Namashko
 */

public class TracerouteFragment extends CommonResultsFragment {

    @Override
    protected void updateToolbar() {
        super.updateToolbar();
        ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(R.string.traceroute);
    }

    @Override
    protected Observable<CommandLineUtils.CommandLineCommandOutput> getCommand() {
        return NetworkUtils.tracerouteCommand(mSelectedHost.ipAddress);
    }

    @Override
    protected void onOutputReceived(CommandLineUtils.CommandLineCommandOutput output) {
        TracerouteData data = (TracerouteData) output.mData;
        if (data != null && data.getLevel() != 0) {
            mAdapter.addItem(data.getLevel() + " " + data.getIpAddress() + " " + data.getHost());
        } else {
            mAdapter.addItem(output.outputLine);
        }
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }

}
