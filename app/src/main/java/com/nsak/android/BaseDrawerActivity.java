package com.nsak.android;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.ValueAnimator;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.nsak.android.fragments.BaseFragment;
import com.nsak.android.fragments.CommonResultsFragment;
import com.nsak.android.fragments.intf.ActivityInterface;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.nsak.android.fragments.CommonResultsFragment.EXTRA_COMMAND;
import static com.nsak.android.fragments.CommonResultsFragment.EXTRA_COMMAND_IP_CALCULATOR;
import static com.nsak.android.fragments.CommonResultsFragment.EXTRA_COMMAND_MY_IP_ISP;
import static com.nsak.android.fragments.CommonResultsFragment.EXTRA_COMMAND_PING;
import static com.nsak.android.fragments.CommonResultsFragment.EXTRA_COMMAND_TRACEROUTE;
import static com.nsak.android.fragments.CommonResultsFragment.EXTRA_COMMAND_WHOIS;

/**
 * @author Vlad Namashko
 */
public class BaseDrawerActivity extends AppCompatActivity implements ActivityInterface {

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 128;

    private final String EXTRA_SELECTED_ITEM_ID = "EXTRA_SELECTED_ITEM_ID";

    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;
    protected NavigationView mNavigationView;
    protected ViewGroup mContentView;

    protected Deque<BaseFragment> mFragments = new LinkedList<>();

    protected ActionBarDrawerToggle mActionBarDrawerToggle;
    protected boolean mIsBackButton;

    private List<Runnable> mPostUiTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main_drawer_view);

        mContentView = (ViewGroup) findViewById(R.id.content_view);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setCheckedItem(getIntent().getIntExtra(EXTRA_SELECTED_ITEM_ID, R.id.wifi_analyzer));
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
 //               if (menuItem.isChecked()) menuItem.setChecked(false);
 //               else menuItem.setChecked(true);

                menuItem.setChecked(!menuItem.isChecked());

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.network_scanner:
                        startActivity(new Intent(BaseDrawerActivity.this, NetworkScanActivity.class).
                                putExtra(EXTRA_SELECTED_ITEM_ID, R.id.network_scanner));
                        return true;

                    case R.id.wifi_analyzer:
                        //startActivity(new Intent(BaseDrawerActivity.this, WifiAnlayzerActivity.class));
                        return false;

                    case R.id.whois:
                        startActivity(new Intent(BaseDrawerActivity.this, CommonResultsActivity.class).
                                putExtra(EXTRA_COMMAND, EXTRA_COMMAND_WHOIS).putExtra(EXTRA_SELECTED_ITEM_ID, R.id.whois));
                        return true;

                    case R.id.traceroute:
                        startActivity(new Intent(BaseDrawerActivity.this, CommonResultsActivity.class).
                                putExtra(EXTRA_COMMAND, EXTRA_COMMAND_TRACEROUTE).putExtra(EXTRA_SELECTED_ITEM_ID, R.id.traceroute));
                        return true;

                    case R.id.ping:
                        startActivity(new Intent(BaseDrawerActivity.this, CommonResultsActivity.class).
                                putExtra(EXTRA_COMMAND, EXTRA_COMMAND_PING).putExtra(EXTRA_SELECTED_ITEM_ID, R.id.ping));
                        return true;

                    case R.id.my_ip:
                        startActivity(new Intent(BaseDrawerActivity.this, NetworkInfoActivity.class));
                        return true;

                    case R.id.ip_calculator:
                        startActivity(new Intent(BaseDrawerActivity.this, CommonResultsActivity.class).
                                putExtra(EXTRA_COMMAND, EXTRA_COMMAND_IP_CALCULATOR).putExtra(EXTRA_SELECTED_ITEM_ID, R.id.ip_calculator));
                        return true;

                    default:
                        return true;
                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar, R.string.openDrawer, R.string.closeDrawer);

        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBackButton) {
                    onBackPressed();
                } else {
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                }
            }
        });

        mActionBarDrawerToggle.syncState();
    }

    protected boolean askPermission(String[] permissions) {

        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }

        final List<String> permissionsRequested = new ArrayList<>();
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                continue;
            }
            permissionsRequested.add(permission);
        }

        if (!permissionsRequested.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsRequested.toArray(
                    new String[permissionsRequested.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    protected void onPermissionsResult(Map<String, Integer> permissions) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {

            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> hash = new HashMap<>();
                for (int i = 0; i < permissions.length; i++) {
                    hash.put(permissions[i], grantResults[i]);
                }
                onPermissionsResult(hash);
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (Runnable runnable : mPostUiTasks) {
            runnable.run();
        }
    }

    @Override
    public void onBackPressed() {
        final FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            mFragments.getFirst().resetViewAndPerformAction(new Runnable() {
                @Override
                public void run() {
                    try {
                        manager.popBackStack();
                    } catch (Exception e) {
                        mPostUiTasks.add(new Runnable() {
                            @Override
                            public void run() {
                                manager.popBackStack();
                            }
                        });
                    }
                    mFragments.pop();
                    if (mFragments.size() == 0) {
                        return;
                    }

                    BaseFragment fragment = mFragments.getFirst();
                    updateToolbarState(fragment);
                    fragment.onMovedToForeground();
                }
            });
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setContentView(View view) {
        mContentView.addView(view, 0);
    }

    @Override
    public void setContentView(int resId) {
        mContentView.addView(LayoutInflater.from(this).inflate(resId, null), 0);
    }

    public void setContentView(BaseFragment fragment) {
        mContentView.removeAllViews();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_view, fragment);
        fragmentTransaction.commit();
        mFragments.push(fragment);
        updateToolbarState(mFragments.getFirst());
    }

    public void setContentViewReplace(BaseFragment fragment) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content_view, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        mFragments.push(fragment);
        updateToolbarState(mFragments.getFirst());
    }

    public void setToolbar(View toolbar) {
        if (mToolbar.getChildCount() > 1) {
            mToolbar.removeViewAt(0);
        }

        mToolbar.addView(toolbar, 0);
    }

    private void updateToolbarState(BaseFragment fragment) {
        if (fragment.isBackOnToolbar()) {
            setBackArrowVisibility(true, !mIsBackButton);
            mIsBackButton = true;
        } else {
            setBackArrowVisibility(false, mIsBackButton);
            mIsBackButton = false;
            mActionBarDrawerToggle.syncState();
            setTitle(null);
        }
    }

    private void setBackArrowVisibility(boolean value, boolean runAnimation) {
        if (runAnimation) {
            ValueAnimator animator = (ValueAnimator) AnimatorInflater.loadAnimator(this,
                    value ? R.animator.action_bar_arrow_in : R.animator.action_bar_arrow_out);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float slideOffset = (Float) valueAnimator.getAnimatedValue();
                    mActionBarDrawerToggle.onDrawerSlide(mDrawerLayout, slideOffset);
                }
            });
            animator.start();
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(value);
            getSupportActionBar().setDisplayShowHomeEnabled(value);
        }
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
