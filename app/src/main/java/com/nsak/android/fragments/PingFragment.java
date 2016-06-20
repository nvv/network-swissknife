package com.nsak.android.fragments;

import android.widget.TextView;

import com.nsak.android.R;
import com.nsak.android.network.data.PingData;
import com.nsak.android.network.data.TracerouteData;
import com.nsak.android.network.utils.NetworkUtils;
import com.nsak.android.utils.CommandLineUtils;

import rx.Observable;

/**
 * @author Vlad Namashko
 */

public class PingFragment extends CommonResultsFragment {

    @Override
    protected void updateToolbar() {
        super.updateToolbar();
        ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(R.string.ping_host);
    }

    @Override
    protected Observable<CommandLineUtils.CommandLineCommandOutput> getCommand() {
        return NetworkUtils.pingCommand(mSelectedHost.ipAddress);
    }

    @Override
    protected void onOutputReceived(CommandLineUtils.CommandLineCommandOutput output) {
        PingData data = (PingData) output.mData;
        if (data != null) {
            mAdapter.addItem(data.getTime() + " " + data.getSize());
        } else {
            mAdapter.addItem(output.outputLine);
        }
        mRecyclerView.smoothScrollToPosition(mAdapter.getItemCount());
    }

}
