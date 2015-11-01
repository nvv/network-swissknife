package com.nsak.android;

import android.os.Bundle;
import android.view.View;

import com.nsak.android.fragments.BaseFragment;
import com.nsak.android.fragments.NetworkScanFragment;
import com.nsak.android.fragments.intf.NetworkScanActivityInterface;

/**
 * @author Vlad Namashko.
 */
public class NetworkScanActivity extends BaseDrawerActivity implements NetworkScanActivityInterface {

    public static final String ARG_SELECTED_HOST = "arg_selected_host";
    public static final String ARG_SELECTED_ITEM_TOP = "arg_selected_item_top";
    public static final String ARG_SELECTED_ITEM_BOTTOM = "arg_selected_item_bottom";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new NetworkScanFragment());
    }

    @Override
    public void setViewToolbar(View toolbar) {
        setToolbar(toolbar);
    }

    @Override
    public void replaceFragment(BaseFragment fragment) {
        setContentViewReplace(fragment);
    }
}
