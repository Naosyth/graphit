package com.originate.graphit.metrics.battery;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.originate.graphit.metrics.MetricDBHelper;

public class BatteryDBHelper extends MetricDBHelper {
    public static final String TABLE_BATTERY = "battery";
    public static final String KEY_PERCENT = "percent";
    public static final String KEY_TIME = "_id";
    public static final String KEY_CRITICAL = "critical";

    public BatteryDBHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BATTERY_TABLE = "CREATE TABLE " + TABLE_BATTERY + "("
                + KEY_TIME + " INTEGER PRIMARY KEY,"
                + KEY_PERCENT + " INTEGER,"
                + KEY_CRITICAL + " INTEGER" + ")";
        db.execSQL(CREATE_BATTERY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATTERY);
        onCreate(db);
    }

    @Override
    public BatteryEntry instantiateEntry() {
        return new BatteryEntry();
    }

    @Override
    public String getTable() {
        return TABLE_BATTERY;
    }

    @Override
    public String getIDKey() {
        return KEY_TIME;
    }

    public int collapseOldEntries(long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numCollapsed = db.delete(TABLE_BATTERY, KEY_TIME + " <= ? AND " + KEY_CRITICAL + " = 0", new String[] { String.valueOf(time) });
        db.close();
        return numCollapsed;
    }
}
