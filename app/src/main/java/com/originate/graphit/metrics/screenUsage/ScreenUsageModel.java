package com.originate.graphit.metrics.screenUsage;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.originate.graphit.metrics.MetricModel;

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
    public void recordData(Context context) {
        //TODO: Record screen usage data
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
