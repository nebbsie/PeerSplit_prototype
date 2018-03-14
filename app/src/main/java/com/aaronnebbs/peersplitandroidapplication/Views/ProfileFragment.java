package com.aaronnebbs.peersplitandroidapplication.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.google.firebase.auth.UserProfileChangeRequest;


public class ProfileFragment extends Fragment {

    private EditText email;
    private EditText username;
    private EditText password;
    public boolean created = false;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        setData();
        created = true;
    }

    // Set the editTexts to the user information.
    private void setData(){
        email.setText(UserManager.user.getEmail());
        username.setText(UserManager.user.getDisplayName());
        password.setText("PASSWORD");
    }

    private void setupUI(){
        email = getView().findViewById(R.id.profile_name_editText);
        username = getView().findViewById(R.id.profile_username_editText);
        password = getView().findViewById(R.id.profile_password_editText);
    }

    @Override
    public void onStop() {
        super.onStop();

        // Only update the display name if it has been changed.
        if(UserManager.user.getDisplayName() != username.getText().toString()){
            UserProfileChangeRequest pUpdater = new UserProfileChangeRequest.Builder().setDisplayName(username.getText().toString()).build();
            UserManager.user.updateProfile(pUpdater);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
