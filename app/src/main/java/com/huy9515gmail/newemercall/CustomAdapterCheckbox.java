package com.huy9515gmail.newemercall;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterCheckbox extends ArrayAdapter<String> {

    SparseBooleanArray mCheckStates;
    private Context context;
    private int resource;
    private List<String> database;

    public CustomAdapterCheckbox(Context context, int resource, ArrayList<String> database) {
        super(context, resource, database);
        this.context = context;
        this.resource = resource;
        this.database = database;
        mCheckStates = new SparseBooleanArray(database.size());
        for (int i=0; i<=database.size(); i++) {
            mCheckStates.put(i, false);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_listview_emercallactivated, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tv = convertView.findViewById(R.id.tvHeader);
            viewHolder.cb = convertView.findViewById(R.id.cbHeader);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String stringInfo = database.get(position);
        viewHolder.tv.setText(stringInfo);
        viewHolder.cb.setTag(position);
        viewHolder.cb.setChecked(mCheckStates.get(position, false));
        viewHolder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckStates.put((Integer) buttonView.getTag(), isChecked);
            }
        });
        return convertView;
    }

    public boolean isChecked(int position) {
        return mCheckStates.get(position, false);
    }

    public void setChecked(int position, boolean isChecked) {
        mCheckStates.put(position, isChecked);
    }

    public void toggle(int position) {
        setChecked(position, !isChecked(position));
    }

    public class ViewHolder {
        TextView tv;
        CheckBox cb;
    }
}
