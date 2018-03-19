package com.aaronnebbs.peersplitandroidapplication.Helpers;

import com.aaronnebbs.peersplitandroidapplication.Model.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserManager {

    public static FirebaseAuth authentication;
    public static FirebaseUser user;
    public static FirebaseDatabase database;
    public static DatabaseReference userDatabaseReference;
    public static User userAccount;

    // Setup the static variables.
    public static void setup(){
        authentication = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userDatabaseReference = database.getReference("users");
    }

    public static void createUser(){
        userDatabaseReference.child(user.getUid()).setValue(userAccount);
    }
    // Currently the same as create as create does update, change this to only update what is necessary.
    public static void updateUser(){
        userDatabaseReference.child(user.getUid()).setValue(userAccount);
    }

}
