package com.aaronnebbs.peersplitandroidapplication.Model;

import android.app.Activity;

import com.aaronnebbs.peersplitandroidapplication.Helpers.Network.ConnectivityHelper;

import java.util.Date;

public class User {
    private String username;
    private float deviceStorageAllocation;
    private boolean allowsDeviceStorage;
    private boolean canTransmitData;
    private long lastOnline;
    private String ip;


    public User(String username, float deviceStorageAllocation, boolean allowsDeviceStorage, Activity act){
        this.username = username;
        this.deviceStorageAllocation = deviceStorageAllocation;
        this.allowsDeviceStorage = allowsDeviceStorage;
        this.canTransmitData = ConnectivityHelper.canUploadChunk(act);
        this.lastOnline = new Date().getTime();
        this.ip = ConnectivityHelper.getPublicIPAddress();
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
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
        this.lastOnline =  new Date().getTime();
    }

}
