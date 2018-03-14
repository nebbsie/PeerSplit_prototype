package com.aaronnebbs.peersplitandroidapplication.Controllers;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.aaronnebbs.peersplitandroidapplication.Helpers.SwipeGestureListener;
import com.aaronnebbs.peersplitandroidapplication.R;

public class WelcomeController extends Activity {

    private ViewFlipper flipper;
    private Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        flipper = findViewById(R.id.welcome_flipper);
        start = findViewById(R.id.welcome_startButton);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), HomeController.class);
                startActivity(i);
            }
        });

        flipper.setOnTouchListener(new SwipeGestureListener(getApplicationContext()){
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                flipper.showPrevious();
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                flipper.showNext();
            }
        });

        flipper.showNext();
    }
}
