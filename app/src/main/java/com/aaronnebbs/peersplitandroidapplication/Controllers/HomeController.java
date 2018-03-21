package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.view.MenuItem;

import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.aaronnebbs.peersplitandroidapplication.Views.HomeFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.OverviewFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.ProfileFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.SettingsFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.Serializable;

public class HomeController extends FragmentActivity implements Serializable {
    // Bottom navigation bar used on all pages.
    private BottomNavigationViewEx navBar;
    private long lastNumber;
    private boolean firstTime;

    // Fragments
    private Fragment selectedFragment;
    private HomeFragment homeActivity;
    private OverviewFragment overviewActivity;
    private ProfileFragment profileActivity;
    private SettingsFragment settingsActivity;


    @Override
    protected void onStop() {
        if(profileActivity.created){
            profileActivity.onStop();
        }
        if(settingsActivity.created){
            settingsActivity.onStop();
        }
        super.onStop();
    }

    // Called when the page is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        firstTime = true;
        homeActivity = new HomeFragment();
        overviewActivity = new OverviewFragment();
        profileActivity = new ProfileFragment();
        settingsActivity = new SettingsFragment();


        setupNavBar();

        // Setup a handler that every n seconds updates the database.
        UserManager.setupStillOnlineHandler(this);

        // Setup the onclick listener for the bottom
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                boolean upload = false;
                boolean isUpload = false;

                // Check if the same tab is not clicked twice.
                if(item.getItemId() != lastNumber){
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = homeActivity;
                            break;
                        case R.id.nav_overrview:
                            selectedFragment = overviewActivity;
                            break;
                        case R.id.nav_upload:
                            Intent i = new Intent(getApplication(), UploadController.class);
                            startActivity(i);
                            isUpload = true;
                            break;
                        case R.id.nav_profile:
                            selectedFragment = profileActivity;
                            break;
                        case R.id.nav_settings:
                            selectedFragment = settingsActivity;
                            break;
                    }
                    // Set the fragment holder as the selected fragment.
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    if(!isUpload){
                        // Work out what animation to use for the fragment transition.
                        if(upload){
                            transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
                        }else{
                            if((item.getItemId() < lastNumber) && !firstTime){
                                transaction.setCustomAnimations(R.anim.push_right_enter, R.anim.push_right_exit);
                            }else{
                                transaction.setCustomAnimations(R.anim.push_left_enter, R.anim.push_left_exit);
                            }
                            firstTime = false;
                        }
                        lastNumber = item.getItemId();
                        transaction.replace(R.id.fragmentHolder, selectedFragment);
                        transaction.commit();
                        return true;
                    }

                }
                return false;
            }
        });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, homeActivity);
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
        UserManager.loggedIn = true;
    }

    // Setup the look and feel of the bottom navigation bar.
    private void setupNavBar(){
        // Setup the navigation bar.
        navBar = findViewById(R.id.bottomNavBar);
        navBar.enableAnimation(false);
        navBar.enableShiftingMode(false);
        navBar.enableItemShiftingMode(false);
        navBar.setIconSizeAt(0, 30, 30);
        navBar.getIconAt(0).setPadding(0, 25, 0, 0);
        navBar.setIconSizeAt(1, 30, 30);
        navBar.getIconAt(1).setPadding(0, 25, 0, 0);
        navBar.setIconSizeAt(2, 40, 40);
        navBar.setIconSizeAt(3, 30, 30);
        navBar.getIconAt(3).setPadding(0, 25, 0, 0);
        navBar.setIconSizeAt(4, 30, 30);
        navBar.getIconAt(4).setPadding(0, 25, 0, 0);
    }

    @Override
    protected void onDestroy() {
        System.out.println("on destroy");

        super.onDestroy();
    }
}


