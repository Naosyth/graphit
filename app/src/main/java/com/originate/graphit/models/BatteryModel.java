package com.originate.graphit.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

public class BatteryModel extends MetricModel {
    private String databaseKey;

    public BatteryModel() {
        super("Battery", "pref_battery_enabled");
        databaseKey = "pref_battery_dbkey";
    }

    public BatteryModel(Parcel in) {
        super(in.readString(), in.readString());
        databaseKey = in.readString();
    }

    @Override
    public void clickHandler(Context context) {
        //TODO: Launch graph activity for battery data
    }

    @Override
    public void recordData(Context context) {
        //TODO: Record battery data
    }

    public static final Parcelable.Creator<BatteryModel> CREATOR
            = new Parcelable.Creator<BatteryModel>() {
        public BatteryModel createFromParcel(Parcel in) {
            return new BatteryModel(in);
        }

        public BatteryModel[] newArray (int size) {
            return new BatteryModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getDisplayName());
        dest.writeString(getEnableKey());
        dest.writeString(databaseKey);
    }
}
