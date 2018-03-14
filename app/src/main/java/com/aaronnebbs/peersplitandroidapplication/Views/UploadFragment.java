package com.aaronnebbs.peersplitandroidapplication.Views;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aaronnebbs.peersplitandroidapplication.Helpers.FileHelper;
import com.aaronnebbs.peersplitandroidapplication.R;

import java.io.File;
import java.util.concurrent.ExecutionException;

import az.plainpie.PieView;

public class UploadFragment extends Fragment {

    private static final int READ_REQUEST_CODE = 42;
    private PieView uploadingChart;
    private LinearLayout selectedFileLayout;
    private TextView fileName;
    private TextView fileSize;
    private Button uploadButton;
    private TextView clickToSelectFile;
    private Button removeFile;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        selectionMode();

        clickToSelectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
                uploadMode();
            }
        });
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUpload();
            }
        });
        removeFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionMode();
            }
        });
    }

    // Opens the file picker
    private void selectFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    // Called when returning from an activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            // Check if the data selected is valid.
            if(data != null){
                Uri uri = data.getData();
                File fileCopy = FileHelper.getFileFromURI(uri, getActivity());
                fileName.setText(fileCopy.getName());
                fileSize.setText(FileHelper.getFileSizeString(fileCopy));
            }
        }
    }

    // Attempt to upload file.
    private void attemptUpload(){
        System.out.println("Attempting Upload");
    }

    // Sets the file upload parts to visible and hides the select file button to invisible.
    private void selectionMode(){
        clickToSelectFile.setVisibility(View.VISIBLE);
        selectedFileLayout.setVisibility(View.INVISIBLE);
        uploadButton.setVisibility(View.INVISIBLE);
        removeFile.setVisibility(View.INVISIBLE);
    }

    // Sets the file upload parts to visible and hides the select file button to invisible.
    private void uploadMode(){
        clickToSelectFile.setVisibility(View.INVISIBLE);
        selectedFileLayout.setVisibility(View.VISIBLE);
        uploadButton.setVisibility(View.VISIBLE);
        removeFile.setVisibility(View.VISIBLE);
    }

    // Links UI elements and sets the design for the progress bar.
    private void setupUI(){
        // Setup the widgets
        uploadingChart = getView().findViewById(R.id.fileUploadingPieChart);
        selectedFileLayout = getView().findViewById(R.id.fileSelectedBar);
        fileName = getView().findViewById(R.id.fileName_upload);
        fileSize = getView().findViewById(R.id.fileSize_upload);
        uploadButton = getView().findViewById(R.id.uploadFileButton);
        clickToSelectFile = getView().findViewById(R.id.clickToSelectFileButton);
        removeFile = getView().findViewById(R.id.removeFile_upload);
        // Set bar colour
        uploadingChart.setPercentageBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        // Set inner colour to white
        uploadingChart.setInnerBackgroundColor(getResources().getColor(R.color.white));
        // Set font colour
        uploadingChart.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        // Set the font size
        uploadingChart.setPercentageTextSize(13);
        // Set the percentage
        uploadingChart.setPercentage(50);
        // Set inner text
        uploadingChart.setInnerText("4.2GB free");
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
        return inflater.inflate(R.layout.upload_fragment, container, false);
    }
}
