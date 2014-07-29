package com.originate.graphit.metrics.battery;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.originate.graphit.R;
import com.originate.graphit.metrics.MetricModel;

import java.util.Calendar;
import java.util.List;

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
        collapseData(context);

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

        if (db.getLastEntry() != null && db.getLastEntry().getPercentage() == entry.getPercentage()) {
            Log.v("GRAPHIT", "No change");
            return;
        }

        Log.v("GRAPHIT", "Logging a point " + entry.getPercentage());

        db.addEntry(entry);
        detectCritical(context, db.getLastEntries(3));
    }

    private void detectCritical(Context context, List<BatteryEntry> entries) {
        if (entries.size() < 3)
            return;

        BatteryEntry criticalEntry = null;
        boolean isCritical = false;
        // Case 1: Unplugged from fully charged state
        if (Float.compare(entries.get(2).getPercentage(), entries.get(1).getPercentage()) == 0) {
            isCritical = true;
            criticalEntry = entries.get(1);
        }
        // Case 2: Unplugged from charging state
        else if (entries.get(1).getPercentage() < entries.get(0).getPercentage() && entries.get(1).getPercentage() < entries.get(2).getPercentage()) {
            isCritical = true;
            criticalEntry = entries.get(1);
        }
        // Case 3: Plugged in from discharging state
        else if (entries.get(1).getPercentage() > entries.get(0).getPercentage() && entries.get(1).getPercentage() > entries.get(2).getPercentage()) {
            isCritical = true;
            criticalEntry = entries.get(1);
        }
        // Case 4: Only data point
        else if (entries.size() == 1) {
            isCritical = true;
            criticalEntry = entries.get(0);
        }

        if (isCritical) {
            BatteryDBHelper db = new BatteryDBHelper(context);
            criticalEntry.setCritical(true);
            int debug = db.updateEntry(criticalEntry);
            Log.v("GRAPHIT", "Critical point detected " + criticalEntry.getPercentage() + ", " + debug);
        }
    }

    @Override
    public void collapseData(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        long collapseDelay = Long.parseLong(settings.getString(context.getString(R.string.pref_battery_collapseDelay), "-1"));

        BatteryDBHelper db = new BatteryDBHelper( context );
        Calendar calendar = Calendar.getInstance();
        int collapsed = db.collapseOldEntries( (calendar.getTimeInMillis()-collapseDelay)/1000 );
        Log.v("GRAPHIT", "Collapsed " + collapsed + " entries older than " + (calendar.getTimeInMillis()-collapseDelay)/1000);
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
