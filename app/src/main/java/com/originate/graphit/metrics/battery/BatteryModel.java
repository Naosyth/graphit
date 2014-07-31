package com.originate.graphit.metrics.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.originate.graphit.R;
import com.originate.graphit.metrics.MetricModel;

import java.util.Calendar;
import java.util.List;

public class BatteryModel extends MetricModel {
    public static String collapseDelayKey;
    public BatteryModel(Context context) {
        super(context.getString(R.string.pref_battery_listName), context.getString(R.string.pref_battery_enabled));
        collapseDelayKey = context.getString(R.string.pref_battery_collapseDelay);
    }

    public BatteryModel(Parcel in) {
        super(in.readString(), in.readString());
    }

    @Override
    public void clickHandler(Context context) {
        Intent graphIntent = new Intent(context, BatteryGraphActivity.class);
        graphIntent.putExtra("model", this);
        context.startActivity(graphIntent);
    }

    @Override
    public void recordData(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (!settings.getBoolean(this.getEnableKey(), false))
            return;

        int numCollapsed = collapseData(context);
        Toast.makeText(context, "Collapsing " + numCollapsed + " entries", Toast.LENGTH_LONG).show();

        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        if (batteryStatus == null)
            return;

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryPct = (int)(100*(level/(float)scale));
        Calendar calendar = Calendar.getInstance();
        BatteryDBHelper db = new BatteryDBHelper(context);
        BatteryEntry entry = new BatteryEntry(calendar.getTimeInMillis()/1000, batteryPct, false);
        if (db.getLastEntry() != null && db.getLastEntry().getPercentage() == entry.getPercentage())
            return;

        Toast.makeText(context, "Adding a point", Toast.LENGTH_SHORT).show();

        db.addEntry(entry);
        detectCritical(context, db.getLastEntries(3));
    }

    private boolean detectCritical(Context context, List<BatteryEntry> entries) {
        BatteryEntry criticalEntry = null;

        if (entries.size() == 1) {
            criticalEntry = entries.get(0);
        } else if (entries.size() >= 3
                && (Float.compare(entries.get(2).getPercentage(), entries.get(1).getPercentage()) == 0 // Case 1: Unplugged from fully charged state
                || entries.get(1).getPercentage() < entries.get(0).getPercentage() && entries.get(1).getPercentage() < entries.get(2).getPercentage() // Case 2: Unplugged from charging state
                || entries.get(1).getPercentage() > entries.get(0).getPercentage() && entries.get(1).getPercentage() > entries.get(2).getPercentage())) { // Case 3: Plugged in from discharging state
            criticalEntry = entries.get(1);
        }

        if (criticalEntry != null) {
            BatteryDBHelper db = new BatteryDBHelper(context);
            criticalEntry.setCritical(true);
            db.updateEntry(criticalEntry);
            return true;
        }
        return false;
    }

    @Override
    public int collapseData(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        long collapseDelay = Long.parseLong(settings.getString(collapseDelayKey, "-1"));

        if (collapseDelay == -1)
            return 0;

        BatteryDBHelper db = new BatteryDBHelper(context);
        Calendar calendar = Calendar.getInstance();
        int debug = db.testCollapseOldEntries((calendar.getTimeInMillis()-collapseDelay)/1000);
        if (debug > 0) {
            Toast.makeText(context, "Found " + debug + " entries to delete with time <= " + (calendar.getTimeInMillis()-collapseDelay)/1000, Toast.LENGTH_LONG).show();
        }
        return db.collapseOldEntries((calendar.getTimeInMillis()-collapseDelay)/1000);
    }

    public List<BatteryEntry> getData(Context context) {
        BatteryDBHelper db = new BatteryDBHelper(context);
        return db.getAllEntries();
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
