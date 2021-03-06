package com.originate.graphit.metrics.screenUsage;

import android.content.ContentValues;
import android.database.Cursor;

import com.originate.graphit.metrics.MetricsEntry;

public class ScreenEntry extends MetricsEntry {
    private long time;
    private boolean on;

    public ScreenEntry() {
    }

    public ScreenEntry(long time, boolean on) {
        this.on = on;
        this.time = time;
    }

    @Override
    public long getID() {
        return this.time;
    }

    @Override
    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(ScreenUsageDBHelper.KEY_TIME, time);
        values.put(ScreenUsageDBHelper.KEY_STATE, on);
        return values;
    }

    @Override
    public void setValues(Cursor cursor) {
        time = (long)cursor.getInt(0);
        on = cursor.getInt(1) > 0;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public boolean getOn() {
        return this.on;
    }
}
