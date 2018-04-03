package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aaronnebbs.peersplitandroidapplication.Helpers.ChunkHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.FileHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.JobHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.Network.ConnectivityHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.CryptoHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.SettingsHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.Model.User;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

public class LoginController extends Activity {

    private EditText username;
    private EditText password;
    private Button loginButton;
    private String usernameStr;
    private String passwordStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Setup helpers.
        setupHelpers();
        // Setup the user interface.
        setupUI();
        ChunkHelper.clearStoredChunks();
        // Check if the application needs to auto login.
        if(SettingsHelper.AUTO_LOGIN){
            attemptAutoLogin();
        }
    }

    // Attempts auto login with params from shared prefs.
    private void attemptAutoLogin(){
        Toast.makeText(getApplicationContext(), "Attempting Auto-Login!", Toast.LENGTH_SHORT).show();
        usernameStr = SettingsHelper.getEmail();
        passwordStr = SettingsHelper.getPassword();
        attemptLogin();
    }

    // Setup the handlers and shared prefs.
    private void setupHelpers(){
        // Get the current time from api.
        ConnectivityHelper.getEpochMinute();
        // Setup the shared preferences.
        SettingsHelper.prefs = getSharedPreferences("com.aaronnebbs.peersplitandroidapplication", Context.MODE_PRIVATE);
        CryptoHelper.prefs = getSharedPreferences("com.aaronnebbs.peersplitandroidapplication", Context.MODE_PRIVATE);
        ChunkHelper.prefs = getSharedPreferences("com.aaronnebbs.peersplitandroidapplication", Context.MODE_PRIVATE);
        // Setup the database links and also user.
        UserManager.setup();
        // Load values from the share preferences.
        SettingsHelper.setup();
        // Setup the crypto.
        CryptoHelper.setup();
        // Setup the chunk helper.
        ChunkHelper.setup();
        // File helper setup.
        FileHelper.setup();
        // Job helper setup.
        JobHelper.setup();
    }

    // Attempts to login user.
    private void attemptLogin(){
        boolean attemptLogin = true;

        // Check if the username is empty.
        if(usernameStr.isEmpty()){
            Toast.makeText(LoginController.this, "Username Is Empty!", Toast.LENGTH_SHORT).show();
            attemptLogin = false;
        }
        // Check if the password is empty.
        if((passwordStr.isEmpty()) && attemptLogin){
            Toast.makeText(LoginController.this, "Password Is Empty!", Toast.LENGTH_SHORT).show();
            attemptLogin = false;
        }

        // Attempt login if valid variables are given.
        if(attemptLogin) {
            // Attempt login.
            UserManager.authentication.signInWithEmailAndPassword(usernameStr, passwordStr)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Set the firebase user information.
                                UserManager.user = UserManager.authentication.getCurrentUser();
                                // Set shared preferences.
                                SettingsHelper.setLoginDetails(usernameStr, passwordStr);
                                // Set the user information that is put into the database.
                                UserManager.userAccount = new User(UserManager.user.getDisplayName(),
                                        SettingsHelper.getStorageAmount(), SettingsHelper.getChunkStorage(),
                                        LoginController.this, UserManager.user.getUid());
                                // Updates the user information in the firebase app, mainly for the update of time.
                                // Used to check if a user is still online.
                                UserManager.updateUser();
                                // Start the next page.
                                Intent i = new Intent(getApplication(), HomeController.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(LoginController.this, "Failed To Sign in!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Links to the register page.
    public void gotoRegisterPage(View v) {
        Intent i = new Intent(getApplication(), RegisterController.class);
        startActivity(i);
    }

    // Setup the ui links.
    private void setupUI(){
        username = findViewById(R.id.editText_username);
        password = findViewById(R.id.editText_password);
        loginButton = findViewById(R.id.button_register);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameStr = username.getText().toString();
                passwordStr = password.getText().toString();
                attemptLogin();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the animation
        this.overridePendingTransition(R.anim.push_right_enter, R.anim.push_right_exit);
    }

    @Override
    protected void onDestroy() {
        System.out.println("on destroy login");
        super.onDestroy();
    }

}
