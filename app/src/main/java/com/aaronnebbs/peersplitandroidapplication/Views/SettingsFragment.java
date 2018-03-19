package com.aaronnebbs.peersplitandroidapplication.Views;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.aaronnebbs.peersplitandroidapplication.Controllers.HomeController;
import com.aaronnebbs.peersplitandroidapplication.Helpers.ConnectivityHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.SettingsHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.google.firebase.auth.AuthCredential;

public class SettingsFragment extends Fragment {

    private ConstraintLayout deleteAccountButton;
    private Switch mobileNetwork;
    private Switch chunkStorage;
    private Switch autoLogin;
    private EditText storageAllocation;
    public boolean created = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        created = true;
    }


    // Attempt to delete account.
    private void deleteAccount(){
        System.out.println("Delete Account");
    }

    @Override
    public void onStop() {
        super.onStop();
        // Check if the mobile network needs to be updating.
        if(SettingsHelper.MOBILE_NETWORK != mobileNetwork.isChecked()){
            SettingsHelper.setMobileNetwork(mobileNetwork.isChecked());
        }
        // Check if the auto login needs to be updating.
        if(SettingsHelper.AUTO_LOGIN != autoLogin.isChecked()){
            SettingsHelper.setAutoLogin(autoLogin.isChecked());
        }
        // Check if the chunk storage needs to be updating.
        if(SettingsHelper.CHUNK_STORAGE != chunkStorage.isChecked()){
            SettingsHelper.setChunkStorage(chunkStorage.isChecked());
        }
        // Check if the storage amount needs to be updating.
        if(SettingsHelper.STORAGE_AMOUNT != Float.parseFloat(storageAllocation.getText().toString())){
            SettingsHelper.setStorageAmount(Float.parseFloat(storageAllocation.getText().toString()));
        }

        // Update if the user can still upload data.
        ConnectivityHelper.canUploadChunk(getActivity());
        // Update the user in the cloud
        UserManager.updateUser();
    }

    private void setupUI(){
        mobileNetwork = getView().findViewById(R.id.settings_switch_mobileNetwork);
        chunkStorage = getView().findViewById(R.id.settings_switch_allowStorage);
        storageAllocation = getView().findViewById(R.id.settings_storageAllocation);
        autoLogin = getView().findViewById(R.id.settings_switch_autoLogin);
        // Set values from shared prefs
        mobileNetwork.setChecked(SettingsHelper.MOBILE_NETWORK);
        chunkStorage.setChecked(SettingsHelper.CHUNK_STORAGE);
        autoLogin.setChecked(SettingsHelper.AUTO_LOGIN);
        storageAllocation.setText(Float.toString(SettingsHelper.STORAGE_AMOUNT));
        // OnClick listeners
        deleteAccountButton = getView().findViewById(R.id.deleteAccountButton_settings);
        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_fragment, container, false);
    }

}
