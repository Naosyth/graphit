package com.originate.graphit;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.originate.graphit.metrics.MetricModel;

import java.util.ArrayList;

public class DataCollectionService extends Service {
    public DataCollectionService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<MetricModel> metricsList;
        metricsList = intent.getParcelableArrayListExtra("metricsList");

        if (metricsList == null)
            return Service.START_NOT_STICKY;

        for (int i = 0; i < metricsList.size(); i++) {
            MetricModel test = metricsList.get(i);
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
