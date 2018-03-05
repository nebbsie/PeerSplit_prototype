package com.aaronnebbs.peersplitandroidapplication.Views;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aaronnebbs.peersplitandroidapplication.Model.CustomAdapter;
import com.aaronnebbs.peersplitandroidapplication.Model.HomePageRow;
import com.aaronnebbs.peersplitandroidapplication.R;

import java.util.ArrayList;

public class homeFragment extends Fragment {

    ArrayList<HomePageRow> dataModels;
    ListView listView;
    private static CustomAdapter adapter;

    public homeFragment(){

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = getView().findViewById(R.id.home_listview);

        dataModels = new ArrayList<>();

        dataModels.add(new HomePageRow("First test!"));
        dataModels.add(new HomePageRow("Second test!"));
        dataModels.add(new HomePageRow("Third test!"));
        dataModels.add(new HomePageRow("Last test!"));

        adapter = new CustomAdapter(dataModels, getContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomePageRow dataModel= dataModels.get(position);
                Snackbar.make(view, dataModel.getStr(), Snackbar.LENGTH_LONG).setAction("No action", null).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.home_fragment, container, false);
    }

    public static homeFragment newInstance() {
        return new homeFragment();
    }
}
