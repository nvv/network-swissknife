package com.nsak.android;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import com.mikepenz.iconics.typeface.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class BaseActivity extends ActionBarActivity {

    protected Drawer.Result mDrawer;
    protected Toolbar mToolbar;

    protected void setupToolbarAndDrawer(){
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDrawer = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(FontAwesome.Icon.faw_home).withBadge("99").withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.title_activity_scan_network).withIcon(FontAwesome.Icon.faw_wifi).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.title_activity_wifi_anlayzer).withIcon(FontAwesome.Icon.faw_wifi).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_custom).withIcon(FontAwesome.Icon.faw_eye).withBadge("6").withIdentifier(4),
                        new SectionDrawerItem().withName(R.string.drawer_item_settings),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_help).withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_open_source).withIcon(FontAwesome.Icon.faw_question).setEnabled(false),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_contact).withIcon(FontAwesome.Icon.faw_github).withBadge("12+").withIdentifier(99)
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        View currentFocus = BaseActivity.this.getCurrentFocus();
                        if (currentFocus != null) {
                            InputMethodManager inputMethodManager = (InputMethodManager) BaseActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                        }
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l, IDrawerItem iDrawerItem) {
                        switch (iDrawerItem.getIdentifier()) {
                            case 1:
                                startActivity(new Intent(BaseActivity.this, Main.class));
                                break;
                            case 2:
                                startActivity(new Intent(BaseActivity.this, NetworkScanActivity.class));
                                break;
                            case 3:
                                startActivity(new Intent(BaseActivity.this, WifiAnlayzerActivity.class));
                                break;
                            default:
                                Toast.makeText(BaseActivity.this, "Navigate to id=" + iDrawerItem.getIdentifier(), Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .build();
    }


}
