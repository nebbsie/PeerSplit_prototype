package com.aaronnebbs.peersplitandroidapplication.Helpers;

import android.content.SharedPreferences;

import com.aaronnebbs.peersplitandroidapplication.Model.HomePageRow;
import com.aaronnebbs.peersplitandroidapplication.Model.PrivateKeyPair;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Random;

public class CryptoHelper {

    private static final String SAVEDKEYS = "SAVEDKEYS";

    public static SharedPreferences prefs;
    private static ArrayList<PrivateKeyPair> keys;
    private static Type type = new TypeToken<ArrayList<PrivateKeyPair>>(){}.getType();

    public static void setup(){
        keys = new ArrayList<>();
        getSavedKeys();
        System.out.println("Found: " + keys.size() + " keys.");
    }

    public static ArrayList<PrivateKeyPair> getSavedKeys(){
        String keys_out = prefs.getString(SAVEDKEYS, "NULL");
        if(keys_out.equals("NULL")){
            keys = new ArrayList<>();
        }else{
            keys = new Gson().fromJson(keys_out, type);
        }
        return keys;
    }

    public static void removeKey(String name){
        PrivateKeyPair toDelete = new PrivateKeyPair("NULL", new byte[16]);
        for(PrivateKeyPair p : keys){
            if(p.getName().equals(name)){
                toDelete = p;
            }
        }
        if (!toDelete.getName().equals("NULL")) {
            keys.remove(toDelete);
            System.out.println("Removed Key: " + toDelete.getName());
        }
        setSavedkeys();
    }

    public static void removeUnusedKeys(ArrayList<HomePageRow> dataModels) {

        ArrayList<String> keysToDel = new ArrayList<>();

        for (PrivateKeyPair key : CryptoHelper.getSavedKeys()) {
            boolean toDelete = true;
            for (HomePageRow file : dataModels) {
                if (key.getName().equals(file.getName())) {
                    toDelete = false;
                }
            }
            if (toDelete) {
                keysToDel.add(key.getName());
            }
        }

        for (String str : keysToDel) {
            removeKey(str);
        }

        // Do deltetion
    }

    private static void setSavedkeys(){
        String keys_in = new Gson().toJson(keys);
        prefs.edit().putString(SAVEDKEYS, keys_in).apply();
    }

    // Searches for a key in the shared preferences to use for decryption.
    public static byte[] getKey(String name){
        for(PrivateKeyPair p : keys){
            if(p.getName().equals(name)){
                return p.getKey();
            }
        }
        return null;
    }

    // Generates a private key to be used.
    public static byte[] generateKey(String name){
        byte[] b = new byte[16];
        new Random().nextBytes(b);
        keys.add(new PrivateKeyPair(name, b));
        setSavedkeys();
        return b;
    }

    public static void clearKeys(){
        keys = new ArrayList<>();
        setSavedkeys();
    }

}
