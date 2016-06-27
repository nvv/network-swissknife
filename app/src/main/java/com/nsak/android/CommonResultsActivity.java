package com.nsak.android;

import android.os.Bundle;

import com.nsak.android.fragments.CommonResultsFragment;

/**
 * @author Vlad Namashko.
 */
public class CommonResultsActivity extends BaseDrawerActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int command = getIntent().getIntExtra(CommonResultsFragment.EXTRA_COMMAND, CommonResultsFragment.EXTRA_COMMAND_PING);
        CommonResultsFragment fragment = CommonResultsFragment.newInstance(command);
        setContentView(fragment);
    }

}
