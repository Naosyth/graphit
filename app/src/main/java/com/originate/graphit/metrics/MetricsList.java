package com.originate.graphit.metrics;

import com.originate.graphit.metrics.battery.BatteryModel;
import com.originate.graphit.metrics.screenUsage.ScreenUsageModel;

import java.util.ArrayList;

public final class MetricsList {
    private static final MetricsList instance = new MetricsList();

    private ArrayList<MetricModel> list = new ArrayList<MetricModel>();

    public static MetricsList getInstance() {
        return instance;
    }

    private MetricsList() {
        list.add(new BatteryModel());
        list.add(new ScreenUsageModel());
    }

    public ArrayList<MetricModel> getList() {
        return list;
    }
}
