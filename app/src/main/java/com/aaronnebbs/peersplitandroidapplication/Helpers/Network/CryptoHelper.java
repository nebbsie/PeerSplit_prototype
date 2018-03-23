package com.aaronnebbs.peersplitandroidapplication.Helpers.Network;

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
        getSavedKeys();
    }

    private static void getSavedKeys(){
        String keys_out = prefs.getString(SAVEDKEYS, "NULL");
        keys = new Gson().fromJson(keys_out, type);
    }

    private static void setSavedkeys(){
        String keys_in = new Gson().toJson(keys);
        prefs.edit().putString(SAVEDKEYS, keys_in);
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

    // Generates
    public static byte[] generateKey(String name){
        byte[] b = new byte[16];
        new Random().nextBytes(b);
        keys.add(new PrivateKeyPair(name, b));
        setSavedkeys();
        return b;
    }

}
