package com.aaronnebbs.peersplitandroidapplication.Helpers;

import android.content.SharedPreferences;


public class SettingsHelper {

    public static SharedPreferences prefs;

    private final static String AUTOLOGIN = "AUTOLOGIN";
    private final static String CHUNKSTORAGE = "CHUNKSTORAGE";
    private final static String MOBILENETWORK = "MOBILENETWORK";
    private final static String STORAGEAMOUNT = "STORAGEAMOUNT";
    private final static String EMAILSTR = "EMAIL";
    private final static String PASSWORDSTR = "PASSWORD";


    public static boolean AUTO_LOGIN;
    public static boolean CHUNK_STORAGE;
    public static boolean MOBILE_NETWORK;
    public static float STORAGE_AMOUNT;
    public static String EMAIL;
    public static String PASSWORD;

    public static void setup(){
        AUTO_LOGIN = prefs.getBoolean(AUTOLOGIN, false);
        CHUNK_STORAGE = prefs.getBoolean(CHUNKSTORAGE, false);
        MOBILE_NETWORK = prefs.getBoolean(MOBILENETWORK, false);
        STORAGE_AMOUNT = prefs.getFloat(STORAGEAMOUNT, 0);
        EMAIL = prefs.getString(EMAIL, "");
        PASSWORD = prefs.getString(PASSWORD, "");
    }

    // Set the auto login in shared preferences.
    public static void setAutoLogin(boolean bool){
        prefs.edit().putBoolean(AUTOLOGIN, bool).apply();
        AUTO_LOGIN = bool;
    }
    // Set the chunk storage in shared preferences.
    public static void setChunkStorage(boolean bool){
        prefs.edit().putBoolean(CHUNKSTORAGE, bool).apply();
        CHUNK_STORAGE = bool;
    }
    // Set the mobile network in shared preferences.
    public static void setMobileNetwork(boolean bool){
        prefs.edit().putBoolean(MOBILENETWORK, bool).apply();
        MOBILE_NETWORK = bool;
    }
    // Set the storage amount in shared preferences.
    public static void setStorageAmount(float data){
        prefs.edit().putFloat(STORAGEAMOUNT, data).apply();
        STORAGE_AMOUNT = data;
    }
    // Set the login details.
    public static void setLoginDetails(String email, String password){
        prefs.edit().putString(EMAILSTR, email).putString(PASSWORDSTR, password).apply();
        EMAIL = email;
        PASSWORD = password;
    }

    // Get the auto login in shared preferences.
    public static boolean getAutoLogin(){
        AUTO_LOGIN = prefs.getBoolean(AUTOLOGIN, false);
        return AUTO_LOGIN;
    }
    // Get the chunk storage in shared preferences.
    public static boolean getChunkStorage(){
        CHUNK_STORAGE = prefs.getBoolean(CHUNKSTORAGE, false);
        return CHUNK_STORAGE;
    }
    // Get the mobile network in shared preferences.
    public static boolean getMobileNetwork(){
        MOBILE_NETWORK = prefs.getBoolean(MOBILENETWORK, false);
        return MOBILE_NETWORK;
    }
    // Get the storage amount in shared preferences.
    public static float getStorageAmount(){
        STORAGE_AMOUNT = prefs.getFloat(STORAGEAMOUNT, 0f);
        return STORAGE_AMOUNT;
    }
    // Get the email from shared preferences.
    public static String getEmail(){
        EMAIL = prefs.getString(EMAILSTR, "");
        return EMAIL;
    }
    // Get the password from shared preferences.
    public static String getPassword(){
        PASSWORD = prefs.getString(PASSWORDSTR, "");
        return PASSWORD;
    }
}
