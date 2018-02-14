package com.aaronnebbs.peersplitandroidapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class loginController extends Activity {

    private EditText username;
    private EditText password;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setupUI();
    }

    // Attempts to login user.
    private void attemptLogin(){

    }

    // Links to the register page.
    public void gotoRegisterPage(View v) {
        Intent i = new Intent(getApplication(), registerController.class);
        startActivity(i);
    }

    // Setup the ui links.
    private void setupUI(){
        username = findViewById(R.id.editText_username);
        password = findViewById(R.id.editText_password);
        loginButton = findViewById(R.id.button_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

}
