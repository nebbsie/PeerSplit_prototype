package com.aaronnebbs.peersplitandroidapplication.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Toast;

import com.aaronnebbs.peersplitandroidapplication.Helpers.Network.ConnectivityHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.Network.PeerSplitClient;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkLink;
import com.google.firebase.database.DatabaseReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    // Searches the chunks that are currently stored to check if it contains another.
    public static ChunkFile getChunk(String name){
        getStoredChunks();
        for(ChunkFile f : storedChunks){
            if(name.equals(f.getOriginalname())){
                return f;
            }
        }
        return null;
    }

    // Deletes a chunk from the php cache server.
    public static void deleteChunkFromServer(final String str, final String userDir, final String fileDir){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String urlString = "http://peersplit.com/api/delete.php?location="+str+"&userDir="+userDir+"&fileDir="+fileDir;
                    URL getIP = new URL(urlString);
                    BufferedReader in = new BufferedReader(new InputStreamReader(getIP.openStream()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
            FileHelper.deleteRecursive(f.getParentFile());
        }
        storedChunks.removeAll(chunksToDelete);
        setStoredChunks();
        getStoredChunks();
    }

    // Uploads multiple chunks to the server.
    public static Call<ResponseBody> uploadChunks(final ArrayList<ChunkFile> chunks){
        // Generate the retrofit boilerplate.
        PeerSplitClient psc = RetrofitBuilderGenerator.generatePeerSplitClient();
        // Add each of the files to the params.
        List<MultipartBody.Part> partList = new ArrayList<>();
        for(ChunkFile f : chunks){
            partList.add(ConnectivityHelper.prepareFilePart(f.getFile().getName(), f.getFile()));
        }
        // Set the response to the upload files.
        return psc.uploadMultipleFilesDynamic(ConnectivityHelper.createPartFromString(UserManager.user.getUid()),partList);
    }

    // Uploads multiple chunks to the server.
    public static Call<ResponseBody> uploadChunk(final ChunkFile chunk, String id){
        // Generate the retrofit boilerplate.
        PeerSplitClient psc = RetrofitBuilderGenerator.generatePeerSplitClient();
        // Set the response to the upload file.
       return psc.uploadFile(ConnectivityHelper.createPartFromString(id),ConnectivityHelper.prepareFilePart(chunk.getFile().getName(), chunk.getFile()));
    }

    // Downloads a single chunk.
    public static Call<ResponseBody> downloadChunk(final String fileToDownload){
        PeerSplitClient psc = RetrofitBuilderGenerator.generatePeerSplitClient();
        // Set the responce to the uploadfiles.
        return psc.downloadFileWithFixedUrl(ConnectivityHelper.createPartFromString(fileToDownload));
    }

    // Saves a chunk when rebuilding a file.
    public static File saveChunkTemp(ResponseBody body, String location, String chunkName) {
        try {
            // Create file to store data from server.
            File file = new File(location);
            file.mkdirs();
            File futureStudioIconFile = new File(file, chunkName);

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return futureStudioIconFile;
            } catch (IOException e) {
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return null;
        }
    }

    // Saves a chunk and then updates firebase to say that it has been downloaded.
    public static boolean writeResponseBodyToDisk(ResponseBody body, String location, String chunkName, ChunkLink link, String originalUserID, String fileID, String chunkID) {
        try {
            // Create file to store data from server.
            File file = new File(location);
            file.mkdirs();
            File futureStudioIconFile = new File(file, chunkName);

            ChunkFile f = new ChunkFile(futureStudioIconFile, futureStudioIconFile.getName(), futureStudioIconFile.length());
            ChunkHelper.addStoredChunk(f);
            System.out.println("Saving Chunk To File: " + f.getOriginalname());
            link.setBeingStored(true);
            DatabaseReference ref = ChunkHelper.ref;
            ref.child(originalUserID).child(fileID).child(chunkID).setValue(link);

            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

}
