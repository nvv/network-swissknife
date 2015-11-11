package com.nsak.android.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.nsak.android.App;
import com.nsak.android.network.wifi.WifiInfo;

import static com.nsak.android.db.NetworkDbAdapter.Columns.*;

/**
 * @author Vlad Namashko.
 */
public class NetworkDbAdapter {

    public static final String TABLE_NAME = "networks";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + IP_ADDRESS + " INT NOT NULL, "
            + MASK_CIDR + " INT NOT NULL, "
            + SSID + " TEXT NOT NULL, "
            + BSSID + " TEXT NOT NULL, "
            + LAST_SCANNED + " INT NOT NULL, "
            + FIRST_DISCOVERED + " INT NOT NULL);"
            + "CREATE INDEX network_index on networks ( " + IP_ADDRESS + "," + MASK_CIDR + "," + SSID + ")";


    public static void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static long saveNetwork(WifiInfo wifiInfo) {
        SQLiteDatabase database = App.sInstance.getDatabase();

        long networkId =  findNetwork(database, wifiInfo);
        ContentValues values = new ContentValues();
        if (networkId != -1) {
            values.put(LAST_SCANNED, System.currentTimeMillis());
            database.update(TABLE_NAME, values, ID + "=" + networkId, null);
            return networkId;
        }

        values.put(IP_ADDRESS, wifiInfo.getIpAddress());
        values.put(MASK_CIDR, wifiInfo.getMaskCidr());
        values.put(SSID, wifiInfo.getSsid());
        values.put(BSSID, wifiInfo.getBssid());
        values.put(FIRST_DISCOVERED, System.currentTimeMillis());
        values.put(LAST_SCANNED, System.currentTimeMillis());

        return database.insert(TABLE_NAME, null, values);
    }

    public static int deleteNetwork(long networkId) {
        SQLiteDatabase database = App.sInstance.getDatabase();
        return database.delete(TABLE_NAME, ID + "=" + networkId, null);
    }

    public static long findNetwork(SQLiteDatabase database, WifiInfo wifiInfo) {
        return findNetwork(database, wifiInfo.getIpAddress(), wifiInfo.getMaskCidr(), wifiInfo.getSsid(), wifiInfo.getBssid());
    }

    public static long findNetwork(SQLiteDatabase database, int ip, int mask, String ssid, String bssid) {
        try {
            return DatabaseUtils.longForQuery(database,
                    String.format("SELECT DISTINCT " + ID + " FROM " + TABLE_NAME + " WHERE " + IP_ADDRESS + "=%1$d AND " +
                            MASK_CIDR + "=%2$d AND " + SSID + "='%3$s' AND " + BSSID + "='%4$s'", ip, mask, ssid, bssid), null);
        } catch (Exception e) {
            return -1;
        }
    }

    public static String print() {
        String value = "Unknown";

        SQLiteDatabase database = App.sInstance.getDatabase();
        Cursor cursor = database.query(TABLE_NAME, null,null, null, null, null, null);
        if (cursor.moveToFirst()) {
            System.out.println(cursor.getString(0));
        }
        cursor.close();

        return value;
    }

    public static final class Columns {
        public static final String ID = "network_id";
        public static final String IP_ADDRESS = "ip_address";
        public static final String MASK_CIDR = "mask_cidr";
        public static final String SSID = "hotspot_ssid";
        public static final String BSSID = "hotspot_bssid";
        public static final String FIRST_DISCOVERED = "network_first_discovered";
        public static final String LAST_SCANNED = "network_last_scanned";
    }
}
