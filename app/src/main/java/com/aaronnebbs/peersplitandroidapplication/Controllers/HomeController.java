package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.aaronnebbs.peersplitandroidapplication.Helpers.ChunkHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.FileHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.JobHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.Network.ConnectivityHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.Network.PeerSplitClient;
import com.aaronnebbs.peersplitandroidapplication.Helpers.RetrofitBuilderGenerator;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkLink;
import com.aaronnebbs.peersplitandroidapplication.Model.PSFile;
import com.aaronnebbs.peersplitandroidapplication.Model.User;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.aaronnebbs.peersplitandroidapplication.Views.HomeFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.OverviewFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.ProfileFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.SettingsFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeController extends FragmentActivity implements Serializable {
    // Bottom navigation bar used on all pages.
    private BottomNavigationViewEx navBar;
    private long lastNumber;
    private boolean firstTime;
    private boolean alreadyRunning = false;
    public boolean isUpload;
    // Fragments
    private Fragment selectedFragment;
    private HomeFragment homeActivity;
    private OverviewFragment overviewActivity;
    private ProfileFragment profileActivity;
    private SettingsFragment settingsActivity;

    private ArrayList<String> downloadedChunks = new ArrayList<>();

    private String originalUserID;
    private String chunkID;
    private String fileID;

    private Thread chunkDownloaderThread;


    private ArrayList<User> availibleUsers;
    ArrayList<User> usersToDistributeTo;
    private ArrayList<PSFile> files;
    private PSFile actualFile;


    // Called when the page is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        firstTime = true;
        // If the user is not valid, go back to login page.
        if(UserManager.user == null){
            Intent i = new Intent(getApplicationContext(), LoginController.class);
            startActivity(i);
        }
        // Make the navigation bar look good.
        setupNavBar();
        // Setup a handler that every n seconds updates the database.
        if(!alreadyRunning) {
            // Update the user in the cloud.
            UserManager.updateUserInCloud(getApplicationContext());
            // Handles all of the chunk interactions.
            setupChunkListener();
            JobHelper.setupJobListener();
        }

        // Setup fragments
        homeActivity = new HomeFragment();
        overviewActivity = new OverviewFragment();
        profileActivity = new ProfileFragment();
        settingsActivity = new SettingsFragment();
        setupFragments();
    }

    // Chunk listener does all chunk downloading/uploading.
    private void setupChunkListener(){
        // Thread used to download chunks to device.
        chunkDownloaderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                final DatabaseReference ref = UserManager.userDatabaseReference.getParent().child("chunks");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //TODO: Put this as a job.
                        // Used to see if files are on device that don't need to be.
                        ArrayList<ChunkFile> filesOnBoard = ChunkHelper.getStoredChunks();
                        // Each user
                        for(final DataSnapshot user : dataSnapshot.getChildren()){
                            final User _user = user.getValue(User.class);
                            _user.setUserID(user.getKey());
                            // Don't download own chunks.
                            if(!_user.getUserID().equals(UserManager.user.getUid())){
                                // Each file for user
                                for(final DataSnapshot file : user.getChildren()){
                                    // Each chunk for each file
                                    for(DataSnapshot chunk : file.getChildren()){
                                        final ChunkLink c = chunk.getValue(ChunkLink.class);
                                        // Check if the chunk is for the current user.
                                        if(c.getUserID().equals(UserManager.user.getUid())){
                                            // If don't have the chunk in memory, download it.
                                            if(!ChunkHelper.searchForChunk(c.getChunkName())){
                                                if(!c.isBeingStored()){
                                                    // Get name of folder (filename no fullstops)
                                                    originalUserID = _user.getUserID();
                                                    chunkID = chunk.getKey();
                                                    fileID = file.getKey();
                                                    // Download file and delete when finished.
                                                    final String fileNameNoDots = c.getFileName().replace(".","");
                                                    final String fileToDelete = _user.getUserID() + "/" + fileNameNoDots  + "/" + c.getChunkName();
                                                    final String fileDownloadLocation = getFilesDir().getPath()+"/chunks/"+c.getFileName();

                                                    if (!checkIfDownloaded(chunkID)) {
                                                        downloadedChunks.add(chunkID);
                                                        // Start the upload and set the callback.
                                                        System.out.println("TRYING: " + fileToDelete);
                                                        Call<ResponseBody> call = ChunkHelper.downloadChunk(fileToDelete);
                                                        call.enqueue(new Callback<ResponseBody>() {
                                                            @Override
                                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                System.out.println("response: " + response.body().contentLength());
                                                                Toast.makeText(getApplicationContext(), "downloaded files!", Toast.LENGTH_SHORT).show();
                                                                ChunkHelper.writeResponseBodyToDisk(response.body(), fileDownloadLocation, c.getChunkName(), c, originalUserID, fileID, chunkID);
                                                                //ChunkHelper.deleteChunkFromServer(fileToDelete, _user.getUserID(), fileNameNoDots );
                                                            }
                                                            @Override
                                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                Toast.makeText(getApplicationContext(), "Failed to download chunk!", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                }
                                            }else{
                                                // Check if local storage has files that have been deleted from cloud and are no longer needing to be hosted.
                                                ArrayList<ChunkFile> toDelete = new ArrayList<>();
                                                // Go through each file and
                                                for(ChunkFile cf : filesOnBoard){
                                                    if(c.getChunkName().equals(cf.getOriginalname())){
                                                        toDelete.add(cf);
                                                    }
                                                }
                                                filesOnBoard.removeAll(toDelete);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        // Delete un needed chunks.
                        ChunkHelper.deleteChunks(filesOnBoard, getApplicationContext());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }
        });
        chunkDownloaderThread.start();
    }


    private boolean checkIfDownloaded(String str){
        for (String uid: downloadedChunks) {
            if (uid.equals(str)){
                return true;
            }
        }
        return false;
    }


    // Setup the fragment holder.
    private void setupFragments(){
        // Setup the onclick listener for the bottom
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean upload = false;
                isUpload = false;

                // Check if the same tab is not clicked twice.
                if(item.getItemId() != lastNumber){
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = homeActivity;
                            break;
                        case R.id.nav_overrview:
                            selectedFragment = overviewActivity;
                            break;
                        case R.id.nav_upload:
                            isUpload = true;
                            Intent i = new Intent(getApplication(), UploadController.class);
                            startActivity(i);
                            break;
                        case R.id.nav_profile:
                            selectedFragment = profileActivity;
                            break;
                        case R.id.nav_settings:
                            selectedFragment = settingsActivity;
                            break;
                    }
                    // Set the fragment holder as the selected fragment.
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    if(!isUpload){
                        // Work out what animation to use for the fragment transition.
                        if(upload){
                            transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
                        }else{
                            if((item.getItemId() < lastNumber) && !firstTime){
                                transaction.setCustomAnimations(R.anim.push_right_enter, R.anim.push_right_exit);
                            }else{
                                transaction.setCustomAnimations(R.anim.push_left_enter, R.anim.push_left_exit);
                            }
                            firstTime = false;
                        }
                        lastNumber = item.getItemId();
                        transaction.replace(R.id.fragmentHolder, selectedFragment);
                        transaction.commit();
                        return true;
                    }

                }
                return false;
            }
        });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, homeActivity);
        transaction.commit();
    }

    // Setup the look and feel of the bottom navigation bar.
    private void setupNavBar(){
        // Setup the navigation bar.
        navBar = findViewById(R.id.bottomNavBar);
        navBar.enableAnimation(false);
        navBar.enableShiftingMode(false);
        navBar.enableItemShiftingMode(false);
        navBar.setIconSizeAt(0, 30, 30);
        navBar.getIconAt(0).setPadding(0, 25, 0, 0);
        navBar.setIconSizeAt(1, 30, 30);
        navBar.getIconAt(1).setPadding(0, 25, 0, 0);
        navBar.setIconSizeAt(2, 40, 40);
        navBar.setIconSizeAt(3, 30, 30);
        navBar.getIconAt(3).setPadding(0, 25, 0, 0);
        navBar.setIconSizeAt(4, 30, 30);
        navBar.getIconAt(4).setPadding(0, 25, 0, 0);
    }

    private void moveChunksToOtherDevices() {

        new Thread(new Runnable() {
            @Override
            public void run() {
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

                        System.out.println("Devices Availible To Move Chunks To: "  + availibleUsers.size());

                        // Get all chunks in memory
                        final ArrayList<ChunkFile> storedChunks = ChunkHelper.getStoredChunks();

                        // If got chunks
                        if (storedChunks.size() > 0) {
                            // Get all files from server
                            FileHelper.ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    files = new ArrayList<>();
                                    // Loop through each file
                                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                                        files.add(d.getValue(PSFile.class));
                                    }

                                    // Go through each chunk file.
                                    for (final ChunkFile cf : storedChunks) {

                                        // Get name of original file from chunk file.
                                        final String chunkToFileName = cf.getOriginalname().substring(0, cf.getOriginalname().length() - 10);
                                        // Go through each file.
                                        for (PSFile file : files) {

                                            // Only do work if the correct file is found
                                            if (file.getFileName().equals(chunkToFileName)) {
                                                actualFile = file;
                                                usersToDistributeTo = new ArrayList<>();
                                                System.out.println("File: " + file.getFileName() + "  chunks: " + file.getChunkAmount() );
                                                // Go through each user
                                                for (User user : availibleUsers) {
                                                    System.out.println(user.getUsername());
                                                    if (!user.getUserID().equals(file.getOwnerID())) {
                                                        System.out.println("USERL " + user.getUsername());
                                                        usersToDistributeTo.add(user);
                                                    }
                                                }
                                            }
                                        }

                                        final ChunkFile f = new ChunkFile(cf.getFile(), cf.getOriginalname(), cf.getSize());
                                        System.out.println("Found " + usersToDistributeTo.size() + " to distribute to");
                                        // Only update once they are online
                                        // TODO: maybe do this anyway? Store on the server until they come online.
                                        if (usersToDistributeTo.size() > 0) {
                                            System.out.println("INSIDE");
                                            // Upload chunk to server
                                            //TODO: Fix the id when change to random user.
                                            ChunkHelper.uploadChunk(f, usersToDistributeTo.get(0).getUserID()).enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    System.out.println("Uploaded Chunk To Server");
                                                    Toast.makeText(getApplicationContext(), "Distributed Chunks To Server!", Toast.LENGTH_SHORT).show();
                                                     // Removes the link on firebase that says this device has the file.
                                                    removeFileLink(cf, chunkToFileName, actualFile.getOwnerID());
                                                    // Select the devices to download the file.
                                                    selectDeviceForFile(usersToDistributeTo, f, chunkToFileName, actualFile.getOwnerID(), actualFile.getFileName());
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                }
                                            });
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) { }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        }).start();
    }

    private void removeFileLink(final ChunkFile file, String fileName, final String orignalFileUserID){
        // Hash the file name to get a unique id for file on firebase.
        fileName += ".gzenc";
        int hashRes = fileName.hashCode();
        final String fileID = Integer.toHexString(hashRes);
        // Get chunk links for the correct file
        ChunkHelper.ref.child(orignalFileUserID).child(fileID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ChunkLink c = d.getValue(ChunkLink.class);
                    // Get the chunk link for the correct chunk.
                    if (c.getUserID().equals(UserManager.user.getUid())) {
                        ChunkHelper.ref.child(orignalFileUserID).child(fileID).child(d.getKey()).removeValue();
                        System.out.println("Removed Chunk Link");
                        //FileHelper.deleteRecursive(file.getFile().getParentFile().getParentFile());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    private void selectDeviceForFile(ArrayList<User> users, ChunkFile file, String name, String originID, String originalFileName){
        if (users.size() > 0) {
            name += ".gzenc";
            int hashRes = name.hashCode();
            final String fileID_link = Integer.toHexString(hashRes);
            //TODO: Chnage this to a kindoff random number
            User u = users.get(0);
            System.out.println("User To Recieve: " + u.getUsername() + "  User ID: " + u.getUserID());
            ChunkLink link = new ChunkLink(u.getUserID(), file.getFile().getName(), originalFileName, fileID_link);

            System.out.println("TESTING:    Name: " + link.getFileName() + "    "  + file.getOriginalname() +  "    " + originalFileName);

            DatabaseReference ref = UserManager.userDatabaseReference.getParent();
            ref.child("chunks").child(originID).child(fileID_link).push().setValue(link);
        }
    }

    @Override
    protected void onStop() {
        System.out.println("App Closed Down, Upload Chunks!");

        if (chunkDownloaderThread.isAlive() == false && !isUpload) {
            System.out.println("MOVE CHUNKS");
            moveChunksToOtherDevices();
        }

        if(profileActivity.created){
            profileActivity.onStop();
        }
        if(settingsActivity.created){
            settingsActivity.onStop();
        }

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
        UserManager.loggedIn = true;
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}


