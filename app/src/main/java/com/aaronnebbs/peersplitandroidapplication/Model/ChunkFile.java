package com.aaronnebbs.peersplitandroidapplication.Model;

import java.io.File;

public class ChunkFile {
    private File file;
    private String originalname;
    private int size;

    public ChunkFile(File file, String name, int size){
        this.file = file;
        this.originalname = name;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public File getFile() {
        return file;
    }

    public String getOriginalname() {
        return originalname;
    }

}
