package com.aaronnebbs.peersplitandroidapplication.Model;

import android.widget.ImageView;

public class HomePageRow {
    private String name;
    private String size;
    private String uid;

    public HomePageRow(String name, String size, String uid){
        this.name = name;
        this.size = size;
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
