package com.originate.graphit;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
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
