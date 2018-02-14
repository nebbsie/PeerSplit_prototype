package com.aaronnebbs.peersplitandroidapplication;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class registerController extends Activity {

    private EditText username;
    private EditText email;
    private EditText password;
    private EditText passwordAgain;
    private Button goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        setupUI();
    }

    private void setupUI(){
        username = findViewById(R.id.editText_usernameReg);
        email = findViewById(R.id.editText_emailReg);
        password = findViewById(R.id.editText_passwordReg);
        passwordAgain = findViewById(R.id.editText_passwordAgainReg);
        goBack = findViewById(R.id.button_gotoLoginPageReg);

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), loginController.class);
                startActivity(i);
            }
        });
    }

}
