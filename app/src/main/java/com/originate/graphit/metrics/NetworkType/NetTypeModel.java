package com.originate.graphit.metrics.NetworkType;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Parcel;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import com.originate.graphit.R;
import com.originate.graphit.metrics.MetricModel;

import java.util.Calendar;
import java.util.List;

public class NetTypeModel extends MetricModel {
    public static String collapseDelayKey;

    public NetTypeModel(Context context) {
        super(context.getString(R.string.pref_screen_listName), context.getString(R.string.pref_screen_enabled));
        collapseDelayKey = context.getString(R.string.pref_battery_collapseDelay);
    }

    public NetTypeModel(Parcel in) {
        super(in.readString(), in.readString());
    }

    @Override
    public void clickHandler(Context context) {
        Intent graphIntent = new Intent(context, NetTypeActivity.class);
        graphIntent.putExtra("model", this);
        context.startActivity(graphIntent);
    }

    @Override
    public void recordData(Context context) {
      SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
      if(!settings.getBoolean(this.getEnableKey(), false)) {
        return;
      }

      collapseData(context);

      TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

      // Gives back one of the TelephonyManager.NETWORK_TYPE_xxxx constants
      int netType = telephonyManager.getNetworkType();


      Calendar calendar = Calendar.getInstance();
      NetTypeDBHelper db = new NetTypeDBHelper(context);
      NetTypeEntry entry = new NetTypeEntry(calendar.getTimeInMillis() / 1000, netType);
      if(db.getLastEntry() != null && db.getLastEntry().getType() == entry.getType()) {
        return;
      }

      db.addEntry(entry);
    }

    @Override
    public int collapseData(Context context) {
        return 0;
    }

    public List<NetTypeEntry> getData(Context context) {
        NetTypeDBHelper db = new NetTypeDBHelper(context);
        return db.getAllEntries();
    }

    public static final Creator<NetTypeModel> CREATOR = new Creator<NetTypeModel>() {
        public NetTypeModel createFromParcel(Parcel in) {
            return new NetTypeModel(in);
        }
        public NetTypeModel[] newArray (int size) {
            return new NetTypeModel[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getDisplayName());
        dest.writeString(getEnableKey());
    }
}
