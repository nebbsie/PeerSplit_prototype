package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aaronnebbs.peersplitandroidapplication.Helpers.UserManager;
import com.aaronnebbs.peersplitandroidapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;

public class RegisterController extends Activity {
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText passwordAgain;
    private Button goBack;
    private TextView gotAccount;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the animation
        this.overridePendingTransition(R.anim.push_left_enter, R.anim.push_left_exit);
    }

    // Goes back to the login screen
    public void goToLoginScreen(){
        Intent i = new Intent(getApplication(), LoginController.class);
        startActivity(i);
    }

    // Attempt to register user after checking each text input.
    private void registerUser(){
        boolean attemptLogin = true;
        final String usernameStr = username.getText().toString();
        String emailStr = email.getText().toString();
        String passwordStr = password.getText().toString();
        String passwordAgainStr = passwordAgain.getText().toString();

        // Check if the username is empty.
        if(usernameStr.isEmpty()) {
            Toast.makeText(RegisterController.this, "Username Is Empty!", Toast.LENGTH_SHORT).show();
            attemptLogin = false;
        }
        // Check if the email string is not empty and also check if it has an @ symbol.
        if((emailStr.isEmpty() || !emailStr.contains("@") && attemptLogin)){
            Toast.makeText(RegisterController.this,"Invalid Email Address!", Toast.LENGTH_SHORT).show();
            attemptLogin = false;
        }
        // Check if the passwords are the same.
        if((passwordStr.isEmpty() || passwordAgainStr.isEmpty()) && attemptLogin){
            Toast.makeText(RegisterController.this,"Invalid Password!", Toast.LENGTH_SHORT).show();
            attemptLogin = false;
        }
        // Check if the passwords are the same.
        if((!passwordStr.equals(passwordAgainStr)) && attemptLogin){
            Toast.makeText(RegisterController.this,"Passwords Do Not Match!", Toast.LENGTH_SHORT).show();
            attemptLogin = false;
        }

        // Only attempt register if correct details are given.
        if(attemptLogin){
            // Attempt register
            UserManager.authentication.createUserWithEmailAndPassword(emailStr, passwordStr)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // User registered.
                            if (task.isSuccessful()) {
                                UserManager.user = UserManager.authentication.getCurrentUser();

                                // Set users username
                                UserProfileChangeRequest pUpdater = new UserProfileChangeRequest.Builder().setDisplayName(usernameStr).build();
                                UserManager.user.updateProfile(pUpdater);
                                Toast.makeText(RegisterController.this,"Created New User Account!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterController.this,"Failed To Register User Account!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    // Setup the UI elements
    private void setupUI(){
        username = findViewById(R.id.editText_usernameReg);
        email = findViewById(R.id.editText_emailReg);
        password = findViewById(R.id.editText_passwordReg);
        passwordAgain = findViewById(R.id.editText_passwordAgainReg);
        goBack = findViewById(R.id.button_gotoLoginPageReg);
        gotAccount = findViewById(R.id.button_gotAnAccount);
        registerButton = findViewById(R.id.button_register);

        // OnClick listeners
        gotAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginScreen();
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginScreen();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

}
