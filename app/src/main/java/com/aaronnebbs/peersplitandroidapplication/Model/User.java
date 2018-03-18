package com.aaronnebbs.peersplitandroidapplication.Model;


public class User {
    private String username;
    private int deviceStorageAllocation;
    private boolean allowsDeviceStorage;
    private boolean allowsMobileNetwork;
    private boolean online;

    public User(String username, int deviceStorageAllocation, boolean allowsDeviceStorage, boolean allowsMobileNetwork){
        this.username = username;
        this.deviceStorageAllocation = deviceStorageAllocation;
        this.allowsDeviceStorage = allowsDeviceStorage;
        this.allowsMobileNetwork = allowsMobileNetwork;
        this.online = true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getDeviceStorageAllocation() {
        return deviceStorageAllocation;
    }

    public void setDeviceStorageAllocation(int deviceStorageAllocation) {
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

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
