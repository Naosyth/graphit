package com.originate.graphit.metrics.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.originate.graphit.metrics.MetricModel;

import java.util.Calendar;

public class BatteryModel extends MetricModel {
    public BatteryModel() {
        super("Battery", "pref_battery_enabled");
    }

    public BatteryModel(Parcel in) {
        super(in.readString(), in.readString());
    }

    @Override
    public void clickHandler(Context context) {
        // Temporary test code to make sure entries are written to the db:
        BatteryDBHelper db = new BatteryDBHelper(context);
        String debug = "There are " + db.getEntryCount() + " entries.";
        Toast.makeText(context, debug, Toast.LENGTH_SHORT).show();

        //TODO: Launch graph activity for battery data
    }

    @Override
    public void recordData(Context context) {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        if (batteryStatus == null)
            return;

        float batteryPct = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) / batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        Calendar calendar = Calendar.getInstance();
        BatteryEntry entry = new BatteryEntry(batteryPct, calendar.getTimeInMillis());
        BatteryDBHelper db = new BatteryDBHelper(context);

        if (db.getLastEntry() != null && db.getLastEntry().getPercentage() == entry.getPercentage())
            return;

        db.addEntry(entry);
    }

    @Override
    public void collapseData() {
        //TODO: Collapse battery data
    }

    public static final Parcelable.Creator<BatteryModel> CREATOR = new Parcelable.Creator<BatteryModel>() {
        public BatteryModel createFromParcel(Parcel in) {
            return new BatteryModel(in);
        }

        public BatteryModel[] newArray (int size) {
            return new BatteryModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getDisplayName());
        dest.writeString(getEnableKey());
    }
}
