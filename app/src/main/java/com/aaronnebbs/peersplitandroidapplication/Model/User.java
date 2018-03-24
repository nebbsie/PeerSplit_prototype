package com.aaronnebbs.peersplitandroidapplication.Model;

import android.app.Activity;

import com.aaronnebbs.peersplitandroidapplication.Helpers.Network.ConnectivityHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;

public class User {
    private String username;
    private float deviceStorageAllocation;
    private boolean allowsDeviceStorage;
    private boolean canTransmitData;
    private long lastOnline;
    private long bytesRemaining;
    private String userID;

    public User(){

    }

    public User(String username, float deviceStorageAllocation, boolean allowsDeviceStorage, Activity act, String userID){
        this.username = username;
        this.deviceStorageAllocation = deviceStorageAllocation;
        this.allowsDeviceStorage = allowsDeviceStorage;
        this.canTransmitData = ConnectivityHelper.canUploadChunk(act);
        this.lastOnline = ConnectivityHelper.getEpochMinute();
        this.bytesRemaining = UserManager.getRemainingStorageSpace();//USED STORAGE
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public long getBytesRemaining() {
        return bytesRemaining;
    }

    public void setBytesRemaining(long bytesRemaining) {
        this.bytesRemaining = bytesRemaining;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public float getDeviceStorageAllocation() {
        return deviceStorageAllocation;
    }

    public void setDeviceStorageAllocation(float deviceStorageAllocation) {
        this.deviceStorageAllocation = deviceStorageAllocation;
    }

    public boolean isAllowsDeviceStorage() {
        return allowsDeviceStorage;
    }

    public void setAllowsDeviceStorage(boolean allowsDeviceStorage) {
        this.allowsDeviceStorage = allowsDeviceStorage;
    }

    public boolean isCanTransmitData() {
        return canTransmitData;
    }

    public void setCanTransmitData(boolean canTransmitData) {
        this.canTransmitData = canTransmitData;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public void  updateTime(){
        this.lastOnline =  ConnectivityHelper.getEpochMinute();
    }

}
