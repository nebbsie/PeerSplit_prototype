package com.aaronnebbs.peersplitandroidapplication.Helpers.Network;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.aaronnebbs.peersplitandroidapplication.Helpers.SettingsHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class ConnectivityHelper{

    private static boolean USING_WIFI;
    private static boolean USING_MOBILE;
    public static boolean CAN_UPLOAD;
    public static String IP_ADDRESS;
    public static Long CURERNTTIME = 0L;


    public static long getEpochMinute(){
        new Thread() {
            public void run() {
                try {
                    URL getIP = new URL("http://peersplit.com/api/getTime.php");
                    BufferedReader in = new BufferedReader(new InputStreamReader(getIP.openStream()));
                    String time = in.readLine();
                    CURERNTTIME =  Long.valueOf(time);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return CURERNTTIME;
    }


    // Checks if the phone is using wifi or mobile networking.
    public static void checkWifi(Context act){
        final ConnectivityManager connectivityManager = (ConnectivityManager) act.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    public static void update(Context act){
        canUploadChunk(act);
    }

    // Checks if the phone is allowed to upload.
    public static boolean canUploadChunk(Context act){
        try{
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
        }catch (NullPointerException e){
            e.printStackTrace();
            System.out.println("Tired doing network check! Failed!");
            return CAN_UPLOAD;
        }
    }
}
