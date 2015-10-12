package com.nsak.android;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

/**
 * @author Vlad Namashko.
 */
public class AppLifecycleHandler implements Application.ActivityLifecycleCallbacks {

    public static int sActive = 0;

    public static boolean isActive() {
        return sActive > 0;
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.i("Tracking Activity Stopped", activity.getLocalClassName());
        sActive--;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        Log.i("Tracking Activity Started", activity.getLocalClassName());
        sActive++;
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.i("Tracking Activity SaveInstanceState", activity.getLocalClassName());
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.i("Tracking Activity Resumed", activity.getLocalClassName());
        sActive++;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.i("Tracking Activity Paused", activity.getLocalClassName());
        sActive--;
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.i("Tracking Activity Destroyed", activity.getLocalClassName());
        sActive--;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.i("Tracking Activity Created", activity.getLocalClassName());
        sActive++;
    }
}
