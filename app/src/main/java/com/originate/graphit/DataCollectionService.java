package com.originate.graphit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.originate.graphit.metrics.MetricModel;
import com.originate.graphit.metrics.MetricsList;

import java.util.ArrayList;

public class DataCollectionService extends Service {
    public DataCollectionService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<MetricModel> metricsList;
        metricsList = new MetricsList(this).getMetricsList();

        if (metricsList == null) {
            this.stopSelf();
            return Service.START_NOT_STICKY;
        }

        for (MetricModel test : metricsList) {
            test.recordData(this);
        }

        this.stopSelf();
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
