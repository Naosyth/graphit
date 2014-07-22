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
import android.widget.Toast;
import android.widget.ToggleButton;

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
        View view = null;
        if (convertView == null) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.list_item_graph, null);
            final ViewHolder viewHolder = new ViewHolder();

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MetricModel element = (MetricModel) viewHolder.text.getTag();
                }
            });

            viewHolder.text = (TextView) view.findViewById(R.id.list_item_graph_textview);

            viewHolder.toggle = (ToggleButton) view.findViewById(R.id.list_item_graph_toggle);
            viewHolder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    MetricModel element = (MetricModel) viewHolder.toggle.getTag();
                    element.setEnabled(buttonView.isChecked());

                    SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor edit = settings.edit();
                    edit.putBoolean(element.getKey(), element.isEnabled());
                    edit.commit();
                }
            });
            list.get(position).setEnabled(settings.getBoolean(((MetricModel)list.get(position)).getKey(), false));

            view.setTag(viewHolder);
            viewHolder.toggle.setTag(list.get(position));
            viewHolder.text.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).toggle.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(list.get(position).getName());
        holder.toggle.setChecked(list.get(position).isEnabled());

        return view;
    }
}
