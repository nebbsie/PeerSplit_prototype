package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.view.MenuItem;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.aaronnebbs.peersplitandroidapplication.Views.HomeFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.OverviewFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.ProfileFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.SettingsFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.UploadFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import java.io.Serializable;

public class HomeController extends FragmentActivity implements Serializable {

    // Bottom navigation bar used on all pages.
    private BottomNavigationViewEx navBar;
    private long lastNumber;

    // Called when the page is created.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        setupNavBar();
        // Setup the onclick listener for the bottom
        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                boolean upload = false;

                switch (item.getItemId()){
                    case R.id.nav_home:
                        selectedFragment = HomeFragment.newInstance();
                        break;
                    case R.id.nav_overrview:
                        selectedFragment = OverviewFragment.newInstance();
                        break;
                    case R.id.nav_upload:
                        selectedFragment = UploadFragment.newInstance();
                        upload = true;
                        break;
                    case R.id.nav_profile:
                        selectedFragment = ProfileFragment.newInstance();
                        break;
                    case R.id.nav_settings:
                        selectedFragment = SettingsFragment.newInstance();
                        break;
                }
                // Set the fragment holder as the selected fragment.
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Work out what animation to use for the fragment transition.
                if(upload){
                    transaction.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
                }else{
                    if(item.getItemId() < lastNumber){
                        transaction.setCustomAnimations(R.anim.push_right_enter, R.anim.push_right_exit);
                    }else{
                        transaction.setCustomAnimations(R.anim.push_left_enter, R.anim.push_left_exit);
                    }
                }
                lastNumber = item.getItemId();
                transaction.replace(R.id.fragmentHolder, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, HomeFragment.newInstance());
        transaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the animation
        this.overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
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
}
