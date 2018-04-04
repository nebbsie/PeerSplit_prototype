package com.aaronnebbs.peersplitandroidapplication.Model;

/**
 * Created by Aaron on 24/03/2018.
 */

public class ChunkLink {
    private String userID;
    private String chunkName;
    private String fileName;
    private boolean beingStored;
    private String fileID;

    public ChunkLink(){

    }

    public ChunkLink(String userID, String chunkName, String fileName, String fileID) {
        this.userID = userID;
        this.chunkName = chunkName;
        this.fileName = fileName;
        this.beingStored = false;
        this.fileID = fileID;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public boolean isBeingStored() {
        return beingStored;
    }

    public void setBeingStored(boolean beingStored) {
        this.beingStored = beingStored;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getChunkName() {
        return chunkName;
    }

    public void setChunkName(String chunkName) {
        this.chunkName = chunkName;
    }



}
