package com.nsak.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import com.nsak.android.db.HistoryDbAdapter;
import com.nsak.android.db.HostDbAdapter;
import com.nsak.android.db.IspInfoDbAdapter;
import com.nsak.android.db.NetworkDbAdapter;
import com.nsak.android.db.PortServiceDbAdapter;
import com.nsak.android.db.VendorDbAdapter;

/**
 * @author Vlad Namashko.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "network_db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        VendorDbAdapter.createTable(db);
        PortServiceDbAdapter.createTable(db);
        NetworkDbAdapter.createTable(db);
        HostDbAdapter.createTable(db);
        HistoryDbAdapter.createTable(db);
        IspInfoDbAdapter.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT >= 16) {
            db.setForeignKeyConstraintsEnabled(true);
        }
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

}
