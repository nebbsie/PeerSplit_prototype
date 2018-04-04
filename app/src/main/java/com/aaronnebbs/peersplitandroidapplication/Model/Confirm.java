package com.aaronnebbs.peersplitandroidapplication.Model;

public class Confirm {
    private String chunkName;
    private String senderID;

    public Confirm(){

    }

    public Confirm(String str, String senderID){
        this.chunkName = str;
        this.senderID = senderID;

    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getChunkName() {
        return chunkName;
    }

    public void setChunkName(String chunkName) {
        this.chunkName = chunkName;
    }
}


