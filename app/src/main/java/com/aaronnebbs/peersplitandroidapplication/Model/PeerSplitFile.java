package com.aaronnebbs.peersplitandroidapplication.Model;

import java.io.File;

public class PeerSplitFile {

    public String originalName; // Used to put back to normal after compressing.
    public File file;
    public String location;

    public PeerSplitFile(File file, String location){
        this.file = file;
        this.originalName = file.getName();
        this.location = location;
    }
}
