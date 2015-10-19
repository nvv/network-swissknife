package com.nsak.android;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.nsak.android.fragments.PortScanFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * @author Vlad Namashko.
 */
public class CommonResultsActivity extends BaseDrawerActivity {

    @InjectView(R.id.common_results_title)
    TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_results_view);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ButterKnife.inject(this);

        //title.setText(getIntent().getStringExtra(Constants.EXTRA_HOST_TO_SCAN));

        PortScanFragment fragment = new PortScanFragment();
        fragment.setArguments(getIntent().getExtras());
        //getSupportFragmentManager().beginTransaction().add(R.id.content_layout, fragment).commit();
    }

}
