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
        // Inflate the menu; this adds items to the action bar if it is present.
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class MainFragment extends Fragment {

        private ArrayAdapter<String> mGraphListAdapter;

        public MainFragment() {
        }

        private List<MetricModel> getModel() {
            List<MetricModel> list = new ArrayList<MetricModel>();
            list.add(new MetricModel("Battery", "battery"));
            list.add(new MetricModel("Data", "data"));
            list.add(new MetricModel("Accelerometer", "accelerometer"));
            list.add(new MetricModel("Ambient Light", "ambient_light"));
            list.add(new MetricModel("Gyroscope", "gyroscope"));
            list.add(new MetricModel("Proximity", "proximity"));
            list.add(new MetricModel("Distance Walked", "distance_walked"));
            list.add(new MetricModel("Steps Taken", "steps"));
            list.add(new MetricModel("Something Else", "something_else"));
            list.add(new MetricModel("Another Thing", "another_thing"));
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
