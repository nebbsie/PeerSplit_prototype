package com.aaronnebbs.peersplitandroidapplication.Views;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aaronnebbs.peersplitandroidapplication.R;


public class settingsFragment extends Fragment {

    public settingsFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

    public static settingsFragment newInstance() {
        return new settingsFragment();
    }

}
