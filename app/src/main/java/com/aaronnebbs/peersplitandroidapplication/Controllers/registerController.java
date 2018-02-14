package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.aaronnebbs.peersplitandroidapplication.R;

public class registerController extends Activity {
    private EditText username;
    private EditText email;
    private EditText password;
    private EditText passwordAgain;
    private Button goBack;
    private TextView gotAccount;

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
        Intent i = new Intent(getApplication(), loginController.class);
        startActivity(i);
    }

    // Setup the UI elements
    private void setupUI(){
        username = findViewById(R.id.editText_usernameReg);
        email = findViewById(R.id.editText_emailReg);
        password = findViewById(R.id.editText_passwordReg);
        passwordAgain = findViewById(R.id.editText_passwordAgainReg);
        goBack = findViewById(R.id.button_gotoLoginPageReg);
        gotAccount = findViewById(R.id.button_gotAnAccount);

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
    }

}
