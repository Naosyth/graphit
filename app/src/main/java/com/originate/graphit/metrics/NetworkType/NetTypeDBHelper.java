package com.originate.graphit.metrics.NetworkType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.originate.graphit.metrics.MetricDBHelper;

import java.util.ArrayList;
import java.util.List;

public class NetTypeDBHelper extends MetricDBHelper {
    private static final String TABLE_NETTYPE = "network_type";
    private static final String KEY_TYPE = "type";
    private static final String KEY_TIME = "_id";

    public NetTypeDBHelper(Context context) {
        super(context);

        String query = "CREATE TABLE IF NOT EXISTS network_type (_id INTEGER PRIMARY KEY, type INTEGER)";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(query);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NETTYPE_USAGE_TABLE = "CREATE TABLE " + TABLE_NETTYPE + "("
                + KEY_TIME + " INTEGER PRIMARY KEY,"
                + KEY_TYPE + " INTEGER)";
        db.execSQL(CREATE_NETTYPE_USAGE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NETTYPE);
        onCreate(db);
    }

    public void addEntry(NetTypeEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, entry.getTime());
        values.put(KEY_TYPE, entry.getType());
        db.insert(TABLE_NETTYPE, null, values);
        db.close();
    }

    public NetTypeEntry getEntry(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NETTYPE, new String[] { KEY_TIME, KEY_TYPE }, KEY_TIME + "=?", new String[] { String.valueOf(id) }, null, null, null, null);
        if (!cursor.moveToFirst())
            return null;

        NetTypeEntry entry = new NetTypeEntry();
        entry.setTime((long)cursor.getInt(0));
        entry.setType(cursor.getInt(1));
        db.close();
        return entry;
    }

    public NetTypeEntry getLastEntry() {
        String selectQuery = "SELECT * FROM " + TABLE_NETTYPE + " WHERE " + KEY_TIME + " = (SELECT MAX(" + KEY_TIME + ") FROM " + TABLE_NETTYPE + ")";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (!cursor.moveToFirst())
            return null;

        NetTypeEntry entry = new NetTypeEntry();
        entry.setTime((long)cursor.getInt(0));
        entry.setType(cursor.getInt(1));
        db.close();
        return entry;
    }

    public List<NetTypeEntry> getLastEntries(int numEntries) {
        String selectQuery = "SELECT * FROM " + TABLE_NETTYPE + " ORDER BY " + KEY_TIME + " DESC LIMIT " + numEntries;
        List<NetTypeEntry> entryList = new ArrayList<NetTypeEntry>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (!cursor.moveToFirst())
            return null;

        do {
            NetTypeEntry entry = new NetTypeEntry();
            entry.setTime((long) cursor.getInt(0));
            entry.setType(cursor.getInt(1));
            entryList.add(entry);
        } while(cursor.moveToNext());
        db.close();
        return entryList;
    }

    public List<NetTypeEntry> getAllEntries() {
        String selectQuery = "SELECT * FROM " + TABLE_NETTYPE;
        List<NetTypeEntry> entryList = new ArrayList<NetTypeEntry>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                NetTypeEntry entry = new NetTypeEntry();
                entry.setTime((long)cursor.getInt(0));
                entry.setType(cursor.getInt(1));
                entryList.add(entry);
            } while(cursor.moveToNext());
        }
        db.close();
        return entryList;
    }

    public int getEntryCount() {
        String countQuery = "SELECT * FROM " + TABLE_NETTYPE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        db.close();
        return count;
    }

    public int updateEntry(NetTypeEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TIME, entry.getTime());
        values.put(KEY_TYPE, entry.getType());
        int numUpdated = db.update(TABLE_NETTYPE, values, KEY_TIME + " = ?", new String[] { String.valueOf(entry.getTime()) });
        db.close();
        return numUpdated;
    }

    public int deleteEntry(NetTypeEntry entry) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(TABLE_NETTYPE, KEY_TIME + " = ?", new String[] { String.valueOf(entry.getTime()) });
        db.close();
        return numDeleted;
    }

    public int deleteAllEntries() {
        SQLiteDatabase db = this.getWritableDatabase();
        int numDeleted = db.delete(TABLE_NETTYPE, "*", null);
        db.close();
        return numDeleted;
    }

    public int collapseOldEntries(long time) {
        SQLiteDatabase db = this.getWritableDatabase();
        int numCollapsed = db.delete(TABLE_NETTYPE, KEY_TIME + " <= ?", new String[] { String.valueOf(time) });
        db.close();
        return numCollapsed;
    }
}
