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

import com.originate.graphit.metrics.MetricModel;

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
        final MetricModel model = (MetricModel) list.get(position);

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.list_item_graph, null);
            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.text = (TextView) view.findViewById(R.id.list_item_graph_textview);
            viewHolder.toggle = (ToggleButton) view.findViewById(R.id.list_item_graph_toggle);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model.clickHandler(context);
                }
            });

            viewHolder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    model.toggleHandler(context, isChecked);
                }
            });

            view.setTag(viewHolder);
            viewHolder.toggle.setTag(model);
            viewHolder.text.setTag(model);
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).toggle.setTag(model);
            ((ViewHolder) view.getTag()).text.setTag(model);
        }

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(model.getDisplayName());
        holder.toggle.setChecked(settings.getBoolean(model.getEnableKey(), false));

        return view;
    }
}
