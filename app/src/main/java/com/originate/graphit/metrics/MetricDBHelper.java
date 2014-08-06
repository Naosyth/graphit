package com.originate.graphit.metrics;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class MetricDBHelper<T extends MetricsEntry> extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 27;
    private static final String DATABASE_NAME = "GraphIt";

    public MetricDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public abstract T instantiateEntry();
    public abstract String getTable();
    public abstract String getIDKey();

    public void addEntry(T entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = entry.getValues();
        db.insert(getTable(), null, values);
        db.close();
    }

    public T getEntry(int id) {
        T entry = instantiateEntry();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(getTable(), new String[] { null }, getIDKey() + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        if (!cursor.moveToFirst())
            return null;
        entry.setValues(cursor);
        db.close();
        return entry;
    }

    public T getLastEntry() {
        T entry = instantiateEntry();
        String selectQuery = "SELECT * FROM " + getTable() + " WHERE " + getIDKey() + " = (SELECT MAX(" + getIDKey() + ") FROM " + getTable() + ")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (!cursor.moveToFirst())
            return null;
        entry.setValues(cursor);
        db.close();
        return entry;
    }

    public List<T> getLastEntries(int numEntries) {
        List<T> entryList = new ArrayList<T>();
        String selectQuery = "SELECT * FROM " + getTable() + " ORDER BY " + getIDKey() + " DESC LIMIT " + numEntries;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (!cursor.moveToFirst())
            return null;
        do {
            T entry = instantiateEntry();
            entry.setValues(cursor);
            entryList.add(entry);
        } while(cursor.moveToNext());
        db.close();
        return entryList;
    }

    public List<T> getAllEntries() {
        List<T> entryList = new ArrayList<T>();
        String selectQuery = "SELECT * FROM " + getTable();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (!cursor.moveToFirst())
            return null;
        do {
            T entry = instantiateEntry();
            entry.setValues(cursor);
            entryList.add(entry);
        } while(cursor.moveToNext());
        db.close();
        return entryList;
    }

    public int getEntryCount() {
        String selectQuery = "SELECT * FROM " + getTable();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int count = cursor.getCount();
        db.close();
        return count;
    }

    public int updateEntry(MetricsEntry entry) {
        ContentValues values = entry.getValues();
        SQLiteDatabase db = this.getWritableDatabase();
        int numUpdated = db.update(getTable(), values, getIDKey() + " = ?", new String[] { String.valueOf(entry.getID()) });
        db.close();
        return numUpdated;
    }

    public int deleteEntry(MetricsEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(getTable(), getIDKey() + " = ?", new String[] { String.valueOf(entry.getID()) });
        db.close();
        return numDeleted;
    }

    public int deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(getTable(), "*", null);
        db.close();
        return numDeleted;
    }

    public int deleteOldEntries(long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(getTable(), getIDKey() + " <= ?", new String[] { String.valueOf(time) });
        db.close();
        return numDeleted;
    }
}
