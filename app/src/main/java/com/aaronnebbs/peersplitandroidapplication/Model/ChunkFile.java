package com.aaronnebbs.peersplitandroidapplication.Model;

import java.io.File;

public class ChunkFile {
    private File file;
    private String originalname;
    private long size;
    private long originalFileSize;

    public ChunkFile(File file, String name, long size){
        this.file = file;
        this.originalname = name;
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public File getFile() {
        return file;
    }

    public String getOriginalname() {
        return originalname;
    }

}
