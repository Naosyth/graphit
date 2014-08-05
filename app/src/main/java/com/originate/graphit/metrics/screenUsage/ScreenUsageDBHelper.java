package com.originate.graphit.metrics.screenUsage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.originate.graphit.metrics.MetricDBHelper;

import java.util.ArrayList;
import java.util.List;

public class ScreenUsageDBHelper extends MetricDBHelper {
    private static final String TABLE_SCREEN = "screen_usage";
    private static final String KEY_STATE = "state";
    private static final String KEY_TIME = "_id";

    public ScreenUsageDBHelper(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SCREEN_USAGE_TABLE = "CREATE TABLE " + TABLE_SCREEN + "("
                + KEY_TIME + " INTEGER PRIMARY KEY,"
                + KEY_STATE + " INTEGER)";
        db.execSQL(CREATE_SCREEN_USAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCREEN);
        onCreate(db);
    }

    public void addEntry(ScreenEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, entry.getTime());
        values.put(KEY_STATE, entry.getOn() ? 1 : 0);
        db.insert(TABLE_SCREEN, null, values);
        db.close();
    }

    public ScreenEntry getEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_SCREEN, new String[] { KEY_TIME, KEY_STATE }, KEY_TIME + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        if (!cursor.moveToFirst())
            return null;

        ScreenEntry entry = new ScreenEntry();
        entry.setTime((long)cursor.getInt(0));
        entry.setOn(cursor.getInt(1) > 0);
        db.close();
        return entry;
    }

    public ScreenEntry getLastEntry() {
        String selectQuery = "SELECT * FROM " + TABLE_SCREEN + " WHERE " + KEY_TIME + " = (SELECT MAX(" + KEY_TIME + ") FROM " + TABLE_SCREEN + ")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (!cursor.moveToFirst())
            return null;

        ScreenEntry entry = new ScreenEntry();
        entry.setTime((long)cursor.getInt(0));
        entry.setOn(cursor.getInt(1) > 0);
        db.close();
        return entry;
    }

    public List<ScreenEntry> getLastEntries(int numEntries) {
        String selectQuery = "SELECT * FROM " + TABLE_SCREEN + " ORDER BY " + KEY_TIME + " DESC LIMIT " + numEntries;
        List<ScreenEntry> entryList = new ArrayList<ScreenEntry>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (!cursor.moveToFirst())
            return null;

        do {
            ScreenEntry entry = new ScreenEntry();
            entry.setTime((long) cursor.getInt(0));
            entry.setOn(cursor.getInt(1) == 1);
            entryList.add(entry);
        } while(cursor.moveToNext());
        db.close();
        return entryList;
    }

    public List<ScreenEntry> getAllEntries() {
        String selectQuery = "SELECT * FROM " + TABLE_SCREEN;
        List<ScreenEntry> entryList = new ArrayList<ScreenEntry>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                ScreenEntry entry = new ScreenEntry();
                entry.setTime((long)cursor.getInt(0));
                entry.setOn(cursor.getInt(1) == 1);
                entryList.add(entry);
            } while(cursor.moveToNext());
        }
        db.close();
        return entryList;
    }

    public int getEntryCount() {
        String countQuery = "SELECT * FROM " + TABLE_SCREEN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        db.close();
        return count;
    }

    public int updateEntry(ScreenEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, entry.getTime());
        values.put(KEY_STATE, entry.getOn());
        int numUpdated = db.update(TABLE_SCREEN, values, KEY_TIME + " = ?", new String[] { String.valueOf(entry.getTime()) });
        db.close();
        return numUpdated;
    }

    public int deleteEntry(ScreenEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(TABLE_SCREEN, KEY_TIME + " = ?", new String[] { String.valueOf(entry.getTime()) });
        db.close();
        return numDeleted;
    }

    public int deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(TABLE_SCREEN, "*", null);
        db.close();
        return numDeleted;
    }

    public int deleteOldEntries(long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numCollapsed = db.delete(TABLE_SCREEN, KEY_TIME + " <= ?", new String[] { String.valueOf(time) });
        db.close();
        return numCollapsed;
    }
}
