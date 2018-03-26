package com.aaronnebbs.peersplitandroidapplication.Model;

/**
 * Created by Aaron on 24/03/2018.
 */

public class ChunkLink {
    private String userID;
    private String fileName;

    public ChunkLink(){

    }

    public ChunkLink(String userID, String fileName) {
        this.userID = userID;
        this.fileName = fileName;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }



}
