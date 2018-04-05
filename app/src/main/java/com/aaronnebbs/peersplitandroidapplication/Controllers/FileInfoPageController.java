package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aaronnebbs.peersplitandroidapplication.Helpers.ChunkHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.CryptoHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.FileHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.ImageSelector;
import com.aaronnebbs.peersplitandroidapplication.Helpers.JobHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkLink;
import com.aaronnebbs.peersplitandroidapplication.Model.Confirm;
import com.aaronnebbs.peersplitandroidapplication.Model.HomePageRow;
import com.aaronnebbs.peersplitandroidapplication.Model.JobType;
import com.aaronnebbs.peersplitandroidapplication.Model.PSFile;
import com.aaronnebbs.peersplitandroidapplication.Model.User;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.NoSuchPaddingException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FileInfoPageController extends Activity {

    private TextView fileName;
    private TextView fileSize;
    private ImageView downloadedImage;
    private Button downloadButton;
    private TextView currentStatus;
    private View fileInfoBar;
    private ProgressBar progressBar;
    private boolean downloadCheck;
    private PSFile fileDownloading;
    private HomePageRow row;
    private Button backButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_info_page_activity);

        fileName = findViewById(R.id.fileName_download);
        fileSize = findViewById(R.id.fileSize_download);
        downloadedImage = findViewById(R.id.imageDownloadedView);
        currentStatus = findViewById(R.id.fileInfoStatusText);
        fileInfoBar = findViewById(R.id.fileInfoBar);
        progressBar = findViewById(R.id.download_loadingBar);
        backButton = findViewById(R.id.fileInfo_back);
        downloadButton = findViewById(R.id.downloadFileButton);
        defaultView();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("psfile");
            String value2 = extras.getString("homepagerow");
            fileDownloading = new Gson().fromJson(value, PSFile.class);
            row =  new Gson().fromJson(value2, HomePageRow.class);
        }

        fileName.setText(fileDownloading.getFileName());
        fileSize.setText(FileHelper.getFileSizeString(fileDownloading.getTotalSize()));

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Attempt download
                downloadingView();
                downloadFile(row);
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), HomeController.class);
                startActivity(i);
            }
        });
    }

    // Sets a job in Firebase to tell device to upload chunk.
    private void setJobList(final ArrayList<ChunkLink> chunkLinks){
        System.out.println("Attempting to download " + chunkLinks.size() + " chunks.");

        // Send jobs to each device to upload the chunks.
        for (ChunkLink link : chunkLinks) {
            JobHelper.addJob(JobType.UPLOAD_CHUNK, link.getChunkName(), link.getUserID(), link.getFileID());
        }

        System.out.println("Looking for: " + chunkLinks.size() + " chunks!");

        downloadCheck = false;

        // Wait for the chunks to be uploaded and then download them.
        final ArrayList<String> chunksGot = new ArrayList<>();
        final ArrayList<ChunkFile> downloadedChunks = new ArrayList<>();
        JobHelper.confirmReference.child(UserManager.user.getUid()).child(chunkLinks.get(0).getFileID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Check if already downloaded the files
                if (downloadCheck == false) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        // Get confirmation for each chunk.
                        final Confirm c = d.getValue(Confirm.class);
                        // Check if the chunk is not already added.
                        if (!chunksGot.contains(c.getChunkName())){
                            // Add to the download list.
                            chunksGot.add(c.getChunkName());
                            // Delete the confirmation message.
                            JobHelper.confirmReference.child(UserManager.user.getUid()).child(chunkLinks.get(0).getFileID()).child(d.getKey()).removeValue();

                            String subString = c.getChunkName().substring(0, c.getChunkName().length() - 10);
                            final String folderString = subString.replace(".","");
                            final String fileDownloadLocation = c.getSenderID() + "/" + folderString + "/" + c.getChunkName();

                            // Attempt to download chunks.
                            ChunkHelper.downloadChunk(fileDownloadLocation).enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                    System.out.println("Successfully Downloaded Temp: " + c.getChunkName());
                                    // Save chunk in memory
                                    File out = ChunkHelper.saveChunkTemp(response.body(), getApplicationContext().getFilesDir()+"/temp/"+ folderString ,c.getChunkName());
                                    // Delete chunk from server
                                    ChunkHelper.deleteChunkFromServer(fileDownloadLocation, c.getSenderID(), folderString);
                                    System.out.println("Deleted from server");

                                    downloadedChunks.add(new ChunkFile(out, out.getName(), out.length()));
                                    if (downloadedChunks.size() == chunkLinks.size()) {
                                        // Put back together!
                                        putFileBackTogether(downloadedChunks);
                                    }
                                }
                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                    Toast.makeText(getApplicationContext(), "Failed to get file! Try again!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                    // Check if all confirmations have been checked, if they have stop listening.
                    if (chunksGot.size() == chunkLinks.size()) {
                        downloadCheck = true;
                        System.out.println("GOT ALL CHUNKS");
                        JobHelper.confirmReference.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void putFileBackTogether(final ArrayList<ChunkFile> chunks){
        if (chunks.size() > 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("merging");
                        currentStatus.setText("MERGING CHUNKS");
                        File mergedFile = FileHelper.merge(chunks.get(0).getFile());
                        String originalName = chunks.get(0).getOriginalname();
                        String keyName = originalName.substring(0, originalName.length()-10);
                        byte[] decryptKey = CryptoHelper.getKey(keyName);
                        System.out.println("decrypting");
                        currentStatus.setText("DECRYPTING FILE!");
                        File decryptedFile = decryptedFile = FileHelper.decrypt(decryptKey, mergedFile, mergedFile, true);
                        System.out.println("decompressing");
                        currentStatus.setText("UNCOMPRESSING FILE");
                        File output = FileHelper.decompress(decryptedFile, decryptedFile, true);
                        System.out.println("Normal File Ready");

                        if (ImageSelector.isImage(output.getName())) {
                            System.out.println("Image: " + output.getName() + " path: " + output.getPath());

                            /*Bitmap myBitmap = BitmapFactory.decodeFile(output.getPath());
                            downloadedImage.setImageBitmap(myBitmap);*/
                            success();
                            imageView();
                        }else {
                            success();
                        }


                    } catch (Exception e) {
                        error();
                    }
                }
            }).start();
        }
    }

    // Gets a list of available devices to get the chunks from.
    private void downloadFile(final HomePageRow fileToCheck){
        // Get information about users.
        final ArrayList<ChunkLink> availableToDownloadFrom = new ArrayList<>();
        final ArrayList<ChunkLink> finalDevicesToDownloadFrom = new ArrayList<>();

        // Get all users that have a chunk for the file stored on their device.
        ChunkHelper.ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    for (DataSnapshot file : user.getChildren()) {
                        if(file.getKey().equals(fileToCheck.getUid())) {
                            for (DataSnapshot chunk : file.getChildren()) {
                                ChunkLink link = chunk.getValue(ChunkLink.class);
                                if (link.isBeingStored()) {
                                    availableToDownloadFrom.add(link);
                                }
                            }
                        }
                    }
                }
                // Get all of the users and only add them if they are online.
                final ArrayList<User> users = new ArrayList<>();
                UserManager.userDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Loop through each user.
                        for (DataSnapshot user : dataSnapshot.getChildren()) {
                            User u = user.getValue(User.class);
                            u.setUserID(user.getKey());
                            // Only get users that are online
                            if(UserManager.getIfOnline(u)){
                                users.add(u);
                            }
                        }
                        // Only select chunks if the user is online and the chunk has not been added already.
                        // Check for multiple chunks.
                        for (ChunkLink chunk : availableToDownloadFrom) {
                            // Check if the chunk has already been added.
                            if(!checkDevices(chunk.getChunkName(), finalDevicesToDownloadFrom)){
                                // Check if the user is available.
                                if(getUserByID(chunk.getUserID(), users)){
                                    finalDevicesToDownloadFrom.add(chunk);
                                }
                            }
                        }


                        if (finalDevicesToDownloadFrom.size() == fileDownloading.getChunkAmount()) {
                            // Tell the selected devices to upload the chunks so they can be downloaded.
                            setJobList(finalDevicesToDownloadFrom);
                        }


                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // Searches for a user by UID in a list.
    private boolean getUserByID(String uid, ArrayList<User> users){
        for (User u : users) {
            if(uid.equals(u.getUserID())){
                return true;
            }
        }
        return false;
    }

    // Check if the list already contains the same chunk.
    private boolean checkDevices(String str, ArrayList<ChunkLink> arr){
        for(ChunkLink l : arr){
            if (l.getChunkName().equals(str)){
                return true;
            }
        }
        return false;
    }

    private void defaultView(){
        fileInfoBar.setVisibility(View.VISIBLE);
        downloadedImage.setVisibility(View.INVISIBLE);
    }

    private void success(){
        progressBar.setVisibility(View.INVISIBLE);
        currentStatus.setText("DOWNLOADED FILE");
        downloadButton.setVisibility(View.INVISIBLE);
    }

    private void error(){
        progressBar.setVisibility(View.INVISIBLE);
        currentStatus.setText("ERROR TRY AGAIN");
    }

    private void imageView(){
        fileInfoBar.setVisibility(View.INVISIBLE);
       downloadedImage.setVisibility(View.VISIBLE);
    }

    private void downloadingView(){
        currentStatus.setVisibility(View.VISIBLE);
        currentStatus.setText("DOWNLOADING CHUNKS");
        progressBar.setVisibility(View.VISIBLE);
    }
}
