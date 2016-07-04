package com.nsak.android.fragments;

import android.widget.TextView;

import com.nsak.android.R;
import com.nsak.android.network.data.IspData;
import com.nsak.android.network.utils.NetworkUtils;
import com.nsak.android.utils.CommandLineUtils;

import rx.Observable;

/**
 * @author Vlad Namashko
 */

public class IspFragment extends CommonResultsFragment {

    @Override
    protected void updateToolbar() {
        super.updateToolbar();
        ((TextView) mToolbar.findViewById(R.id.toolbar_title)).setText(R.string.title_network_info);
    }

    @Override
    protected Observable<CommandLineUtils.CommandLineCommandOutput> getCommand() {
        return NetworkUtils.getIspCommand();
    }

    @Override
    protected void onOutputReceived(CommandLineUtils.CommandLineCommandOutput output) {
        /*
        String[] data = output.outputLine.split("\n");
        for (String item : data) {
            mAdapter.addItem(item);
        }
        */
        IspData ispData = (IspData) output.mData;
        mAdapter.addItem(ispData.getKey() + " : " + ispData.getValue());
    }

    @Override
    public void initEditOptions() {
        mAddress = " ";
        doResult();
        hideSettingsIcon();
    }
}
