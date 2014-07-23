package com.originate.graphit;

import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.originate.graphit.models.MetricModel;

public class MetricArrayAdapter extends ArrayAdapter<MetricModel> {
    private final List<MetricModel> list;
    private final Activity context;

    public MetricArrayAdapter(Activity context, List<MetricModel> list) {
        super(context, R.layout.list_item_graph, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
        protected ToggleButton toggle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        MetricModel model = list.get(position);

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.list_item_graph, null);
            final ViewHolder viewHolder = new ViewHolder();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO: Open a new activity with the graph
                }
            });

            viewHolder.text = (TextView) view.findViewById(R.id.list_item_graph_textview);

            viewHolder.toggle = (ToggleButton) view.findViewById(R.id.list_item_graph_toggle);
            viewHolder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    MetricModel model = (MetricModel) viewHolder.toggle.getTag();
                    model.setEnabled(buttonView.isChecked());

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor edit = settings.edit();
                    edit.putBoolean(model.getKey(), model.isEnabled());
                    edit.commit();
                }
            });
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            model.setEnabled(settings.getBoolean(model.getKey(), false));

            view.setTag(viewHolder);
            viewHolder.toggle.setTag(model);
            viewHolder.text.setTag(model);
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).toggle.setTag(model);
            ((ViewHolder) view.getTag()).text.setTag(model);
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(model.getName());
        holder.toggle.setChecked(model.isEnabled());

        return view;
    }
}
