package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.aaronnebbs.peersplitandroidapplication.Views.homeFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.overviewFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.profileFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.settingsFragment;
import com.aaronnebbs.peersplitandroidapplication.Views.uploadFragment;
import java.io.Serializable;
import java.lang.reflect.Field;

public class homeController extends FragmentActivity implements Serializable {

    // Bottom navigation bar used on all pages.
    private BottomNavigationView navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        // Setup the navigation bar.
        navBar = findViewById(R.id.bottomNavBar);
        removeShiftMode(navBar);

        navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.nav_home:
                        selectedFragment = homeFragment.newInstance();
                        break;
                    case R.id.nav_overrview:
                        selectedFragment = overviewFragment.newInstance();
                        break;
                    case R.id.nav_upload:
                        selectedFragment = uploadFragment.newInstance();
                        break;
                    case R.id.nav_profile:
                        selectedFragment = profileFragment.newInstance();
                        break;
                    case R.id.nav_settings:
                        selectedFragment = settingsFragment.newInstance();
                        break;
                }
                // Set the fragment holder as the selected fragment.
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmentHolder, selectedFragment);
                transaction.commit();
                return true;
            }
        });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentHolder, homeFragment.newInstance());
        transaction.commit();
    }

    @SuppressLint("RestrictedApi")
    public static void removeShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
                item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
                item.setChecked(item.getItemData().isChecked());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BottomNav", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BottomNav", "Unable to change value of shift mode", e);
        }
    }
}
