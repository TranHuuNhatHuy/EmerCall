package com.huy9515gmail.newemercall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resource;
    private List<String> database;

    public CustomAdapter(Context context, int resource, ArrayList<String> database) {
        super(context, resource, database);
        this.context = context;
        this.resource = resource;
        this.database = database;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_listview_emerdict, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.item = convertView.findViewById(R.id.cbHeader);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String itemDatabase = database.get(position);
        viewHolder.item.setText(itemDatabase);
        return convertView;
    }

    public class ViewHolder {
        TextView item;
    }
}
