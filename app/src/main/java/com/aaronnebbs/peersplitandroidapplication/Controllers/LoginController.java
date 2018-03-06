package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.aaronnebbs.peersplitandroidapplication.R;

public class LoginController extends Activity {

    private EditText username;
    private EditText password;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setupUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start the animation
        this.overridePendingTransition(R.anim.push_right_enter, R.anim.push_right_exit);
    }

    // Attempts to login user.
    private void attemptLogin(){
        Intent i = new Intent(getApplication(), HomeController.class);
        startActivity(i);
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
        loginButton = findViewById(R.id.button_login);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

}
