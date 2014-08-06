package com.originate.graphit.metrics.battery;

import android.content.ContentValues;
import android.database.Cursor;

import com.originate.graphit.metrics.MetricsEntry;

public class BatteryEntry extends MetricsEntry{
    private long time;
    private int percentage;
    private boolean critical;

    public BatteryEntry() {
    }

    public BatteryEntry(long time, int pct, boolean critical) {
        this.percentage = pct;
        this.time = time;
        this.critical = critical;
    }

    @Override
    public long getID() {
        return this.time;
    }

    @Override
    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(BatteryDBHelper.KEY_TIME, time);
        values.put(BatteryDBHelper.KEY_PERCENT, percentage);
        values.put(BatteryDBHelper.KEY_CRITICAL, critical ? 1 : 0);
        return values;
    }

    @Override
    public void setValues(Cursor cursor) {
        time = (long)cursor.getInt(0);
        percentage = cursor.getInt(1);
        critical = cursor.getInt(2) > 0;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public void setPercentage(int pct) {
        this.percentage = pct;
    }

    public int getPercentage() {
        return this.percentage;
    }

    public void setCritical(boolean critical) {
        this.critical = critical;
    }

    public boolean getCritical() {
        return this.critical;
    }
}
