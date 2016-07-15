package com.nsak.android;

import android.os.Bundle;

import com.nsak.android.fragments.NetworkInfoFragment;

/**
 * @author Vlad Namashko
 */

public class NetworkInfoActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new NetworkInfoFragment());
    }
}
