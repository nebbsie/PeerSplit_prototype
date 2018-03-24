package com.aaronnebbs.peersplitandroidapplication.Model;

/**
 * Created by Aaron on 24/03/2018.
 */

public class PSFile {
    private int chunkAmount;
    private long chunkSize;
    private String fileName;

    public PSFile(int chunkAmount, long chunkSize, String fileName) {
        this.chunkAmount = chunkAmount;
        this.chunkSize = chunkSize;
        this.fileName = fileName;
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
