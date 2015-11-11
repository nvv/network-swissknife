package com.nsak.android.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import com.nsak.android.App;
import com.nsak.android.network.Host;
import com.nsak.android.network.utils.NetworkCalculator;

import java.util.ArrayList;
import java.util.List;

import static com.nsak.android.db.HostDbAdapter.Columns.*;

/**
 * @author Vlad Namashko.
 */
public class HostDbAdapter {

    public static final String TABLE_NAME = "hosts";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + MAC_ADDRESS + " TEXT NOT NULL,"
            + IP_ADDRESS + " INT NOT NULL, "
            + DEVICE_TYPE + " INT NOT NULL, "
            + HOSTNAME + " TEXT, "
            + NETBIOS_NAME + " TEXT, "
            + VENDOR + " TEXT, "
            + FIRST_DISCOVERED + " INT NOT NULL,"
            + LAST_SEEN + " INT NOT NULL, "
            + NETWORK_ID + " INT NOT NULL, "
            + "CONSTRAINT network_fk " +
            "FOREIGN KEY (" + NETWORK_ID + ") REFERENCES " + NetworkDbAdapter.TABLE_NAME + "(" + NetworkDbAdapter.Columns.ID +") " +
            "ON DELETE CASCADE );";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }


    public static void saveHosts(long networkId, List<Host> hosts) {
        if (networkId == -1 || hosts.size() == 0) {
            return;
        }

        SQLiteDatabase database = App.sInstance.getDatabase();

        ContentValues values = new ContentValues();
        for (Host host : hosts) {
            values.clear();

            values.put(DEVICE_TYPE, host.deviceType);
            values.put(HOSTNAME, host.hostname);
            values.put(NETBIOS_NAME, host.netBiosName);
            values.put(LAST_SEEN, host.discoveredTime);

            if (isHostExists(database, networkId, host.ipAddressInt, host.macAddress)) {
                database.update(TABLE_NAME, values, String.format(NETWORK_ID + "=%1$d AND " +
                        IP_ADDRESS + "=%2$d AND " + MAC_ADDRESS + "='%3$s'", networkId, host.ipAddressInt, host.macAddress), null);
            } else {
                values.put(MAC_ADDRESS, host.macAddress);
                values.put(IP_ADDRESS, host.ipAddressInt);
                values.put(VENDOR, host.nicVendor);
                values.put(FIRST_DISCOVERED, host.discoveredTime);
                values.put(LAST_SEEN, host.discoveredTime);
                values.put(NETWORK_ID, networkId);
                database.insert(TABLE_NAME, null, values);
            }
        }
    }

    public static List<Host> getHosts(long networkId) {
        SQLiteDatabase database = App.sInstance.getDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, networkId == -1 ? null : NETWORK_ID + "=" + networkId, null, null, null, null, null);
        
        List<Host> hosts = new ArrayList<>();
        if (cursor == null) {
            return hosts;    
        }

        int idxIpAddress = cursor.getColumnIndex(IP_ADDRESS);
        int idxDeviceType = cursor.getColumnIndex(DEVICE_TYPE);
        int idxHostName = cursor.getColumnIndex(HOSTNAME);
        int idxNetbiosName = cursor.getColumnIndex(NETBIOS_NAME);
        int idxMacAddress = cursor.getColumnIndex(MAC_ADDRESS);
        int idxVendor = cursor.getColumnIndex(VENDOR);
        int idxFirstDiscovered = cursor.getColumnIndex(FIRST_DISCOVERED);
        int idxLastSeen = cursor.getColumnIndex(LAST_SEEN);

        while (cursor.moveToNext()) {
            Host host = new Host();
            host.ipAddressInt = cursor.getInt(idxIpAddress);
            host.ipAddress = NetworkCalculator.ipIntToString(host.ipAddressInt);
            host.deviceType = cursor.getInt(idxDeviceType);
            host.hostname = cursor.getString(idxHostName);
            host.netBiosName = cursor.getString(idxNetbiosName);
            host.macAddress = cursor.getString(idxMacAddress);
            host.nicVendor = cursor.getString(idxVendor);
            host.firstDiscovered = cursor.getLong(idxFirstDiscovered);
            host.lastSeen = cursor.getLong(idxLastSeen);
            host.isReachable = false;
            hosts.add(host);
        }

        return hosts;
    }

    public static boolean isHostExists(SQLiteDatabase database, long networkId, int ip, String mac) {
        try {
            return DatabaseUtils.longForQuery(database,
                    String.format("SELECT COUNT(1) FROM " + TABLE_NAME + " WHERE " + NETWORK_ID + "=%1$d AND " +
                            IP_ADDRESS + "=%2$d AND " + MAC_ADDRESS + "='%3$s'", networkId, ip, mac), null) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static final class Columns {
        public static final String IP_ADDRESS = "ip_address";
        public static final String DEVICE_TYPE = "device_type";
        public static final String HOSTNAME = "hostname";
        public static final String NETBIOS_NAME = "netbios_name";
        public static final String MAC_ADDRESS = "mac_address";
        public static final String VENDOR = "nic_vendor";
        public static final String FIRST_DISCOVERED = "first_discovered";
        public static final String LAST_SEEN = "last_seen";
        public static final String NETWORK_ID = "network_id";
    }
}
