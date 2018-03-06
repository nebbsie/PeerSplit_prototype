package com.aaronnebbs.peersplitandroidapplication.Model;

import android.widget.ImageView;

public class HomePageRow {
    private String name;
    private String size;

    public HomePageRow(String name, String size){
        this.name = name;
        this.size = size;
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
