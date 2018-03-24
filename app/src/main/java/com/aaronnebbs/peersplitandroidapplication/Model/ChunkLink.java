package com.aaronnebbs.peersplitandroidapplication.Model;

/**
 * Created by Aaron on 24/03/2018.
 */

public class ChunkLink {
    private String userID;
    private String fileName;
    private String fileID;

    public ChunkLink(String userID, String fileName, String fileID) {
        this.userID = userID;
        this.fileName = fileName;
        this.fileID = fileID;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
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
