package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.view.MenuItem;

import com.aaronnebbs.peersplitandroidapplication.Helpers.ChunkHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.Network.ChunkDownloader;
import com.aaronnebbs.peersplitandroidapplication.Helpers.Network.PeerSplitClient;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkLink;
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
import java.io.Serializable;
import java.util.ArrayList;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeController extends FragmentActivity implements Serializable {
    // Bottom navigation bar used on all pages.
    private BottomNavigationViewEx navBar;
    private long lastNumber;
    private boolean firstTime;
    private boolean alreadyRunning = false;

    // Fragments
    private Fragment selectedFragment;
    private HomeFragment homeActivity;
    private OverviewFragment overviewActivity;
    private ProfileFragment profileActivity;
    private SettingsFragment settingsActivity;

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
        }

        homeActivity = new HomeFragment();
        overviewActivity = new OverviewFragment();
        profileActivity = new ProfileFragment();
        settingsActivity = new SettingsFragment();

        setupFragments();
    }

    // Chunk listener does all chunk downloading/uploading.
    private void setupChunkListener(){
        // Thread used to download chunks to device.
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                DatabaseReference ref = UserManager.userDatabaseReference.getParent().child("chunks");
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Used to see if files are on device that don't need to be.
                        ArrayList<ChunkFile> filesOnBoard = ChunkHelper.getStoredChunks();
                        // Each user
                        for(DataSnapshot user : dataSnapshot.getChildren()){
                            User _user = user.getValue(User.class);
                            _user.setUserID(user.getKey());
                            // Don't download own chunks.
                            if(!_user.getUserID().equals(UserManager.user.getUid())){
                                // Each file for user
                                for(DataSnapshot file : user.getChildren()){
                                    // Each chunk for each file
                                    for(DataSnapshot chunk : file.getChildren()){
                                        ChunkLink c = chunk.getValue(ChunkLink.class);
                                        // Check if the chunk is for the current user.
                                        if(c.getUserID().equals(UserManager.user.getUid())){
                                            // If don't have the chunk in memory, download it.
                                            if(!ChunkHelper.searchForChunk(c.getChunkName())){
                                                // Get name of folder (filename no fullstops)
                                                String fileNameNoDots = c.getFileName().replace(".","");
                                                // Take the encoding and compression markings away
                                                fileNameNoDots = fileNameNoDots.substring(0, fileNameNoDots.length() - 5);
                                                // Concat to create download URL
                                                String fileToDownload = _user.getUserID() + "/" + fileNameNoDots + "/" + c.getChunkName();
                                                // Download file
                                                ChunkDownloader cd = new ChunkDownloader(c, _user.getUserID(),file.getKey() ,chunk.getKey());
                                                cd.execute(c.getChunkName(), fileToDownload, getFilesDir().getPath()+"/chunks/"+c.getFileName());
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
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        t.start();
    }

    // Setup the fragment holder.
    private void setupFragments(){
        // Setup the onclick listener for the bottom
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean upload = false;
                boolean isUpload = false;

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
                            Intent i = new Intent(getApplication(), UploadController.class);
                            startActivity(i);
                            isUpload = true;
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

    @Override
    protected void onStop() {
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
        System.out.println("on destroy");
        super.onDestroy();
    }
}


