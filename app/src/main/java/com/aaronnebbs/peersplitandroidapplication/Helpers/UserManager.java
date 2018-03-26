package com.aaronnebbs.peersplitandroidapplication.Helpers;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.os.PowerManager;

import com.aaronnebbs.peersplitandroidapplication.Helpers.Network.ConnectivityHelper;
import com.aaronnebbs.peersplitandroidapplication.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class UserManager {

    public static boolean DEBUGMODE = false;

    public static FirebaseAuth authentication;
    public static FirebaseUser user;
    public static FirebaseDatabase database;
    public static DatabaseReference userDatabaseReference;
    public static User userAccount;
    public static boolean loggedIn = false;
    public static boolean alreadyRunning = false;
    private static Handler handler;
    private static int delay = 10000;

    // Setup the static variables.
    public static void setup(){
        handler = new Handler();
        authentication = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userDatabaseReference = database.getReference("users");
    }

    public static void createUser(){
        userDatabaseReference.child(user.getUid()).setValue(userAccount);
    }
    // Currently the same as create as create does update, change this to only update what is necessary.
    public static void updateUser(){
        userAccount.setUsername(UserManager.user.getDisplayName());
        userAccount.setAllowsDeviceStorage(SettingsHelper.getChunkStorage());
        userAccount.setDeviceStorageAllocation(SettingsHelper.getStorageAmount());
        userAccount.setCanTransmitData(ConnectivityHelper.CAN_UPLOAD);
        userAccount.updateTime();
        userAccount.setBytesRemaining(getRemainingStorageSpace());
        userDatabaseReference.child(user.getUid()).setValue(userAccount);
    }

    public static void updateUserInCloud(final Context context){

        PowerManager mgr = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyWakeLock");
        wakeLock.acquire();

        final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

        handler.postDelayed(new Runnable(){
            public void run(){
                System.out.println("Updated Firebase: " + UserManager.user.getUid() + "  " + UserManager.userAccount.getUsername());
                // Reload the data used for an update.
                SettingsHelper.setup();
                // Check if the user can upload chu
                ConnectivityHelper.update(context);
                UserManager.updateUser();
                handler.postDelayed(this, delay);
            }
        }, 0);
    }

    public static long getRemainingStorageSpace(){
        //TODO: make it take into account the already stored stuff.
        return (long)((SettingsHelper.getStorageAmount() * 1e+9));
    }

    public static boolean getIfOnline(User user){
        long timeNow = ConnectivityHelper.getEpochMinute();
        long seconds = timeNow - user.getLastOnline();
        int offlineTime = 25;
        System.out.println("Since Update: " + seconds + " seconds." );

        if(seconds < offlineTime){
            return true;
        }else{
            return false;
        }
    }

}
