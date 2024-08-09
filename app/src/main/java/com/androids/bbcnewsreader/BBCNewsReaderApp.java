package com.androids.bbcnewsreader;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

/**
 * Custom {@link Application} class for managing application-wide settings, including locale settings.
 */
public class BBCNewsReaderApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        loadLocale();
    }

    /**
     * Loads the locale setting from shared preferences and applies it to the application.
     */
    public void loadLocale() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = sharedPreferences.getString("My_Lang", "en");
        setLocale(language);
    }

    /**
     * Sets the application's locale to the specified language.
     *
     * @param lang The language code (e.g., "en" for English, "fr" for French).
     */
    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(updateResources(base));
    }

    /**
     * Updates the base context with the locale settings from shared preferences.
     *
     * @param context The base context to update.
     * @return The updated context with the new locale settings.
     */
    private Context updateResources(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("My_Lang", "en");
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }
}
