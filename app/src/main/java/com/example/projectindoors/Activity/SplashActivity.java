package com.example.projectindoors.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo;
    private static int splashTimeOut=5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        logo = (ImageView) findViewById(R.id.logo);
        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,MainActivities.class);
                startActivity(intent);
                finish();
            }
        },splashTimeOut);
        Animation indoor = AnimationUtils.loadAnimation(this,R.anim.indoorsplashanim);
        logo.startAnimation(indoor);
    }
}