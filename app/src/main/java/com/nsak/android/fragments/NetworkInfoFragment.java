package com.nsak.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nsak.android.R;
import com.nsak.android.fragments.intf.ActivityInterface;

/**
 * @author Vlad Namashko
 */

public class NetworkInfoFragment extends BaseFragment {

    private View mRootView;
    private View mToolbar;

    private TextView mTitle;
    private View mProgress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_netowrk_info, container, false);

        mToolbar = LayoutInflater.from(getActivity()).inflate(R.layout.toolbar_titled, null);

        mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        mProgress = mToolbar.findViewById(R.id.progress);

        mTitle.setText(R.string.network_info);
        ((ActivityInterface) getActivity()).setViewToolbar(mToolbar);
        return mRootView;
    }

    @Override
    public boolean isBackOnToolbar() {
        return false;
    }

    @Override
    public void onMovedToForeground() {

    }

    @Override
    public void resetViewAndPerformAction(Runnable action) {

    }

}
