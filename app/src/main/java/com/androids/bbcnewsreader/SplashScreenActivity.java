package com.androids.bbcnewsreader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity that displays a splash screen while the app is loading.
 * The splash screen is shown for a specified duration before transitioning to the MainActivity.
 */
public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DISPLAY_LENGTH = 2000; // Duration of splash screen display (2 seconds)

    /**
     * Called when the activity is starting.
     * Initializes the splash screen and sets up a delay before transitioning to the MainActivity.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains the data it most recently supplied in {@link #onSaveInstanceState}.
     *                           Note: Otherwise it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Create a new Handler to post a delayed Runnable
        new Handler().postDelayed(() -> {
            // Create an Intent to start the MainActivity
            Intent mainIntent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(mainIntent);
            // Close the SplashScreenActivity
            finish();
        }, SPLASH_DISPLAY_LENGTH); // Delay for 2 seconds
    }
}