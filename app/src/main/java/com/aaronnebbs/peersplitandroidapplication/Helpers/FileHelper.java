package com.aaronnebbs.peersplitandroidapplication.Helpers;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class FileHelper {

    // Returns a pretty string with correct initial at the end depending on file size.
    public static String getFileSizeString(File file){
        long bytes = file.length();
        // Check if MB should be the output.
        if(bytes > 1000000 && bytes < 1e+9){
            return String.valueOf(getFileSizeInMB(bytes)) + " MB";
        } else if(bytes > 1e+9){ // Check if GB should be the output.
            return String.valueOf(getFileSizeInGB(bytes)) + " GB";
        } else{ // Check if KB should be the output.
            return String.valueOf(getFileSizeInKB(bytes)) + " KB";
        }
    }

    // Get KB representation of the byte input.
    public static long getFileSizeInKB(long bytes){
        return bytes / 1024;
    }
    // Get MB representation of the byte input.
    public static long getFileSizeInMB(long bytes){
        return (bytes / 1024) / 1024;
    }
    // Get GB representation of the byte input.
    public static long getFileSizeInGB(long bytes){
        return ((bytes / 1024) / 1024) / 1024;
    }

    // Copies the data from the uri into a copy of the file.
    public static File getFileFromURI(Uri uri, Activity activity){
        // Get more infomration from the Uri
        Cursor returnCursor = activity.getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        System.out.println("Creating: " + returnCursor.getString(nameIndex) + "\nSize: " + returnCursor.getLong(sizeIndex));
        // Create a local copy of the file on phone
        try {
            // Get the input stream from file
            InputStream in =  activity.getContentResolver().openInputStream(uri);
            // Create a new file in a random location
            File file = new File(activity.getApplicationContext().getFilesDir(), returnCursor.getString(nameIndex));
            // Set an output stream into the new file
            FileOutputStream fos = new FileOutputStream(file);
            // Copy the contents of the original file to the new file
            byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0){
                fos.write(buf,0,len);
            }
            // Close the connections
            fos.close();
            in.close();
            // Return file if exists
            if(file.exists()){
                return file;
            }else{
                return null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
