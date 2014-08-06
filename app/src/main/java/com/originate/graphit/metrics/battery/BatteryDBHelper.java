package com.originate.graphit.metrics.battery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.originate.graphit.metrics.MetricDBHelper;
import com.originate.graphit.metrics.MetricsEntry;

import java.util.ArrayList;
import java.util.List;

public class BatteryDBHelper extends MetricDBHelper {
    public static final String TABLE_BATTERY = "battery";
    public static final String KEY_PERCENT = "percent";
    public static final String KEY_TIME = "_id";
    public static final String KEY_CRITICAL = "critical";

    public BatteryDBHelper(Context context) {
        super(context, BatteryEntry.class);
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

    public List<BatteryEntry> getAllEntries() {
        String selectQuery = "SELECT * FROM " + TABLE_BATTERY;
        List<BatteryEntry> entryList = new ArrayList<BatteryEntry>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BatteryEntry entry = new BatteryEntry();
                entry.setTime((long)cursor.getInt(0));
                entry.setPercentage(cursor.getInt(1));
                entry.setCritical(cursor.getInt(2) > 0);
                entryList.add(entry);
            } while(cursor.moveToNext());
        }
        db.close();
        return entryList;
    }

    public int getEntryCount() {
        String countQuery = "SELECT * FROM " + TABLE_BATTERY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        db.close();
        return count;
    }

    public int updateEntry(BatteryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, entry.getTime());
        values.put(KEY_PERCENT, entry.getPercentage());
        values.put(KEY_CRITICAL, entry.getCritical());
        int numUpdated = db.update(TABLE_BATTERY, values, KEY_TIME + " = ?", new String[] { String.valueOf(entry.getTime()) });
        db.close();
        return numUpdated;
    }

    public int deleteEntry(BatteryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(TABLE_BATTERY, KEY_TIME + " = ?", new String[] { String.valueOf(entry.getTime()) });
        db.close();
        return numDeleted;
    }

    public int deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(TABLE_BATTERY, "*", null);
        db.close();
        return numDeleted;
    }

    public int collapseOldEntries(long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numCollapsed = db.delete(TABLE_BATTERY, KEY_TIME + " <= ? AND " + KEY_CRITICAL + " = 0", new String[] { String.valueOf(time) });
        db.close();
        return numCollapsed;
    }

    public int deleteOldEntries(long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numCollapsed = db.delete(TABLE_BATTERY, KEY_TIME + " <= ?", new String[] { String.valueOf(time) });
        db.close();
        return numCollapsed;
    }
}
