package com.aaronnebbs.peersplitandroidapplication.Helpers;

import android.app.Activity;
import android.os.Handler;

import com.aaronnebbs.peersplitandroidapplication.Model.User;
import com.aaronnebbs.peersplitandroidapplication.Views.SettingsFragment;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserManager {

    public static FirebaseAuth authentication;
    public static FirebaseUser user;
    public static FirebaseDatabase database;
    public static DatabaseReference userDatabaseReference;
    public static User userAccount;

    // Setup the static variables.
    public static void setup(){
        authentication = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userDatabaseReference = database.getReference("users");
    }

    public static void createUser(){
        userDatabaseReference.child(user.getUid()).setValue(userAccount);
    }
    // Currently the same as create as create does update, change this to only update what is necessary.
    public static void updateUser(){
        userAccount.setAllowsDeviceStorage(SettingsHelper.getChunkStorage());
        userAccount.setDeviceStorageAllocation(SettingsHelper.getStorageAmount());
        userAccount.setCanTransmitData(ConnectivityHelper.CAN_UPLOAD);
        userAccount.updateTime();
        userDatabaseReference.child(user.getUid()).setValue(userAccount);
    }

    public static void setupStillOnlineHandler(final Activity act){
        final Handler handler = new Handler();
        final int delay = 10000;

        handler.postDelayed(new Runnable(){
            public void run(){
                // Reload the data used for an update.
                SettingsHelper.setup();
                ConnectivityHelper.canUploadChunk(act);
                updateUser();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

}
