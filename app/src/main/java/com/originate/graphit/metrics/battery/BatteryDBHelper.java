package com.originate.graphit.metrics.battery;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.originate.graphit.metrics.MetricDBHelper;

import java.util.ArrayList;
import java.util.List;

public class BatteryDBHelper extends MetricDBHelper {
    private static final String TABLE_BATTERY = "battery";
    private static final String KEY_ID = "_id";
    private static final String KEY_PERCENT = "percent";
    private static final String KEY_TIME = "time";

    public BatteryDBHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BATTERY_TABLE = "CREATE TABLE " + TABLE_BATTERY + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_PERCENT + " REAL,"
                + KEY_TIME + " INTEGER" + ")";
        db.execSQL(CREATE_BATTERY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATTERY);
        onCreate(db);
    }

    public void addEntry(BatteryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PERCENT, entry.getPercentage());
        values.put(KEY_TIME, entry.getTime());

        db.insert(TABLE_BATTERY, null, values);
        db.close();
    }

    public BatteryEntry getEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BATTERY, new String[] { KEY_ID, KEY_PERCENT, KEY_TIME }, KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor.moveToFirst()) {
            BatteryEntry entry = new BatteryEntry();
            entry.setId(cursor.getInt(0));
            entry.setPercentage(cursor.getFloat(1));
            entry.setTime((long)cursor.getInt(2));
            return entry;
        }
        else
            return null;
    }

    public BatteryEntry getLastEntry() {
        String selectQuery = "SELECT * FROM " + TABLE_BATTERY + " WHERE " + KEY_ID + " = (SELECT MAX(" + KEY_ID + ") FROM " + TABLE_BATTERY + ")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (!cursor.moveToFirst())
            return null;

        BatteryEntry entry = new BatteryEntry();
        entry.setId(cursor.getInt(0));
        entry.setPercentage(cursor.getFloat(1));
        entry.setTime((long)cursor.getInt(2));
        return entry;
    }

    public List<BatteryEntry> getAllEntries() {
        List<BatteryEntry> entryList = new ArrayList<BatteryEntry>();
        String selectQuery = "SELECT * FROM " + TABLE_BATTERY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                BatteryEntry entry = new BatteryEntry();
                entry.setId(cursor.getInt(0));
                entry.setPercentage(cursor.getFloat(1));
                entry.setTime((long)cursor.getInt(2));
                entryList.add(entry);
            } while(cursor.moveToNext());
        }

        return entryList;
    }

    public int getEntryCount() {
        String countQuery = "SELECT * FROM " + TABLE_BATTERY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        return cursor.getCount();
    }

    public int updateEntry(BatteryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PERCENT, entry.getPercentage());
        values.put(KEY_TIME, entry.getTime());

        return db.update(TABLE_BATTERY, values, KEY_ID + " = ?", new String[] { String.valueOf(entry.getId()) });
    }

    public void deleteEntry(BatteryEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BATTERY, KEY_ID + " = ?", new String[] { String.valueOf(entry.getId()) });
        db.close();
    }
}