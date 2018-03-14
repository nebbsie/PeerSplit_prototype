package com.aaronnebbs.peersplitandroidapplication.Views;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aaronnebbs.peersplitandroidapplication.Helpers.FileHelper;
import com.aaronnebbs.peersplitandroidapplication.R;

import java.io.File;

import az.plainpie.PieView;

public class OverviewFragment extends Fragment {

    private PieView cloudStorageChart;
    private PieView localStorageChart;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        updateUI();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void updateUI(){
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSize() *(long)stat.getBlockCount();
        long megAvailable = bytesAvailable / 1048576;
        System.out.println("BYTES: " + megAvailable);
        localStorageChart.setPercentage((261f/532f)*100);
        localStorageChart.setInnerText("" + getPhoneSizeUsedBytes() + "/" + getPhoneSizeBytes() );
    }

    // Links UI elements and sets the design for the progress bar.
    private void setupUI(){
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
        cloudStorageChart.setPercentage(80);
        localStorageChart.setPercentage(25);
        // Set inner text
        cloudStorageChart.setInnerText("1.2GB free");
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.overview_fragment, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getPhoneSizeUsedBytes() {
        File p = Environment.getDataDirectory();
        StatFs s = new StatFs(p.getPath());
        long blockSize = s.getBlockSizeLong();
        long availableBlocks = s.getAvailableBlocksLong();
        return FileHelper.getFileSizeString(availableBlocks * blockSize);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public String getPhoneSizeBytes() {
        File p = Environment.getDataDirectory();
        StatFs s = new StatFs(p.getPath());
        long blockSize = s.getBlockSizeLong();
        long totalBlocks = s.getBlockCountLong();
        return  FileHelper.getFileSizeString(totalBlocks * blockSize);
    }


}
