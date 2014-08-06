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

    private Class<T> clazz;

    public MetricDBHelper(Context context, Class<T> clazz) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.clazz = clazz;
    }

    public <T extends MetricsEntry> void addEntry(T entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = entry.getValues();
        db.insert(entry.getTable(), null, values);
        db.close();
    }

    public T getEntry(int id) {
        T entry = null;
        try {
            entry = clazz.newInstance();

            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.query(entry.getTable(), new String[] { null }, entry.getIDKey() + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
            if (!cursor.moveToFirst())
                return null;

            entry.setValues(cursor);
            db.close();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return entry;
    }

    public T getLastEntry() {
        T entry = null;
        try {
            entry = clazz.cast(clazz.newInstance());

            String selectQuery = "SELECT * FROM " + entry.getTable() + " WHERE " + entry.getIDKey() + " = (SELECT MAX(" + entry.getIDKey() + ") FROM " + entry.getTable() + ")";
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (!cursor.moveToFirst())
                return null;

            entry.setValues(cursor);
            db.close();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return entry;
    }

    public List<T> getLastEntries(int numEntries) {
        List<T> entryList = new ArrayList<T>();
        try {
            T entry = clazz.newInstance();
            String selectQuery = "SELECT * FROM " + entry.getTable() + " ORDER BY " + entry.getIDKey() + " DESC LIMIT " + numEntries;
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (!cursor.moveToFirst())
                return null;

            do {
                entry = clazz.newInstance();
                entry.setValues(cursor);
                entryList.add(entry);
            } while(cursor.moveToNext());

            entry.setValues(cursor);
            db.close();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return entryList;
    }
}
