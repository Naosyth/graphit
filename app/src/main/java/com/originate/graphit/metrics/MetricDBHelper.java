package com.originate.graphit.metrics;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class MetricDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 25;
    private static final String DATABASE_NAME = "GraphIt";

    public MetricDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
