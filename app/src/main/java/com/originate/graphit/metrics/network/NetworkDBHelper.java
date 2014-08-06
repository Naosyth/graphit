package com.originate.graphit.metrics.network;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.originate.graphit.metrics.MetricDBHelper;

public class NetworkDBHelper extends MetricDBHelper<NetworkEntry> {
    public static final String TABLE_NETWORK = "network";
    public static final String KEY_TIME = "_id";
    public static final String KEY_DOWN = "down";
    public static final String KEY_UP = "up";

    public NetworkDBHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NETWORK_TABLE = "CREATE TABLE " + TABLE_NETWORK + "("
                + KEY_TIME + " INTEGER PRIMARY KEY,"
                + KEY_DOWN + " REAL,"
                + KEY_UP + " REAL" + ")";
        db.execSQL(CREATE_NETWORK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NETWORK);
        onCreate(db);
    }

    @Override
    public NetworkEntry instantiateEntry() {
        return new NetworkEntry();
    }

    @Override
    public String getTable() {
        return TABLE_NETWORK;
    }

    @Override
    public String getIDKey() {
        return KEY_TIME;
    }
}
