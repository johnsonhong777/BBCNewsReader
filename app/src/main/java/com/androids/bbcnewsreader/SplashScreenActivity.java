package com.androids.bbcnewsreader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }, SPLASH_DISPLAY_LENGTH);
    }
}
