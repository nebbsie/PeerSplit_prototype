package com.aaronnebbs.peersplitandroidapplication.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class ChunkHelper {

    public static SharedPreferences prefs;
    private static final String CHUNKS = "CHUNKS";
    private static ArrayList<ChunkFile> storedChunks;
    private static Type type = new TypeToken<ArrayList<ChunkFile>>(){}.getType();
    public static DatabaseReference ref;

    public static void setup(){
        storedChunks = new ArrayList<>();
        ref = UserManager.database.getReference().child("chunks");
        getStoredChunks();
    }

    // Gets the chunks stored in memory.
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

    // Searches the chunks that are currently stored to check if it contains another.
    public static boolean searchForChunk(String name){
        getStoredChunks();
        for(ChunkFile f : storedChunks){
            if(name.equals(f.getOriginalname())){
                return true;
            }
        }
        return false;
    }

    // Get amount of chunks stored in the phone.
    public static int getAmountOfChunksStored(){
        getStoredChunks();
        return storedChunks.size();
    }

    // Get size of all chunks stored.
    public static long getCurrentChunksStoredSize(){
        getStoredChunks();

        long counter = 0;
        for(ChunkFile c : storedChunks){
            counter += c.getSize();
        }
        return counter;
    }

    // Add a chunk to the file.
    public static void addStoredChunk(ChunkFile fileIn){
        storedChunks.add(fileIn);
        setStoredChunks();
    }

    // Clear all of the chunks of the device.
    public static void clearStoredChunks(){
        storedChunks = new ArrayList<>();
        setStoredChunks();
    }

    // Deletes un needed chunks.
    public static void deleteChunks(ArrayList<ChunkFile> chunksToDelete, Context context){
        for(ChunkFile c : chunksToDelete){
            String folderName = c.getOriginalname().substring(0, c.getOriginalname().length() - 10);
            String filePath = context.getFilesDir().getPath()+"/chunks/"+folderName+"/"+c.getFile().getName();
            File f = new File(filePath);
            f.delete();

            if(f.exists()){
                if(f.getParentFile().list().length == 0){
                    f.getParentFile().delete();
                }
            }


        }
        storedChunks.removeAll(chunksToDelete);
        setStoredChunks();
        getStoredChunks();
    }

}
