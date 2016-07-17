package com.nsak.android.db;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nsak.android.App;

import static com.nsak.android.db.IspInfoDbAdapter.Columns.*;;

/**
 * @author Vlad Namashko
 */
public class IspInfoDbAdapter {

    private static final int HALF_HOUR = 1800000;

    public static final String TABLE_NAME = "isp_info";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + IP_ADDRESS + " TEXT NOT NULL, "
            + LAST_DISCOVERED + " INT NOT NULL, "
            + INFO + " TEXT NOT NULL);";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static IspInfo getValidIspInfo(String ip) {
        IspInfo info = getInfoForIp(ip);
        return info != null && info.lastDiscovered > System.currentTimeMillis() - HALF_HOUR ? info : null;
    }

    private static IspInfo getInfoForIp(String ip) {

        SQLiteDatabase database = App.sInstance.getDatabase();

        Cursor cursor = database.query(TABLE_NAME, new String[] { LAST_DISCOVERED, INFO } ,
                IP_ADDRESS + " = '" + ip + "'", null, null, null, null);
        if (cursor.moveToFirst()) {
            int idxLastDiscovered = cursor.getColumnIndex(LAST_DISCOVERED);
            int idxInfo = cursor.getColumnIndex(INFO);

            IspInfo ispInfo = new IspInfo();
            ispInfo.ipAddress = ip;
            ispInfo.lastDiscovered = cursor.getLong(idxLastDiscovered);
            ispInfo.ipInfoJson = cursor.getString(idxInfo);

            return ispInfo;
        }
        cursor.close();

        return null;
    }

    public static void saveIspInfo(String ip, String jsonInfo) {

        SQLiteDatabase database = App.sInstance.getDatabase();

        IspInfo info = getInfoForIp(ip);
        ContentValues values = new ContentValues();
        if (info != null) {
            values.put(LAST_DISCOVERED, System.currentTimeMillis());
            values.put(INFO, jsonInfo);
            database.update(TABLE_NAME, values, IP_ADDRESS + "='" + ip + "'", null);
            return;
        }

        values.put(IP_ADDRESS, ip);
        values.put(INFO, jsonInfo);
        values.put(LAST_DISCOVERED, System.currentTimeMillis());

        database.insert(TABLE_NAME, null, values);
    }

    public static final class Columns {
        public static final String IP_ADDRESS = "ip_address";
        public static final String LAST_DISCOVERED = "last_discovered";
        public static final String INFO = "info";
    }

    public static class IspInfo {
        public String ipAddress;
        public long lastDiscovered;
        public String ipInfoJson;
    }
}
