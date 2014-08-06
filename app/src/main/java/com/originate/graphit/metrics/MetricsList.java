package com.originate.graphit.metrics;

import android.content.Context;

import com.originate.graphit.metrics.battery.BatteryModel;
import com.originate.graphit.metrics.network.NetworkModel;
import com.originate.graphit.metrics.screenUsage.ScreenUsageModel;

import java.util.ArrayList;

public class MetricsList {
    private final ArrayList<MetricModel> metricsList = new ArrayList<MetricModel>();

    public MetricsList(Context context) {
        metricsList.add(new BatteryModel(context));
        metricsList.add(new ScreenUsageModel(context));
        metricsList.add(new NetworkModel(context));
    }

    public ArrayList<MetricModel> getMetricsList() {
        return metricsList;
    }
}
