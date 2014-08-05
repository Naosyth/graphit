package com.originate.graphit.metrics.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.originate.graphit.metrics.MetricDBHelper;

import java.util.ArrayList;
import java.util.List;

public class NetworkDBHelper extends MetricDBHelper {
    private static final String TABLE_NETWORK = "network";
    private static final String KEY_TIME = "_id";
    private static final String KEY_DOWN = "down";
    private static final String KEY_UP = "up";

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

    public void addEntry(NetworkEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, entry.getTime());
        values.put(KEY_DOWN, entry.getDown());
        values.put(KEY_UP, entry.getUp());

        db.insert(TABLE_NETWORK, null, values);
        db.close();
    }

    public NetworkEntry getEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NETWORK, new String[] { KEY_TIME, KEY_DOWN, KEY_UP }, KEY_TIME + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        if (!cursor.moveToFirst())
            return null;

        NetworkEntry entry = new NetworkEntry();
        entry.setTime((long)cursor.getInt(0));
        entry.setDown(cursor.getInt(1));
        entry.setUp(cursor.getInt(2));
        db.close();
        return entry;
    }

    public NetworkEntry getLastEntry() {
        String selectQuery = "SELECT * FROM " + TABLE_NETWORK + " WHERE " + KEY_TIME + " = (SELECT MAX(" + KEY_TIME + ") FROM " + TABLE_NETWORK + ")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (!cursor.moveToFirst())
            return null;

        NetworkEntry entry = new NetworkEntry();
        entry.setTime((long)cursor.getInt(0));
        entry.setDown(cursor.getInt(1));
        entry.setUp(cursor.getInt(2));
        db.close();
        return entry;
    }

    public List<NetworkEntry> getLastEntries(int numEntries) {
        String selectQuery = "SELECT * FROM " + TABLE_NETWORK + " ORDER BY " + KEY_TIME + " DESC LIMIT " + numEntries;
        List<NetworkEntry> entryList = new ArrayList<NetworkEntry>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (!cursor.moveToFirst())
            return null;

        do {
            NetworkEntry entry = new NetworkEntry();
            entry.setTime((long)cursor.getInt(0));
            entry.setDown(cursor.getInt(1));
            entry.setUp(cursor.getInt(2));
            entryList.add(entry);
        } while(cursor.moveToNext());
        db.close();
        return entryList;
    }

    public List<NetworkEntry> getAllEntries() {
        String selectQuery = "SELECT * FROM " + TABLE_NETWORK;
        List<NetworkEntry> entryList = new ArrayList<NetworkEntry>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                NetworkEntry entry = new NetworkEntry();
                entry.setTime((long)cursor.getInt(0));
                entry.setDown(cursor.getInt(1));
                entry.setUp(cursor.getInt(2));
                entryList.add(entry);
            } while(cursor.moveToNext());
        }
        db.close();
        return entryList;
    }

    public int getEntryCount() {
        String countQuery = "SELECT * FROM " + TABLE_NETWORK;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        db.close();
        return count;
    }

    public int updateEntry(NetworkEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, entry.getTime());
        values.put(KEY_DOWN, entry.getDown());
        values.put(KEY_UP, entry.getUp());
        int numUpdated = db.update(TABLE_NETWORK, values, KEY_TIME + " = ?", new String[] { String.valueOf(entry.getTime()) });
        db.close();
        return numUpdated;
    }

    public int deleteEntry(NetworkEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(TABLE_NETWORK, KEY_TIME + " = ?", new String[] { String.valueOf(entry.getTime()) });
        db.close();
        return numDeleted;
    }

    public int deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(TABLE_NETWORK, "*", null);
        db.close();
        return numDeleted;
    }

    public int deleteOldEntries(long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numCollapsed = db.delete(TABLE_NETWORK, KEY_TIME + " <= ?", new String[] { String.valueOf(time) });
        db.close();
        return numCollapsed;
    }
}
