package com.originate.graphit.metrics.screenUsage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.WindowManager;
import android.os.Build;
import android.widget.Toast;

import com.originate.graphit.R;
import com.originate.graphit.metrics.MetricModel;

import java.util.Calendar;
import java.util.List;

public class ScreenUsageModel extends MetricModel {
    public static String collapseDelayKey;
    public static String deleteDelayKey;

    public ScreenUsageModel(Context context) {
        super(context.getString(R.string.pref_screen_listName), context.getString(R.string.pref_screen_enabled));
        collapseDelayKey = context.getString(R.string.pref_screen_collapseDelay);
        deleteDelayKey = context.getString(R.string.pref_screen_deleteDelay);
    }

    public ScreenUsageModel(Parcel in) {
        super(in.readString(), in.readString());
    }

    @Override
    public void clickHandler(Context context) {
        ScreenUsageDBHelper db = new ScreenUsageDBHelper(context);
        if (db.getEntryCount() < 2) {
            Toast.makeText(context, R.string.error_not_enough_data_entries, Toast.LENGTH_LONG).show();
            return;
        }

        Intent graphIntent = new Intent(context, ScreenGraphActivity.class);
        graphIntent.putExtra("model", this);
        context.startActivity(graphIntent);
    }

    @Override
    public void recordData(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (!settings.getBoolean(this.getEnableKey(), false))
            return;

        //collapseData(context);
        deleteData(context);

        boolean screenOn;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            screenOn = display.getState() == Display.STATE_ON;
        } else {
            PowerManager pwmngr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
          // Deprecated, but we're making sure we only use it in SDKs that support it so whatever, yo
          //noinspection deprecation
          screenOn = pwmngr.isScreenOn();
        }
        Calendar calendar = Calendar.getInstance();
        ScreenUsageDBHelper db = new ScreenUsageDBHelper(context);
        ScreenEntry lastEntry = db.getLastEntry();
        ScreenEntry entry = new ScreenEntry(calendar.getTimeInMillis()/1000, screenOn);
        if (db.getLastEntry() != null && lastEntry.getOn() == entry.getOn())
            return;

        db.addEntry(entry);
    }

    @Override
    public int collapseData(Context context) {
        return 0;
    }

    @Override
    public int deleteData(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        long deleteDelay = Long.parseLong(settings.getString(deleteDelayKey, "-1"));

        if (deleteDelay == -1)
            return 0;

        ScreenUsageDBHelper db = new ScreenUsageDBHelper(context);
        Calendar calendar = Calendar.getInstance();
        return db.deleteOldEntries((calendar.getTimeInMillis()-deleteDelay)/1000);
    }

    public List<ScreenEntry> getData(Context context) {
        ScreenUsageDBHelper db = new ScreenUsageDBHelper(context);
        return db.getAllEntries();
    }

    public static final Parcelable.Creator<ScreenUsageModel> CREATOR = new Parcelable.Creator<ScreenUsageModel>() {
        public ScreenUsageModel createFromParcel(Parcel in) {
            return new ScreenUsageModel(in);
        }
        public ScreenUsageModel[] newArray (int size) {
            return new ScreenUsageModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getDisplayName());
        dest.writeString(getEnableKey());
    }
}
