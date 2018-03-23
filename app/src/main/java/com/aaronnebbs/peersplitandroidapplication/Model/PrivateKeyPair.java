package com.aaronnebbs.peersplitandroidapplication.Model;

public class PrivateKeyPair {
    private String name;
    private byte[] key;

    public PrivateKeyPair(String name, byte[] key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public byte[] getKey() {
        return key;
    }
}
