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

import com.originate.graphit.models.MetricModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }

        Intent intent = new Intent(this, DataCollectionService.class);
        PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
        Calendar cal = Calendar.getInstance();
        AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 60*1000, pintent);
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
            Intent intent = new Intent(this, DataCollectionService.class);
            PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
            Calendar cal = Calendar.getInstance();
            AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 60*1000, pintent);
        }
        else if (id == R.id.action_stopService) {
            Intent intentStop = new Intent(this, DataCollectionService.class);
            PendingIntent pintentStop = PendingIntent.getService(this, 0, intentStop, 0);
            AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pintentStop);
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MainFragment extends Fragment {
        public MainFragment() {
        }

        private List<MetricModel> getModel() {
            List<MetricModel> list = new ArrayList<MetricModel>();
            list.add(new MetricModel("Battery", "pref_battery_enabled"));
            list.add(new MetricModel("Data", "pref_data_enabled"));
            list.add(new MetricModel("Screen Usage", "pref_screen_usage_enabled"));
            list.add(new MetricModel("Distance Moved", "pref_distance_moved_enabled"));
            list.add(new MetricModel("Steps Taken", "pref_stepsTaken_enabled"));
            list.add(new MetricModel("...", "...1"));
            list.add(new MetricModel("...", "...2"));
            list.add(new MetricModel("...", "...3"));
            list.add(new MetricModel("...", "...4"));
            list.add(new MetricModel("...", "...5"));
            return list;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ArrayAdapter<MetricModel> adapter = new MetricArrayAdapter(getActivity(), getModel());
            ListView listView = (ListView) rootView.findViewById(R.id.listview_graphs);
            listView.setAdapter(adapter);

            return rootView;
        }
    }
}
