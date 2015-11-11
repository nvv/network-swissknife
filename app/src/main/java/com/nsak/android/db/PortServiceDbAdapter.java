package com.nsak.android.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nsak.android.App;
import com.nsak.android.network.Port;

import java.util.LinkedList;
import java.util.List;

import static com.nsak.android.db.PortServiceDbAdapter.Columns.*;

/**
 * @author Vlad Namashko.
 */
public class PortServiceDbAdapter {

    private static final String TABLE_NAME = "port_service";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + Columns.PORT + " INT NOT NULL, "
            + Columns.SERVICE + " TEXT NOT NULL, "
            + Columns.DESCRIPTION + " TEXT NOT NULL, "
            + Columns.TYPE + " INT NOT NULL, "
            + Columns.SCAN_REQUIRED + " INT NOT NULL);"
            + "CREATE INDEX port_index on port_service (port)";

    private static final String DATA_FILE_NAME = "port_service_data";

    public static void createTable(SQLiteDatabase db) {
        DbAdapterTools.createTableLoadData(db, CREATE_TABLE, DATA_FILE_NAME);
    }

    public static String getService(int port) {
        return DbAdapterTools.getField(port, TABLE_NAME, PORT, SERVICE);
    }

    public static List<Port> getPortsForScan() {
        SQLiteDatabase database = App.sInstance.getDatabase();

        List<Port> ports = new LinkedList<>();

        Cursor cursor = database.query(TABLE_NAME, null, SCAN_REQUIRED + " = 1", null, null, null, null);
        int idxPort = cursor.getColumnIndex(PORT);
        int idxService = cursor.getColumnIndex(SERVICE);
        while (cursor.moveToNext()) {
            ports.add(new Port(cursor.getInt(idxPort), cursor.getString(idxService)));
        }

        cursor.close();

        return ports;
    }

    public static final class Columns {
        public static final String PORT = "port";
        public static final String SERVICE = "service";
        public static final String DESCRIPTION = "description";
        public static final String TYPE = "type";
        public static final String SCAN_REQUIRED = "scan_required";
    }
}
