package com.nsak.android;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.nsak.android.core.Settings;
import com.nsak.android.core.ThreadPool;
import com.nsak.android.network.NetworkConnectionManager;
import com.nsak.android.network.wifi.WifiManager;

/**
 * @author Vlad Namashko.
 */
public class App extends Application {

    public static App sInstance;

    private DatabaseHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    private WifiManager mWifiManager;
    private ThreadPool mThreadPool;
    private Settings mSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        //NetworkConnectionManager.init();
        registerActivityLifecycleCallbacks(new AppLifecycleHandler());

        mDatabaseHelper = new DatabaseHelper(this);

        mSettings = new Settings();
        mWifiManager = new WifiManager();
        mThreadPool = new ThreadPool();
    }

    @Override
    public void onTerminate() {
        NetworkConnectionManager.shutdown();
        if (mDatabase != null) {
            mDatabase.close();
        }
        super.onTerminate();
    }

    public WifiManager getWifiManager() {
        mWifiManager.initWifiManager();
        return mWifiManager;
    }

    public SQLiteDatabase getDatabase() {
        if (mDatabase == null || !mDatabase.isOpen()) {
            mDatabase = mDatabaseHelper.getWritableDatabase();
        }

        return mDatabase;
    }

    public ThreadPool getThreadPool() {
        return mThreadPool;
    }

    public Settings getSettings() {
        return mSettings;
    }
}
