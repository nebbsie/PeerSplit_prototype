package com.aaronnebbs.peersplitandroidapplication.Helpers;

import android.content.SharedPreferences;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ChunkHelper {

    public static SharedPreferences prefs;
    private static final String CHUNKS = "CHUNKS";
    private static ArrayList<ChunkFile> storedChunks;
    private static Type type = new TypeToken<ArrayList<ChunkFile>>(){}.getType();

    public static void setup(){
        storedChunks = new ArrayList<>();
        getStoredChunks();
    }

    public static ArrayList<ChunkFile> getStoredChunks(){
        String chunks = prefs.getString(CHUNKS, "NULL");
        if(!chunks.equals("NULL")){
            storedChunks = new Gson().fromJson(chunks, type);
        }
        return storedChunks;
    }

    public static void setStoredChunks(){
        prefs.edit().putString(CHUNKS, new Gson().toJson(storedChunks)).apply();
    }


}
