package com.nsak.android;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.nsak.android.fragments.BaseFragment;

import java.util.Deque;
import java.util.LinkedList;

/**
 * @author Vlad Namashko
 */
public class BaseDrawerActivity extends AppCompatActivity {

    protected Toolbar mToolbar;
    protected DrawerLayout mDrawerLayout;
    protected NavigationView mNavigationView;
    protected ViewGroup mContentView;

    protected Deque<BaseFragment> mFragments = new LinkedList<>();

    protected ActionBarDrawerToggle mActionBarDrawerToggle;
    protected boolean mIsBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.main_drawer_view);

        mContentView =  (ViewGroup) findViewById(R.id.content_view);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.network_scanner:
                        startActivity(new Intent(BaseDrawerActivity.this, NetworkScanActivity.class));
                        return true;

                    case R.id.wifi_analyzer:
                        return true;

                    default:
                        return true;
                }
            }
        });

        // Initializing Drawer Layout and ActionBarToggle
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar, R.string.openDrawer, R.string.closeDrawer){

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

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

    @Override
    public void onBackPressed() {
        final FragmentManager manager = getFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            mFragments.getFirst().resetViewAndPerformAction(new Runnable() {
                @Override
                public void run() {
                    manager.popBackStack();
                    mFragments.pop();
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
            mIsBackButton = true;
            setBackArrowVisibility(true);
        } else {
            mIsBackButton = false;
            setBackArrowVisibility(false);
            mActionBarDrawerToggle.syncState();
            setTitle(null);
        }
    }

    private void setBackArrowVisibility(boolean value) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(value);
        getSupportActionBar().setDisplayShowHomeEnabled(value);

    }
}
