package com.originate.graphit.metrics.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.originate.graphit.R;
import com.originate.graphit.metrics.MetricModel;

import java.util.Calendar;
import java.util.List;

public class NetworkModel extends MetricModel {
    public static String collapseDelayKey;
    public static String deleteDelayKey;

    public NetworkModel(Context context) {
        super(context.getString(R.string.pref_network_listName), context.getString(R.string.pref_network_enabled));
        collapseDelayKey = context.getString(R.string.pref_network_collapseDelay);
        deleteDelayKey = context.getString(R.string.pref_network_deleteDelay);
    }

    public NetworkModel(Parcel in) {
        super(in.readString(), in.readString());
    }

    @Override
    public void clickHandler(Context context) {
        NetworkDBHelper db = new NetworkDBHelper(context);
        if (db.getEntryCount() < 3) {
            Toast.makeText(context, R.string.error_not_enough_data_entries, Toast.LENGTH_LONG).show();
            return;
        }

        Intent graphIntent = new Intent(context, NetworkGraphActivity.class);
        graphIntent.putExtra("model", this);
        context.startActivity(graphIntent);
    }

    @Override
    public void recordData(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        if (!settings.getBoolean(this.getEnableKey(), false))
            return;

        collapseData(context);
        deleteData(context);

        Calendar calendar = Calendar.getInstance();
        NetworkDBHelper db = new NetworkDBHelper(context);

        long down = TrafficStats.getTotalRxBytes()/1024;
        long up = TrafficStats.getTotalTxBytes()/1024;

        if (down < 0)
            return;

        NetworkEntry entry = new NetworkEntry(calendar.getTimeInMillis()/1000, down, up);
        NetworkEntry previousEntry = db.getLastEntry();
        if (db.getLastEntry() != null && previousEntry.getUp() == entry.getUp() && previousEntry.getDown() == entry.getDown())
            return;

        db.addEntry(entry);
    }

    @Override
    public int collapseData(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        long collapseDelay = Long.parseLong(settings.getString(collapseDelayKey, "-1"));

        NetworkDBHelper db = new NetworkDBHelper(context);
        List<NetworkEntry> entries = db.getAllEntries();
        int numDeleted = 0;
        for (int i = 1; i < entries.size(); i++) {
            NetworkEntry previous = entries.get(i-1);
            NetworkEntry current = entries.get(i);

            if (Math.abs(current.getDown() - previous.getDown())/1024 < 0.05
                    && current.getTime() < (Calendar.getInstance().getTimeInMillis()-collapseDelay)/1000) {
                db.deleteEntry(previous);
                numDeleted++;
            }
        }
        return numDeleted;
    }

    @Override
    public int deleteData(Context context) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        long deleteDelay = Long.parseLong(settings.getString(deleteDelayKey, "-1"));

        if (deleteDelay == -1)
            return 0;

        NetworkDBHelper db = new NetworkDBHelper(context);
        Calendar calendar = Calendar.getInstance();
        return db.deleteOldEntries((calendar.getTimeInMillis()-deleteDelay)/1000);
    }

    public List<NetworkEntry> getData(Context context) {
        NetworkDBHelper db = new NetworkDBHelper(context);
        return db.getAllEntries();
    }

    public static final Parcelable.Creator<NetworkModel> CREATOR = new Parcelable.Creator<NetworkModel>() {
        public NetworkModel createFromParcel(Parcel in) {
            return new NetworkModel(in);
        }
        public NetworkModel[] newArray (int size) {
            return new NetworkModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getDisplayName());
        dest.writeString(getEnableKey());
    }
}
