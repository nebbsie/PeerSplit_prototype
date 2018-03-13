package com.aaronnebbs.peersplitandroidapplication.Helpers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserManager {

    public static FirebaseAuth authentication;
    public static FirebaseUser user;

    // Setup the static variables.
    public static void setup(){
        authentication = FirebaseAuth.getInstance();
    }


}
