package com.aaronnebbs.peersplitandroidapplication.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.aaronnebbs.peersplitandroidapplication.Model.BottomNavBarAdapter;
import com.aaronnebbs.peersplitandroidapplication.Model.HomePageRow;
import com.aaronnebbs.peersplitandroidapplication.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    ArrayList<HomePageRow> dataModels;
    ListView listView;
    private static BottomNavBarAdapter adapter;

    public HomeFragment(){

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = getView().findViewById(R.id.home_listview);

        dataModels = new ArrayList<>();

        dataModels.add(new HomePageRow("LoremIpsum.txt", "20.56KB"));
        dataModels.add(new HomePageRow("LoremIpsum.jpg", "5.78MB"));
        dataModels.add(new HomePageRow("LoremIpsum.mp4", "200MB"));
        dataModels.add(new HomePageRow("LoremIpsum.jpg", "3.24MB"));


        adapter = new BottomNavBarAdapter(dataModels, getContext());

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HomePageRow dataModel= dataModels.get(position);
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

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }
}
