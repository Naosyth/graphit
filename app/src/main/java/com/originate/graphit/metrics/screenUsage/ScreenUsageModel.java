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

import com.originate.graphit.R;
import com.originate.graphit.metrics.MetricModel;

import java.util.Calendar;
import java.util.List;

public class ScreenUsageModel extends MetricModel {
    public static String collapseDelayKey;

    public ScreenUsageModel(Context context) {
        super(context.getString(R.string.pref_screen_listName), context.getString(R.string.pref_screen_enabled));
        collapseDelayKey = context.getString(R.string.pref_battery_collapseDelay);
    }

    public ScreenUsageModel(Parcel in) {
        super(in.readString(), in.readString());
    }

    @Override
    public void clickHandler(Context context) {
        Intent graphIntent = new Intent(context, ScreenGraphActivity.class);
        graphIntent.putExtra("model", this);
        context.startActivity(graphIntent);
    }

    @Override
    public void recordData(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (!settings.getBoolean(this.getEnableKey(), false))
            return;

        collapseData(context);

        //IntentFilter ifilter1 = new IntentFilter(Intent.ACTION_SCREEN_ON);
        //IntentFilter ifilter2 = new IntentFilter(Intent.ACTION_SCREEN_OFF);


        //if (ifilter1 == null || ifilter2 == null) {
        //  return;
        //}

        //Intent screenOnIntent = context.registerReceiver(null, ifilter1);
        //Intent screenOffIntent = context.registerReceiver(null, ifilter2);

        boolean screenOn;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
            screenOn = display.getState() == Display.STATE_ON;
        } else {
            PowerManager pwmngr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            screenOn = pwmngr.isScreenOn();
        }
        Calendar calendar = Calendar.getInstance();
        ScreenUsageDBHelper db = new ScreenUsageDBHelper(context);
        ScreenEntry entry = new ScreenEntry(calendar.getTimeInMillis()/1000, screenOn);
        if (db.getLastEntry() != null && db.getLastEntry().getOn() == entry.getOn())
            return;

        db.addEntry(entry);
    }

    @Override
    public int collapseData(Context context) {
        /*SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences( context );
        long collapseDelay = Long.parseLong(settings.getString(collapseDelayKey, "-1"));
        ScreenUsageDBHelper db = new ScreenUsageDBHelper(context);
        Calendar calendar = Calendar.getInstance();
        return db.collapseOldEntries((calendar.getTimeInMillis()-collapseDelay)/1000);*/
        return 0;
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
