package com.originate.graphit.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

public class ScreenUsageModel extends MetricModel {
    private String databaseKey;

    public ScreenUsageModel() {
        super("Screen Usage", "pref_screenUsage_enabled");
        databaseKey = "pref_screenUsage_dbkey";
    }

    public ScreenUsageModel(Parcel in) {
        super(in.readString(), in.readString());
        databaseKey = in.readString();
    }

    @Override
    public void clickHandler(Context context) {
        //TODO: Launch graph activity for screen data
    }

    @Override
    public void collapseData() {
        //TODO: Collapse screen usage data
    }

    public static final Parcelable.Creator<ScreenUsageModel> CREATOR
            = new Parcelable.Creator<ScreenUsageModel>() {
        public ScreenUsageModel createFromParcel(Parcel in) {
            return new ScreenUsageModel(in);
        }

        public ScreenUsageModel[] newArray (int size) {
            return new ScreenUsageModel[size];
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
