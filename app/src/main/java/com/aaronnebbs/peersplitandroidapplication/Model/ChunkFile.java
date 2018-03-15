package com.aaronnebbs.peersplitandroidapplication.Model;

import java.io.File;

public class ChunkFile {
    private File file;
    private String name;

    public ChunkFile(File file, String loc){
        this.file = file;
        this.name = loc;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

}
