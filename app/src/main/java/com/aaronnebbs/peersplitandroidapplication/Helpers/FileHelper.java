package com.aaronnebbs.peersplitandroidapplication.Helpers;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Pair;
import com.aaronnebbs.peersplitandroidapplication.Model.ChunkFile;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class FileHelper {

    private static int blockSize = 1024;

    // Splits a file into chunks and returns an array with the files.
    public static ArrayList<ChunkFile> splitFileIntoChunks(File input, boolean deleteOriginalFile){
        // Array list of all chunks.
        ArrayList<ChunkFile> files = new ArrayList<>();
        // File to split into chunks, and get name.
        File baseFile = input;
        String fileName = baseFile.getName();
        // Counter to create numbered chunk name.
        int chunkCounter = 0;
        // Size of each chunk.
        //TODO: Make this automatic depending on file size and amount of users.
        int chunkSize = 1024 * (getSizeOfChunk(baseFile.length()));
        // Array to store the bytes into.
        byte[] dataBuffer = new byte[chunkSize];
        // Where the output chunks will be placed.
        String chunkLocation = input.getParent() +  "/chunks";
        // Create the location to place chunks and output.
        new File(chunkLocation).mkdirs();

        System.out.println("Splitting: " + fileName);
        System.out.println("Chunk Size: " + chunkSize / 1024 + " KB");
        try{
            int byteData;
            FileInputStream fis = new FileInputStream(baseFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            // Read in the the set amount of bytes per loop into dataBuffer
            // Check that the size is bigger than 0.
            while ((byteData = bis.read(dataBuffer)) > 0){
                // Create file name for chunk.
                String chunkName = String.format("%s.%03d", fileName, chunkCounter);
                // Make a new file for the chunk.
                ChunkFile chunk = new ChunkFile(new File(chunkLocation, chunkName));
                // Write the data to the new file.
                FileOutputStream out = new FileOutputStream(chunk.getFile());
                out.write(dataBuffer,0,byteData);
                // Increment the chunk counter.
                chunkCounter++;
                files.add(chunk);
            }
            System.out.println("Split into " + chunkCounter + " chunks.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Delete the original file after
        if(deleteOriginalFile){
            input.delete();
        }

        return files;
    }

    // Merge the files into one output file.
    public static File merge(File input) {
        // Get the files to merge.
        File[] chunks = input.getParentFile().listFiles();
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
        String outputFileName = strings[0] + "." + strings[1];
        File outputFile = new File( path + outputFileName);
        int sizeOfMerge = 0;

        try {
            FileOutputStream fos = new FileOutputStream(outputFile);
            BufferedOutputStream fileOut = new BufferedOutputStream(fos);
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
            new File(activity.getApplicationContext().getFilesDir() + "/" + returnCursor.getString(nameIndex)+"_data").mkdirs();
            // Get the input stream from file
            InputStream in =  activity.getContentResolver().openInputStream(uri);
            // Create a new file in a random location
            File file = new File(activity.getApplicationContext().getFilesDir() + "/" +  returnCursor.getString(nameIndex)+"_data", returnCursor.getString(nameIndex));
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

    // Compresses file into another file given.
    public static File compress(File input, File output, boolean deleteOriginalFile) throws IOException {

        // Check if the input is valid.
        if(!input.exists()){
            System.out.println("COMPRESSION: Input file not found! " + input.getPath());
            return null;
        }
        // Create the output location if it does not exist.
        if(!output.exists()){
            output.mkdirs();
        }

        // Input stream
        FileInputStream in = new FileInputStream(input);
        // Output stream
        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(output + ".gz"));
        // Buffer used to write to the output file.
        byte[] buffer = new byte[16 * blockSize];
        int len;
        // Write input file to output file until end of file.
        while((len=in.read(buffer)) != -1){
            out.write(buffer, 0, len);
        }
        // Close down streams.
        out.close();
        in.close();
        System.out.println("COMPRESSION: Compressed " + input.getName());

        // Checks if the original file should be deleted.
        if(deleteOriginalFile){
            input.delete();
        }
        return new File(output.getPath()+".gz");
    }

    // Decompresses a compressed file into the a new file.
    public static File decompress(File input, File output,boolean deleteOriginalFile) throws IOException {

        // Check if the input is valid.
        if(!input.exists()){
            System.out.println("DECOMPRESSION: Input file not found! " + input.getPath());
            return null;
        }
        // Create the output location if it does not exist.
        if(!output.exists()){
            output.mkdirs();
        }
        // Input stream
        GZIPInputStream in = new GZIPInputStream(new FileInputStream(input));
        // Output stream
        FileOutputStream out = new FileOutputStream(new File(output.getPath().substring(0, output.getPath().length()-3)));

        // Buffer used to write to the output file.
        byte[] buffer = new byte[16 * blockSize];
        int len;
        // Write input file to output file until end of file.
        while((len=in.read(buffer)) != -1){
            out.write(buffer, 0, len);
        }
        // Close down streams.
        out.close();
        in.close();

        // Checks if the original file should be deleted.
        if(deleteOriginalFile){
            input.delete();
        }

        return new File(output.getPath().substring(0, output.getPath().length()-2));
    }

    // Encrypts a file into a new file.
    public static File encrypt(byte[] key ,File input, File output, boolean deleteOriginalFile) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        // Check if the input is valid.
        if(!input.exists()){
            System.out.println("ENCRYPTION: Input file not found! " + input.getPath());
            return null;
        }
        // Create the output location if it does not exist.
        if(!output.exists()){
            output.mkdirs();
        }
        // Input stream
        FileInputStream fis = new FileInputStream(input);
        // Output stream
        FileOutputStream fos = new FileOutputStream(output + "enc");
        // Create the cipher using AES and using the private key
        SecretKeySpec sks = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        System.out.println("BLOCK SIZE: " + cipher.getBlockSize());

        cipher.init(Cipher.ENCRYPT_MODE, sks);

        // Create a cipher output stream.
        CipherOutputStream cos = new CipherOutputStream(fos, cipher);
        // Write bytes
        int len;
        byte[] data = new byte[blockSize * cipher.getBlockSize()];
        while((len = fis.read(data)) != -1) {
            cos.write(data, 0, len);
        }
        cos.flush();
        cos.close();
        fis.close();
        // Check if need to delete the original file.
        if(deleteOriginalFile){
            input.delete();
        }
        return new File(output.getPath() + "enc");
    }

    // Decrypts a file into a new file.
    public static File decrypt(byte[] key, File input, File output, boolean deleteOriginalFile) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
        // Check if the input is valid.
        if(!input.exists()){
            System.out.println("DECRYPTION: Input file not found! " + input.getPath());
            return null;
        }
        // Create the output location if it does not exist.
        if(!output.exists()){
            output.mkdirs();
        }
        // Input stream.
        FileInputStream fis = new FileInputStream(input);
        // Output stream.
        FileOutputStream fos = new FileOutputStream(new File(output.getPath().substring(0, output.getPath().length()-3)));
        // Create the cipher using AES and using the private key
        SecretKeySpec sks = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");

        cipher.init(Cipher.DECRYPT_MODE, sks);
        // Create a cipher input stream to read the file.
        CipherInputStream cis = new CipherInputStream(fis, cipher);
        int len;
        byte[] data = new byte[blockSize * cipher.getBlockSize()];
        while((len = cis.read(data)) != -1) {
            fos.write(data, 0, len);
        }
        fos.flush();
        fos.close();
        cis.close();
        // Check if need to delete the original file.
        if(deleteOriginalFile){
            input.delete();
        }
        return new File(output.getPath().substring(0, output.getPath().length()-3));
    }
}
