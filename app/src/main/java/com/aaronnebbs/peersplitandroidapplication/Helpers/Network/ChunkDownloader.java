package com.aaronnebbs.peersplitandroidapplication.Helpers.Network;

import android.content.Context;
import android.os.AsyncTask;

import com.aaronnebbs.peersplitandroidapplication.Helpers.ChunkHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkLink;
import com.google.firebase.database.DatabaseReference;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ChunkDownloader extends AsyncTask<String, Void, Void> {

    private String fileName;
    private File outputFile;
    private ChunkLink link;
    private String originalUserID;
    private String chunkID;
    private String fileID;

    // string[0] == fileName   string[1] == fileDownloadURL string[2] == filePath

    public ChunkDownloader(ChunkLink link, String originalUserID,String fileID ,String chunkID){
        this.link = link;
        this.originalUserID = originalUserID;
        this.chunkID = chunkID;
        this.fileID = fileID;
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            URL u = new URL("http://peersplit.com/api/download.php");
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("POST");
            c.setDoInput(true);
            c.setDoOutput(true);

            OutputStream os = c.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            HashMap<String, String> a = new HashMap<>();
            a.put("fileToDownload", strings[1]);
            writer.write(getPostDataString(a));
            writer.flush();
            writer.close();
            os.close();

            // Create file to store data from server.
            File file = new File(strings[2]);
            file.mkdirs();
            outputFile = new File(file, strings[0]);
            fileName = outputFile.getName();

            // Copy data into the file.
            FileOutputStream fos = new FileOutputStream(outputFile);
            InputStream is = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1;
            while ((len1 = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fos.close();
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        ChunkFile f = new ChunkFile(outputFile, outputFile.getName(), outputFile.length());
        ChunkHelper.addStoredChunk(f);
        System.out.println("Downloaded Chunk: " + f.getOriginalname());
        link.setBeingStored(true);
        DatabaseReference ref = ChunkHelper.ref;
        ref.child(originalUserID).child(fileID).child(chunkID).setValue(link);
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
