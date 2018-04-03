package com.aaronnebbs.peersplitandroidapplication.Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.aaronnebbs.peersplitandroidapplication.Helpers.ImageSelector;
import com.aaronnebbs.peersplitandroidapplication.R;
import java.util.ArrayList;

public class BottomNavBarAdapter extends ArrayAdapter<HomePageRow> implements View.OnClickListener {

    private ArrayList<HomePageRow> dataList;
    private int lastPosition = -1;
    private Context activityContext;

    public BottomNavBarAdapter(ArrayList<HomePageRow> data, Context context){
        super(context, R.layout.row_item, data);
        this.dataList = data;
        this.activityContext = context;
    }

    // Used to hold information about the row.
    private static class ViewHolder {
        TextView name;
        TextView size;
        ImageView fileTypeImage;
        ImageView downloadImage;
    }

    // OnClick for the download button.
    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Object obj = getItem(position);
        HomePageRow model = (HomePageRow)obj;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View res;
        HomePageRow row = getItem(position);
        ViewHolder viewHolder;

        // Create a row.
        if(convertView == null){
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);

            viewHolder.name = convertView.findViewById(R.id.row_item_string);
            viewHolder.size = convertView.findViewById(R.id.row_size);
            viewHolder.downloadImage = convertView.findViewById(R.id.row_download_image);
            viewHolder.fileTypeImage = convertView.findViewById(R.id.row_file_type);

            res = convertView;
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
            res = convertView;
        }

        lastPosition = position;

        // Set the text of the row
        viewHolder.name.setText(row.getName());
        viewHolder.size.setText(row.getSize());

        // Set the images for the row
        viewHolder.fileTypeImage.setImageResource(ImageSelector.getTypeImage(row.getName()));
        viewHolder.downloadImage.setImageResource(R.drawable.ic_file_download_black_24dp);
        viewHolder.downloadImage.setOnClickListener(this);
        viewHolder.downloadImage.setTag(position);

        return convertView;
    }
}
