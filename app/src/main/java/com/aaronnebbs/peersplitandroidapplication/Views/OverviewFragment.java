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
import android.widget.TextView;
import android.widget.Toast;

import com.aaronnebbs.peersplitandroidapplication.Helpers.ChunkHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.FileHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.Model.HomePageRow;
import com.aaronnebbs.peersplitandroidapplication.Model.PSFile;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.File;

import az.plainpie.PieView;

public class OverviewFragment extends Fragment {

    private PieView cloudStorageChart;
    private PieView localStorageChart;
    private TextView chunksCreatedByUser;
    private TextView chunksStoredByUser;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        updateUI();
        setupListenerForChunkData();
    }

    private void setupListenerForChunkData(){
        FileHelper.ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long totalStorageUsed = 0;
                long totalChunksCreated = 0;

                // For each file that is the users, add to the home page.
                for (DataSnapshot file : dataSnapshot.getChildren()){
                    PSFile f = file.getValue(PSFile.class);
                    if(f.getOwnerID().equals(UserManager.user.getUid())){
                        totalChunksCreated += f.getChunkAmount();
                        totalStorageUsed += f.getTotalSize();
                    }
                }

                // Clout storage calculations
                long usedSpace = (totalStorageUsed / 1024) / 1024;
                double percentage = ((double)usedSpace / (double)UserManager.cloudStorageLimit) * 100;

                if(percentage == 0){
                    percentage = 1;
                }

                long free = UserManager.cloudStorageLimit - usedSpace;
                cloudStorageChart.setPercentage((float)percentage);
                cloudStorageChart.setInnerText(free + " MB/"+UserManager.cloudStorageLimit+" MB");

                // Chunk storage ui
                chunksCreatedByUser.setText(""+totalChunksCreated);
                chunksStoredByUser.setText(""+ChunkHelper.getAmountOfChunksStored());
                Toast.makeText(getContext(), "CHUNKS: " + ChunkHelper.getAmountOfChunksStored(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void updateUI(){
        long usedSpace = ( getPhoneSizeUsedBytes() / 1024 ) / 1024;
        long totalSpace = ( getPhoneSizeBytes() / 1024 ) / 1024;
        double percentage =  ( (double)usedSpace / (double)totalSpace ) * 100;
        localStorageChart.setPercentage((float)percentage);
        localStorageChart.setInnerText("" + FileHelper.getFileSizeString(getPhoneSizeUsedBytes()) + "/" + FileHelper.getFileSizeString(getPhoneSizeBytes()) );
    }

    // Links UI elements and sets the design for the progress bar.
    private void setupUI(){
        // Setup the pie charts
        cloudStorageChart = getView().findViewById(R.id.cloudStoragePieChart);
        localStorageChart = getView().findViewById(R.id.localStoragePieChart);

        chunksCreatedByUser = getView().findViewById(R.id.chunksCreatedByYou);
        chunksStoredByUser = getView().findViewById(R.id.chunksYouAreStoring);

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
        chunksStoredByUser.setText(""+ChunkHelper.getStoredChunks().size());
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
    public long getPhoneSizeUsedBytes() {
        File p = Environment.getDataDirectory();
        StatFs s = new StatFs(p.getPath());
        long blockSize = s.getBlockSizeLong();
        long availableBlocks = s.getAvailableBlocksLong();
        return (availableBlocks * blockSize);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public long getPhoneSizeBytes() {
        File p = Environment.getDataDirectory();
        StatFs s = new StatFs(p.getPath());
        long blockSize = s.getBlockSizeLong();
        long totalBlocks = s.getBlockCountLong();
        return  (totalBlocks * blockSize);
    }


}
