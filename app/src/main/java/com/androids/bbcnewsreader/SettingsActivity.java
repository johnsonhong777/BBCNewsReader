package com.androids.bbcnewsreader;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

/**
 * Activity for managing application settings, including language selection.
 */
public class SettingsActivity extends AppCompatActivity {
    private String selectedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        RadioGroup languageGroup = findViewById(R.id.language_group);
        Button confirmButton = findViewById(R.id.confirm_button);

        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = sharedPreferences.getString("My_Lang", "en");
        if (language.equals("fr")) {
            ((RadioButton) findViewById(R.id.radio_french)).setChecked(true);
            selectedLanguage = "fr";
        } else {
            ((RadioButton) findViewById(R.id.radio_english)).setChecked(true);
            selectedLanguage = "en";
        }

        languageGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_english) {
                selectedLanguage = "en";
            } else if (checkedId == R.id.radio_french) {
                selectedLanguage = "fr";
            }
        });

        confirmButton.setOnClickListener(v -> {
            if (selectedLanguage != null) {
                setLocale(selectedLanguage);
            }
        });
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();

        // Restart the MainActivity to apply the language change
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);  // Use getApplicationContext()
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
