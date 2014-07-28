package com.originate.graphit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.originate.graphit.metrics.MetricsList;
import com.originate.graphit.metrics.battery.BatteryModel;
import com.originate.graphit.metrics.MetricModel;
import com.originate.graphit.metrics.screenUsage.ScreenUsageModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    private ArrayList<MetricModel> metricsList = MetricsList.getInstance().getList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startBackgroundService();

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment(metricsList))
                    .commit();
        }
    }

    private void startBackgroundService() {
        Intent intent = new Intent(this, DataCollectionService.class);
        intent.putParcelableArrayListExtra("metricsList", metricsList);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        Calendar cal = Calendar.getInstance();
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 60*1000, pintent);
    }

    private void stopBackgroundService() {
        Intent intentStop = new Intent(this, DataCollectionService.class);
        PendingIntent pintentStop = PendingIntent.getService(this, 0, intentStop, 0);
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pintentStop);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        else if (id == R.id.action_startService) {
            startBackgroundService();
        }
        else if (id == R.id.action_stopService) {
            stopBackgroundService();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MainFragment extends Fragment {
        private List<MetricModel> metricsList;

        public MainFragment() {
        }

        public MainFragment(List<MetricModel> metricsList) {
            this.metricsList = metricsList;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ArrayAdapter<MetricModel> adapter = new MetricArrayAdapter(getActivity(), metricsList);
            ListView listView = (ListView) rootView.findViewById(R.id.listview_graphs);
            listView.setAdapter(adapter);

            return rootView;
        }
    }
}
