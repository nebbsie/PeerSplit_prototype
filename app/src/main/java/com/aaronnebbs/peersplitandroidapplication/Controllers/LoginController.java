package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.aaronnebbs.peersplitandroidapplication.Helpers.SettingsHelper;
import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import java.util.Set;

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
        setupUI();

        SettingsHelper.prefs = getSharedPreferences("com.aaronnebbs.peersplitandroidapplication", Context.MODE_PRIVATE);
        UserManager.setup();
        SettingsHelper.setup();

        if(SettingsHelper.AUTO_LOGIN){
            usernameStr = SettingsHelper.getEmail();
            passwordStr = SettingsHelper.getPassword();
            attemptLogin();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the animation
        this.overridePendingTransition(R.anim.push_right_enter, R.anim.push_right_exit);
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
                                UserManager.user = UserManager.authentication.getCurrentUser();
                                SettingsHelper.setLoginDetails(usernameStr, passwordStr);
                                Intent i = new Intent(getApplication(), WelcomeController.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(LoginController.this, "Failed To Signin!", Toast.LENGTH_SHORT).show();
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
}
