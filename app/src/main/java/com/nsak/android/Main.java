package com.nsak.android;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.PopupWindow;

import com.nsak.android.ui.widget.PopupActionWindow;

public class Main extends BaseDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

//            PopupActionWindow popupWindow = new PopupActionWindow(findViewById(R.id.content_view), R.layout.host_actions);
//            popupWindow.setAnimationStyle(R.style.Animation);
//            popupWindow.showAtLocation(findViewById(R.id.content_view), Gravity.NO_GRAVITY, 900, 100);
//            popupWindow.showAsDropDown(findViewById(R.id.content_view));

            return false;
        }

        return super.onOptionsItemSelected(item);
    }

}
