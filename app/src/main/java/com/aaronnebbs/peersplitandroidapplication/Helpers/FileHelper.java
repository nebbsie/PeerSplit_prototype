package com.aaronnebbs.peersplitandroidapplication.Helpers;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.util.Pair;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
import com.aaronnebbs.peersplitandroidapplication.Model.PeerSplitFile;
import com.scottyab.aescrypt.AESCrypt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class FileHelper {

    public static byte[] key;
    public static byte[] initVector = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16, 17};

    // Splits a file into chunks and returns an array with the files.
    public static ArrayList<ChunkFile> splitFileIntoChunks(PeerSplitFile peerSplitFileIn){
        // Array list of all chunks.
        ArrayList<ChunkFile> files = new ArrayList<>();
        // File to split into chunks, and get name.
        File baseFile = peerSplitFileIn.file;
        String fileName = baseFile.getName();
        // Counter to create numbered chunk name.
        int chunkCounter = 0;
        // Size of each chunk.
        //TODO: Make this automatic depending on file size and amount of users.
        int chunkSize = 1024 * (getSizeOfChunk(baseFile.length()));
        // Array to store the bytes into.
        byte[] dataBuffer = new byte[chunkSize];
        // Where the output chunks will be placed.
        String chunkLocation = peerSplitFileIn.location + "/chunks";
        String chunkMergeLocation = peerSplitFileIn.location + "/mergeOutput";

        // Create the location to place chunks and output.
        new File(chunkLocation).mkdirs();
        new File(chunkMergeLocation).mkdirs();

       // new File(outputLocation).mkdirs();


        System.out.println("Splitting: " + fileName);
        System.out.println("Chunk Size: " + chunkSize / 1024 + " KB");
        System.out.println(chunkLocation);

        try{
            int byteData;
            FileInputStream fis = new FileInputStream(baseFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            // Read in the the set amount of bytes per loop into dataBuffer
            // Check that the size is bigger than 0.
            System.out.print("Splitting ");
            while ((byteData = bis.read(dataBuffer)) > 0){
                // Create file name for chunk.
                String chunkName = String.format("%s.%03d", fileName, chunkCounter);
                // Make a new file for the chunk.
                ChunkFile chunk = new ChunkFile(new File(chunkLocation, chunkName), chunkLocation);
                // Write the data to the new file.
                FileOutputStream out = new FileOutputStream(chunk.getFile());
                out.write(dataBuffer,0,byteData);
                // Increment the chunk counter.
                chunkCounter++;
                files.add(chunk);
                System.out.print(". ");
            }
            System.out.println("Split into " + chunkCounter + " chunks.");
            System.out.println("FILES: " + files.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }

    // Merge the files into one output file.
    public static File merge(String name) {


        // Get the files to merge.
        File[] chunks = new File(name).listFiles();
        Arrays.sort(chunks);
        List<File> files = Arrays.asList(chunks);
        // Return error message if no chunks are found.
        if(files.size() == 0){
            System.out.println("no chunks found!");
            return null;
        }
        // Get the first chunks name and split into strings to get name and extension.
        String fileName = files.get(0).getName();
        String[] strings = fileName.split("\\.");
        // Check if the has a correct name and extension to use for the output file.
        if(strings.length == 0){
            System.out.println("Failed to get filename and extension!");
            return null;
        }
        // Create the file name based on the first chunks information.
        String path = files.get(0).getParentFile().getParentFile()+"/mergeOutput/";
        String outputFileName = strings[0] + "." + strings[1]+".gz";
        File outputFile = new File( path + outputFileName);
        int sizeOfMerge = 0;
        // Merge the files.

        FileOutputStream fos = null;
        BufferedOutputStream fileOut = null;
        try {
            fos = new FileOutputStream(outputFile);
            fileOut = new BufferedOutputStream(fos);
            for (File f : files) {
                RandomAccessFile fl = new RandomAccessFile(f, "r");
                byte[] b = new byte[(int) f.length()];
                fl.readFully(b);
                fileOut.write(b);
                sizeOfMerge += f.length();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Merged " + files.size() + " file/s  Totaling: " + (sizeOfMerge / 1048576) + " MB");
        return outputFile;
    }

    // Returns the size of each chunk to be used for splitting.
    private static int getSizeOfChunk(long size){
        //TODO: set device number to a number of phones availible.
        int devices = 10;
        System.out.println("Devices Online: " + devices);
        long autoSize = (size / devices)/1024;
        int autoSizeRounded = roundUpToNearestPowerOfTwo(autoSize);
        return autoSizeRounded;
    }

    // Round number to the nearest power of two.
    private static int roundUpToNearestPowerOfTwo(long in){
        long num = in;
        num --;
        num |= num >> 1;
        num |= num >> 2;
        num |= num >> 4;
        num |= num >> 8;
        num |= num >> 16;
        num++;
        return (int)num;
    }

    //Returns the name and size of a file from the URI.
    public static Pair<String,String> getNameandSize(Uri uri, Activity activity){
        Cursor returnCursor = activity.getContentResolver().query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        return new Pair<>(returnCursor.getString(nameIndex), getFileSizeString(returnCursor.getLong(sizeIndex)));
    }

    // Returns a pretty string with correct initial at the end depending on file size.
    public static String getFileSizeString(File file){
        long bytes = file.length();
        // Check if MB should be the output.
        if(bytes > 1000000 && bytes < 1e+9){
            return Long.toString(getFileSizeInMB(bytes)) + " MB";
        } else if(bytes > 1e+9){ // Check if GB should be the output.
            return Long.toString(getFileSizeInGB(bytes)) + " GB";
        } else{ // Check if KB should be the output.
            return Long.toString(getFileSizeInKB(bytes)) + " KB";
        }
    }

    // Returns a pretty string with correct initial at the end depending on file size.
    public static String getFileSizeString(long bytes){
        // Check if MB should be the output.
        if(bytes > 1000000 && bytes < 1e+9){
            return Long.toString(getFileSizeInMB(bytes)) + " MB";
        } else if(bytes > 1e+9){ // Check if GB should be the output.
            return Long.toString(getFileSizeInGB(bytes)) + " GB";
        } else{ // Check if KB should be the output.
            return Long.toString(getFileSizeInKB(bytes)) + " KB";
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





    public static File compress(File input, File output) throws IOException {
        new File(input+"_data/compressed").mkdirs();
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(output));
        FileInputStream in = new FileInputStream(input);
        byte[] buffer = new byte[1024];
        int len;
        while((len=in.read(buffer)) != -1){
            out.write(buffer, 0, len);
        }
        out.close();
        in.close();
        return output;
    }


    public static void decompress(PeerSplitFile ps) throws IOException {
        new File(ps.location+"/decompressed/").mkdirs();
        GZIPInputStream in = new GZIPInputStream(new FileInputStream(new File(ps.location+"/decoded/"+ps.file.getName())));
        FileOutputStream out = new FileOutputStream(new File(ps.location+"/decompressed/" + ps.originalName));

        byte[] buffer = new byte[1024];
        int len;
        while((len = in.read(buffer)) != -1){
            out.write(buffer, 0, len);
        }
        out.close();
        in.close();
    }

    public static File encrypt(PeerSplitFile psFile) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Here you read the cleartext.
        FileInputStream fis = new FileInputStream(psFile.file);
        // This stream write the encrypted text. This stream will be wrapped by another stream.
        new File(psFile.location+"/encoded/").mkdirs();
        FileOutputStream fos = new FileOutputStream(psFile.location+"/encoded/"+psFile.file.getName());

        // Length is 16 byte
        // Careful when taking user input!!! https://stackoverflow.com/a/3452620/1188357
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
        // Create cipher
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sks);
        // Wrap the output stream
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int b;
        byte[] d = new byte[8];
        while((b = fis.read(d)) != -1) {
            cos.write(d, 0, b);
        }
        // Flush and close streams.
        cos.flush();
        cos.close();
        fis.close();
        return new File(psFile.location+"/encoded/"+psFile.file.getName());
    }

    public static void decrypt(PeerSplitFile psFile) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {

        FileInputStream fis = new FileInputStream(psFile.location+"/encoded/"+psFile.file.getName());

        new File(psFile.location+"/decoded/").mkdirs();
        FileOutputStream fos = new FileOutputStream(psFile.location+"/decoded/"+psFile.file.getName());
        SecretKeySpec sks = new SecretKeySpec("MyDifficultPassw".getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sks);
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int b;
        byte[] d = new byte[8];
        while((b = cis.read(d)) != -1) {
            fos.write(d, 0, b);
        }
        fos.flush();
        fos.close();
        cis.close();
    }




}
