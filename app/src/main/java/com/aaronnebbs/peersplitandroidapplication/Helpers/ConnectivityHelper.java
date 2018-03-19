package com.aaronnebbs.peersplitandroidapplication.Helpers;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityHelper {

    private static boolean USING_WIFI;
    private static boolean USING_MOBILE;
    public static boolean CAN_UPLOAD;



    // Checks if the phone is using wifi or mobile networking.
    public static void checkWifi(Activity act){
        final ConnectivityManager connectivityManager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        USING_WIFI = false;
        USING_MOBILE = false;
        CAN_UPLOAD = false;

        if(wifi.isConnectedOrConnecting()){
            // Connected to wifi
            USING_WIFI = true;
        }else if(mobile.isConnectedOrConnecting()){
            // Connected to mobile
            USING_MOBILE = true;
        }
    }

    // Checks if the phone is allowed to upload.
    public static boolean canUploadChunk(Activity act){
        checkWifi(act);
        if(USING_WIFI){
            CAN_UPLOAD = true;
            return true;
        }else if (USING_MOBILE && SettingsHelper.getMobileNetwork()){
            CAN_UPLOAD = true;
            return true;
        }else{
            return false;
        }
    }
}
