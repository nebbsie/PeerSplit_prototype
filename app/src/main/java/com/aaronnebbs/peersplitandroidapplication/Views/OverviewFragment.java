package com.aaronnebbs.peersplitandroidapplication.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aaronnebbs.peersplitandroidapplication.R;
import az.plainpie.PieView;

public class OverviewFragment extends Fragment {

    private PieView cloudStorageChart;
    private PieView localStorageChart;

    public OverviewFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Setup the pie charts
        cloudStorageChart = getView().findViewById(R.id.cloudStoragePieChart);
        localStorageChart = getView().findViewById(R.id.localStoragePieChart);
        // Set bar colour
        cloudStorageChart.setPercentageBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        localStorageChart.setPercentageBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        // Set inner colour to white
        cloudStorageChart.setInnerBackgroundColor(getResources().getColor(R.color.white));
        localStorageChart.setInnerBackgroundColor(getResources().getColor(R.color.white));
        // Set font colour
        cloudStorageChart.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        localStorageChart.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        // Set the font size
        cloudStorageChart.setPercentageTextSize(13);
        localStorageChart.setPercentageTextSize(13);
        // Set the percentage
        cloudStorageChart.setPercentage(50);
        localStorageChart.setPercentage(25);
        // Set inner text
        cloudStorageChart.setInnerText("4.2GB free");
        localStorageChart.setInnerText("4.2GB free");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.overview_fragment, container, false);

    }

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }
}
