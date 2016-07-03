package com.nsak.android.db;

import android.database.sqlite.SQLiteDatabase;

import static com.nsak.android.db.HistoryDbAdapter.Columns.*;

/**
 * @author Vlad Namashko
 */

public class HistoryDbAdapter {

    public static final String TABLE_NAME = "history";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
            + TYPE + " INT NOT NULL,"
            + HISTORY + " TEXT NOT NULL,"
            + EXTRA + " TEXT)";

    public static void createTable(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    public static final class Columns {
        public static final String TYPE = "type";
        public static final String HISTORY = "history";
        public static final String EXTRA = "extra";
    }
}
