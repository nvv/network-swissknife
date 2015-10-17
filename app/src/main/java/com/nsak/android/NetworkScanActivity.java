package com.nsak.android;

import android.os.Bundle;
import android.widget.TextView;

import com.nsak.android.fragments.NetworkScanFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Vlad Namashko.
 */
public class NetworkScanActivity extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setToolbar(R.layout.toolbar_network_scan);
        setContentView(new NetworkScanFragment());
    }

}
