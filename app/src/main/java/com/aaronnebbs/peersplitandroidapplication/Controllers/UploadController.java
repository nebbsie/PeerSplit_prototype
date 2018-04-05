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
import android.widget.Toast;

import com.aaronnebbs.peersplitandroidapplication.Helpers.ChunkHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.FileHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.CryptoHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkLink;
import com.aaronnebbs.peersplitandroidapplication.Model.PSFile;
import com.aaronnebbs.peersplitandroidapplication.Model.User;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.util.ArrayList;

import az.plainpie.PieView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
    private ArrayList<ChunkFile> chunks;
    private ArrayList<User> availibleUsers;
    private long orignalFileSize;

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
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Get name and size of file for the UI.
                Uri uri = data.getData();
                Pair<String, String> pair = FileHelper.getNameandSize(uri, UploadController.this);
                // Set the UI to show the file name and size.
                fileName.setText(pair.first);
                fileSize.setText(pair.second);
                try {
                    // Get a copy of the file.
                    File fileCopy = FileHelper.getFileFromURI(uri, UploadController.this);
                    orignalFileSize = fileCopy.length();
                    // Compress the file.
                    //fileStatus.setText("Compressing File");
                    File compressedFile = FileHelper.compress(fileCopy, fileCopy, true);
                    // Generate a private key
                    byte[] key = CryptoHelper.generateKey(fileCopy.getName());
                    // Encrypt the file.
                    //fileStatus.setText("Encrypting File");
                    final File encr = FileHelper.encrypt(key, compressedFile, compressedFile, true);

                    // Get the availible devices for storage.
                    UserManager.userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            availibleUsers = new ArrayList<>();
                            for(DataSnapshot s : dataSnapshot.getChildren()){
                                User user = s.getValue(User.class);
                                // Dont add users if they are own device.
                                if(!user.getUsername().equals(UserManager.userAccount.getUsername())){
                                    // Check if the wifi settings allow for transmission and if they allow storage.
                                    if(user.isCanTransmitData() && user.isAllowsDeviceStorage()){
                                        // Check if they are online.
                                        if(UserManager.getIfOnline(user)){
                                            // Check if device has enough storage.
                                            user.setUserID(s.getKey());
                                            availibleUsers.add(user);
                                        }
                                    }
                                }
                            }
                            System.out.println(availibleUsers.size() + " users found.");
                            // Split the file into chunks.
                            if(availibleUsers.size() == 0){
                                // Notify that no devices to distribute to.
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "No active devices to distribute to :(", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return;
                            }else{
                                chunks = FileHelper.splitFileIntoChunks(encr, true, availibleUsers.size());
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                } catch (Exception e) {
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

    // Distribute the chunks to all availible devices.
    private void selectDevicesForFiles(ArrayList<ChunkFile> chunks, ArrayList<User> availibleDevices){
        // Link to the root of firebase.
        DatabaseReference ref = UserManager.userDatabaseReference.getParent();
        // Hash the file name to get a unique id for file on firebase.
        int hashRes = chunks.get(0).getOriginalname().hashCode();
        String fileID = Integer.toHexString(hashRes);
        String nameWithoutEncoding = chunks.get(0).getOriginalname().substring(0, chunks.get(0).getOriginalname().length() - 6);
        // Create a file for use in firebase.
        PSFile file = new PSFile(chunks.size(), chunks.get(0).getFile().length(), nameWithoutEncoding, UserManager.user.getUid(), orignalFileSize);
        // Create a new file in firebase.
        ref.child("files").child(fileID).setValue(file);
        int availibleCounter = 0;
        // Go through all chunks
        for(ChunkFile c : chunks){
            // Make sure counter does not go over the max.
            if(availibleCounter > availibleDevices.size()){
                availibleCounter = 0;
            }
            String deviceID = availibleDevices.get(availibleCounter).getUserID();
            // Create a link so each device knows what file to download.
            ChunkLink link = new ChunkLink(deviceID, c.getFile().getName(), file.getFileName(), fileID);
            // Put the link on firebase
            ref.child("chunks").child(UserManager.user.getUid()).child(fileID).push().setValue(link);
            availibleCounter +=1;
        }
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
        // Upload the chunks to the server.
        loadingMode();
        fileStatus.setText("UPLOADING FILE");
        // Start the upload and set the callback.
        ChunkHelper.uploadChunks(chunks).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(getApplicationContext(), "Uploaded files!", Toast.LENGTH_SHORT).show();
                // Delete the temp files.
                FileHelper.deleteRecursive(chunks.get(0).getFile().getParentFile().getParentFile());
                // Selects what devices will recieve the chunks.
                selectDevicesForFiles(chunks, availibleUsers);
                uploadMode();
                fileStatus.setText("UPLOADED FILE");
                uploadingChart.setPercentage(100);
                uploadingChart.setInnerText("100%");
                uploadButton.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Failed to upload files!", Toast.LENGTH_SHORT).show();
            }
        });
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
        selectedFileLayout = findViewById(R.id.fileInfoBar);
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
