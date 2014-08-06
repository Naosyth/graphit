package com.originate.graphit.metrics;

import android.content.ContentValues;
import android.database.Cursor;

public abstract class MetricsEntry {
    public abstract ContentValues getValues();
    public abstract void setValues(Cursor cursor);
    public abstract String getTable();
    public abstract String getIDKey();


}
