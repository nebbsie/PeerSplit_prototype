package com.aaronnebbs.peersplitandroidapplication.Model;

import java.io.File;

public class PeerSplitFile {
    public File file;
    public String location;

    public PeerSplitFile(File file, String location){
        this.file = file;
        this.location = location;
    }
}
