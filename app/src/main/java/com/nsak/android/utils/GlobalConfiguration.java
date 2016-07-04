package com.nsak.android.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.nsak.android.App;

/**
 * @author Vlad Namashko
 */

public class GlobalConfiguration {

    public static final float DPI;

    static {
        Context context = App.sInstance;
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        DPI = metrics.density;
    }

    public static int getDimensionSize(int size) {
        return (int) (size * DPI);
    }
}
