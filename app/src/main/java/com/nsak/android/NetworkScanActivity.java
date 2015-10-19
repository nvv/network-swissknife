package com.nsak.android;

import android.app.Fragment;
import android.os.Bundle;

import com.nsak.android.fragments.NetworkScanFragment;
import com.nsak.android.fragments.intf.NetworkScanActivityInterface;

/**
 * @author Vlad Namashko.
 */
public class NetworkScanActivity extends BaseDrawerActivity implements NetworkScanActivityInterface {

    public static final String ARG_SELECTED_HOST = "arg_selected_host";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new NetworkScanFragment());
    }

    @Override
    public void setViewToolbar(int layout, boolean setBackArrow) {
        setToolbar(layout, setBackArrow);
    }

    @Override
    public void replaceFragment(Fragment fragment) {
        setContentViewReplace(fragment);
    }
}
