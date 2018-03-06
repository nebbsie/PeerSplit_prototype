package com.aaronnebbs.peersplitandroidapplication.Model;

public class DataFile {

    private String name;
    private int size;
    private int chunks;

    public DataFile(int size, int chunks, String name) {
        this.size = size;
        this.chunks = chunks;
        this.name = name;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getChunks() {
        return chunks;
    }

    public void setChunks(int chunks) {
        this.chunks = chunks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
