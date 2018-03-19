package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    public FirebaseDatabase database;
    public DatabaseReference userDatabaseReference;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Service Created", Toast.LENGTH_LONG).show();


    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {

        database = FirebaseDatabase.getInstance();
        userDatabaseReference = database.getReference("users");

        final Handler handler = new Handler();
        final int delay = 5000;

        handler.postDelayed(new Runnable(){
            public void run(){
                //System.out.println("WHY");
                userDatabaseReference.child("/tests/").push().setValue("yo! + " + new Date().getTime());
                handler.postDelayed(this, delay);
            }
        }, delay);

    }

}
