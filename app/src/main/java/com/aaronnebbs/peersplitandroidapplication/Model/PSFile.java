package com.aaronnebbs.peersplitandroidapplication.Model;

/**
 * Created by Aaron on 24/03/2018.
 */

public class PSFile {
    private int chunkAmount;
    private long chunkSize;
    private String fileName;
    private String ownerID;
    private long totalSize;

    public PSFile(){

    }

    public PSFile(int chunkAmount, long chunkSize, String fileName, String ownerID, long totalSize) {
        this.chunkAmount = chunkAmount;
        this.chunkSize = chunkSize;
        this.fileName = fileName;
        this.ownerID = ownerID;
        this.totalSize =totalSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public int getChunkAmount() {
        return chunkAmount;
    }

    public void setChunkAmount(int chunkAmount) {
        this.chunkAmount = chunkAmount;
    }

    public long getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(long chunkSize) {
        this.chunkSize = chunkSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
