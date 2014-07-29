package com.originate.graphit.metrics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.preference.PreferenceManager;

public abstract class MetricModel implements Parcelable {
    private String displayName;
    private String enableKey;

    public MetricModel(String name, String key) {
        this.displayName = name;
        this.enableKey = key;
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
        edit.commit();
    }

    public abstract void recordData(Context context);

    public abstract int collapseData(Context context);

    @Override
    public int describeContents() {
        return 0;
    }
}
