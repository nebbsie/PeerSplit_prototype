package com.aaronnebbs.peersplitandroidapplication.Helpers;

import android.content.SharedPreferences;
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

    private static void getSavedKeys(){
        String keys_out = prefs.getString(SAVEDKEYS, "NULL");
        if(keys_out.equals("NULL")){
            keys = new ArrayList<>();
        }else{
            keys = new Gson().fromJson(keys_out, type);
        }

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
