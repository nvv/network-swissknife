package com.nsak.android.fragments.intf;

import android.app.Fragment;

/**
 * @author Vlad Namashko.
 */
public interface NetworkScanActivityInterface {

    void setViewToolbar(int layout, boolean setBackArrow);

    void replaceFragment(Fragment fragment);
}
