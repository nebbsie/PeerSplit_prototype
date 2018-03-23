package com.aaronnebbs.peersplitandroidapplication.Model;

import java.io.File;

public class ChunkFile {
    private File file;

    public ChunkFile(File file){
        this.file = file;
    }

    public File getFile() {
        return file;
    }


}
