package com.aaronnebbs.peersplitandroidapplication.Model;

import java.io.File;

public class ChunkFile {
    private File file;
    private String chunksFolderLocation;

    public ChunkFile(File file, String loc){
        this.file = file;
        this.chunksFolderLocation = loc;
    }

    public File getFile() {
        return file;
    }

    public String getLocation() {
        return chunksFolderLocation;
    }

}
