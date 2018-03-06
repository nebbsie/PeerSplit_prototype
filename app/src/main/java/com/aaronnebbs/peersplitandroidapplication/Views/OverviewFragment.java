package com.aaronnebbs.peersplitandroidapplication.Views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaronnebbs.peersplitandroidapplication.R;

public class OverviewFragment extends Fragment {

    public OverviewFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.overview_fragment, container, false);
    }

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }
}