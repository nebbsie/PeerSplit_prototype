package com.aaronnebbs.peersplitandroidapplication.Model;


import java.time.Instant;
import java.util.Date;

public class User {
    private String username;
    private float deviceStorageAllocation;
    private boolean allowsDeviceStorage;
    private boolean allowsMobileNetwork;
    private long lastOnline;
    private Date date;

    public User(String username, float deviceStorageAllocation, boolean allowsDeviceStorage, boolean allowsMobileNetwork){
        date = new Date();
        this.username = username;
        this.deviceStorageAllocation = deviceStorageAllocation;
        this.allowsDeviceStorage = allowsDeviceStorage;
        this.allowsMobileNetwork = allowsMobileNetwork;
        this.lastOnline = date.getTime();
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

    public boolean isAllowsMobileNetwork() {
        return allowsMobileNetwork;
    }

    public void setAllowsMobileNetwork(boolean allowsMobileNetwork) {
        this.allowsMobileNetwork = allowsMobileNetwork;
    }

    public long getLastOnline() {
        return lastOnline;
    }

    public void  updateTime(){
        this.lastOnline =  date.getTime();
    }

}
