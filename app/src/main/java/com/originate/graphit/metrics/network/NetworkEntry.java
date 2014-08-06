package com.originate.graphit.metrics.network;

import android.content.ContentValues;
import android.database.Cursor;

import com.originate.graphit.metrics.MetricsEntry;

public class NetworkEntry extends MetricsEntry {
    private long time;
    private float down;
    private float up;

    public NetworkEntry() {
    }

    public NetworkEntry(long time, float up, float down) {
        this.time = time;
        this.down = up;
        this.up = down;
    }

    @Override
    public long getID() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public void setDown(float pct) {
        this.down = pct;
    }

    public float getDown() {
        return this.down;
    }

    public void setUp(float up) {
        this.up = up;
    }

    public float getUp() {
        return this.up;
    }

    @Override
    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(NetworkDBHelper.KEY_TIME, time);
        values.put(NetworkDBHelper.KEY_DOWN, down);
        values.put(NetworkDBHelper.KEY_UP, up);
        return values;
    }

    @Override
    public void setValues(Cursor cursor) {
        time = (long)cursor.getInt(0);
        down = cursor.getInt(1);
        up = cursor.getInt(2);
    }
}
