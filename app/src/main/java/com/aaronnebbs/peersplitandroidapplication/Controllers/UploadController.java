package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.aaronnebbs.peersplitandroidapplication.Helpers.FileHelper;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
import com.aaronnebbs.peersplitandroidapplication.Model.PeerSplitFile;
import com.aaronnebbs.peersplitandroidapplication.R;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

import az.plainpie.PieView;

public class UploadController extends Activity {

    private static final int READ_REQUEST_CODE = 42;
    private PieView uploadingChart;
    private LinearLayout selectedFileLayout;
    private TextView fileName;
    private TextView fileSize;
    private Button uploadButton;
    private TextView clickToSelectFile;
    private Button removeFile;
    private ProgressBar loadingBar;
    private TextView fileStatus;
    private Button goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_fragment);
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

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), HomeController.class);
                startActivity(i);
            }
        });
    }


    // Copies files from the local file system into a new file.
    private void copyFileForUpload(final Intent data){
        // Set ui to show file loading.
        loadingMode();
        // Get a copy of the file on a seperate thread to not lockup the ui.
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Get name and size of file for the UI.
                Uri uri = data.getData();
                Pair<String, String> pair = FileHelper.getNameandSize(uri, UploadController.this);
                // Set the UI to show the file name and size.
                fileName.setText(pair.first);
                fileSize.setText(pair.second);

                // Get a copy of the file
                File fileCopy = FileHelper.getFileFromURI(uri, UploadController.this);

                try {
                    File compressedFile = FileHelper.compress(fileCopy, fileCopy, true);
                    File decompressed = FileHelper.decompress(compressedFile, compressedFile ,true);
                    File encr = FileHelper.encrypt(decompressed, decompressed, true);
                    FileHelper.decrypt(encr, encr, true);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }

                // Run the change UI on the UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        uploadMode();
                    }
                });
            }
        });
        thread.start();
    }

    // Opens the file picker
    private void selectFile(){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    // Called when returning from an activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Check if the data selected is valid.
            if(data != null){
                copyFileForUpload(data);
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
        loadingBar.setVisibility(View.INVISIBLE);
    }

    // Sets the screen to the loading mode.
    private void loadingMode(){
        clickToSelectFile.setVisibility(View.INVISIBLE);
        selectedFileLayout.setVisibility(View.VISIBLE);
        uploadingChart.setVisibility(View.INVISIBLE);
        fileStatus.setText("WAITING FOR FILE TO LOAD");
        removeFile.setVisibility(View.VISIBLE);
        loadingBar.setVisibility(View.VISIBLE);
    }

    // Sets the file upload parts to visible and hides the select file button to invisible.
    private void uploadMode(){
        clickToSelectFile.setVisibility(View.INVISIBLE);
        selectedFileLayout.setVisibility(View.VISIBLE);
        uploadButton.setVisibility(View.VISIBLE);
        removeFile.setVisibility(View.VISIBLE);
        fileStatus.setText("READY TO UPLOAD");
        loadingBar.setVisibility(View.INVISIBLE);
        uploadingChart.setVisibility(View.VISIBLE);
    }

    // Links UI elements and sets the design for the progress bar.
    private void setupUI(){
        // Setup the widgets
        uploadingChart = findViewById(R.id.fileUploadingPieChart);
        selectedFileLayout = findViewById(R.id.fileSelectedBar);
        fileName = findViewById(R.id.fileName_upload);
        fileSize = findViewById(R.id.fileSize_upload);
        uploadButton = findViewById(R.id.uploadFileButton);
        clickToSelectFile = findViewById(R.id.clickToSelectFileButton);
        removeFile = findViewById(R.id.removeFile_upload);
        loadingBar = findViewById(R.id.upload_loadingBar);
        fileStatus = findViewById(R.id.upload_statusBarTitle);
        goBack = findViewById(R.id.fileUpload_back);

        // Set bar colour
        uploadingChart.setPercentageBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        // Set inner colour to white
        uploadingChart.setInnerBackgroundColor(getResources().getColor(R.color.white));
        // Set font colour
        uploadingChart.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        // Set the font size
        uploadingChart.setPercentageTextSize(13);
        // Set the percentage
        uploadingChart.setPercentage(1);
        // Set inner text
        uploadingChart.setInnerText("0%");
    }

    @Override
    public void onResume() {
        super.onResume();
        this.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }


}
