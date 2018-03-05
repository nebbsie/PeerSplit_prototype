package com.aaronnebbs.peersplitandroidapplication.Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aaronnebbs.peersplitandroidapplication.R;

import java.util.ArrayList;

/**
 * Created by Aaron on 05/03/2018.
 */

public class CustomAdapter extends ArrayAdapter<HomePageRow> implements View.OnClickListener {

    private ArrayList<HomePageRow> dataSet;
    private int lastPosition = -1;
    Context mContext;

    private static class ViewHolder {
        TextView str;
    }

    public CustomAdapter(ArrayList<HomePageRow> data, Context context){
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object obj = getItem(position);
        HomePageRow model = (HomePageRow)obj;
        System.out.println(model.getStr());
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HomePageRow row = getItem(position);
        ViewHolder viewHolder;

        final View res;

        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.str = (TextView) convertView.findViewById(R.id.row_item_string);
            res = convertView;
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            res = convertView;
        }

        lastPosition = position;

        viewHolder.str.setText(row.getStr());

        return convertView;
    }
}
