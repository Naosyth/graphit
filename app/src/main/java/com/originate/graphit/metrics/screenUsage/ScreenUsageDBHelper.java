package com.originate.graphit.metrics.screenUsage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.originate.graphit.metrics.MetricDBHelper;

public class ScreenUsageDBHelper extends MetricDBHelper {
    public static final String TABLE_SCREEN = "screen_usage";
    public static final String KEY_STATE = "state";
    public static final String KEY_TIME = "_id";

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

    @Override
    public ScreenEntry instantiateEntry() {
        return new ScreenEntry();
    }

    @Override
    public String getTable() {
        return TABLE_SCREEN;
    }

    @Override
    public String getIDKey() {
        return KEY_TIME;
    }
}
