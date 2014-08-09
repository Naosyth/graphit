package com.originate.graphit.metrics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;

public abstract class MetricModel implements Parcelable {
    private final String displayName;
    private final String enableKey;
    private final int icon;
    private final int color;

    public MetricModel(String name, String key, int icon, int color) {
        this.displayName = name;
        this.enableKey = key;
        this.icon = icon;
        this.color = color;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getEnableKey() {
        return this.enableKey;
    }

    public abstract void clickHandler(Context context);

    public void toggleHandler(Context context, boolean isChecked) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = settings.edit();
        edit.putBoolean(enableKey, isChecked);
        edit.apply();
    }

    public int getIcon() {
        return icon;
    }

    public int getColor() {
        return color;
    }

    public abstract void recordData(Context context);

    public abstract int collapseData(Context context);

    public abstract int deleteData(Context context);

    @Override
    public int describeContents() {
        return 0;
    }
}
