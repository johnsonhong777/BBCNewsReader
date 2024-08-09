package com.androids.bbcnewsreader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.androids.bbcnewsreader.adapter.NewsAdapter;
import com.androids.bbcnewsreader.model.NewsItem;
import com.androids.bbcnewsreader.util.NetworkUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import android.os.AsyncTask;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Main activity that displays a list of news articles, handles navigation, and manages fetching and parsing news data.
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView newsListView;
    private List<NewsItem> newsList;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private NavigationView navigationView;
    private EditText searchEditText;
    private View mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();  // Load the saved locale
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the toolbar title based on the current locale
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getString(R.string.app_name));
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        mainLayout = findViewById(R.id.main_layout); // Initialize the main layout

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Log.d(TAG, "Navigation item selected: " + id);
            if (id == R.id.nav_favorites) {
                Log.d(TAG, "Navigating to FavoriteNewsActivity");
                startActivity(new Intent(MainActivity.this, FavoriteNewsActivity.class));
            } else if (id == R.id.nav_settings) {
                Log.d(TAG, "Navigating to SettingsActivity");
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        updateNavigationViewMenu();

        progressBar = findViewById(R.id.progress_bar);
        newsListView = findViewById(R.id.news_list_view);
        searchEditText = findViewById(R.id.search_edit_text);  // Initialize the EditText

        // Set up an action when the user enters text in the search EditText and presses Enter
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            String searchText = searchEditText.getText().toString();
            if (!searchText.isEmpty()) {
                Toast.makeText(MainActivity.this, "Searching for: " + searchText, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Showing all items", Toast.LENGTH_SHORT).show();
            }
            newsAdapter.filter(searchText);  // Filter the list based on the search query
            return true;
        });

        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, newsList, newsItem -> {
            Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
            intent.putExtra("newsItem", newsItem);
            startActivity(intent);
        }, false, mainLayout); // Pass the main layout to the adapter

        newsListView.setAdapter(newsAdapter);

        // Fetch news using AsyncTask
        new FetchNewsTask().execute();
    }

    private class FetchNewsTask extends AsyncTask<Void, Void, List<NewsItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE); // Show progress bar before starting the task
        }

        @Override
        protected List<NewsItem> doInBackground(Void... voids) {
            try {
                String response = NetworkUtils.getResponseFromHttpUrl();
                Log.d(TAG, "Network response: " + response);
                return response != null ? parseXml(response) : null;
            } catch (Exception e) {
                Log.e(TAG, "Error fetching news", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<NewsItem> newsItems) {
            super.onPostExecute(newsItems);
            progressBar.setVisibility(View.GONE); // Hide progress bar after task completion
            if (newsItems != null) {
                Log.d(TAG, "News items fetched: " + newsItems.size());
                newsList.clear();
                newsList.addAll(newsItems);
                newsAdapter.notifyDataSetChanged();
            } else {
                Log.e(TAG, "No news items fetched");
                Snackbar.make(mainLayout, "Failed to fetch news", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private List<NewsItem> parseXml(String xml) {
        List<NewsItem> newsItems = new ArrayList<>();
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xml));

            int eventType = parser.getEventType();
            NewsItem currentItem = null;
            String text = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equals("item")) {
                            currentItem = new NewsItem(0, "", "", "", "", false); // Initialize with default ID 0
                        }
                        break;
                    case XmlPullParser.TEXT:
                        text = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (currentItem != null) {
                            switch (tagName) {
                                case "title":
                                    currentItem.setTitle(text);
                                    break;
                                case "description":
                                    currentItem.setDescription(text);
                                    break;
                                case "pubDate":
                                    currentItem.setDate(text);
                                    break;
                                case "link":
                                    currentItem.setLink(text);
                                    break;
                                case "item":
                                    newsItems.add(currentItem);
                                    break;
                            }
                        }
                        break;
                }
                eventType = parser.next();
            }
            Log.d(TAG, "Parsed news items: " + newsItems.size());
        } catch (Exception e) {
            Log.e(TAG, "Error parsing XML", e);
        }
        return newsItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_favorites) {
            Intent intent = new Intent(this, FavoriteNewsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_help) {
            showHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.action_help))
                .setMessage(getString(R.string.help_message))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void loadLocale() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        String language = sharedPreferences.getString("My_Lang", "en");
        setLocale(language);
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Update the drawer menu to reflect the new language
        if (navigationView != null) {
            updateNavigationViewMenu();
        }
    }

    private void updateNavigationViewMenu() {
        navigationView.getMenu().clear();  // Clear the current menu
        navigationView.inflateMenu(R.menu.drawer_menu);  // Inflate the new menu to refresh titles
        Menu menu = navigationView.getMenu();
        menu.findItem(R.id.nav_favorites).setTitle(getString(R.string.nav_favorites));
        menu.findItem(R.id.nav_settings).setTitle(getString(R.string.nav_settings));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close any open resources
        Log.d(TAG, "MainActivity destroyed. Closing resources.");
    }
}
