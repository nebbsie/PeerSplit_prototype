package com.aaronnebbs.peersplitandroidapplication.Model;

public class Confirm {
    private String chunkName;

    public Confirm(){

    }

    public Confirm(String str){
        this.chunkName = str;
    }

    public String getChunkName() {
        return chunkName;
    }

    public void setChunkName(String chunkName) {
        this.chunkName = chunkName;
    }
}


