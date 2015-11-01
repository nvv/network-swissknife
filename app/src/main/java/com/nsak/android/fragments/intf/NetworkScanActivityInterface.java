package com.nsak.android.fragments.intf;

import android.view.View;

import com.nsak.android.fragments.BaseFragment;

/**
 * @author Vlad Namashko.
 */
public interface NetworkScanActivityInterface {

    void setViewToolbar(View toolbar);

    void replaceFragment(BaseFragment fragment);
}
