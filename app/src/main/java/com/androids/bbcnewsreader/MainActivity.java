package com.androids.bbcnewsreader;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.androids.bbcnewsreader.adapter.NewsAdapter;
import com.androids.bbcnewsreader.model.NewsItem;
import com.androids.bbcnewsreader.util.NetworkUtils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ListView newsListView;
    private List<NewsItem> newsList;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
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
            drawerLayout.closeDrawers();
            return true;
        });


        progressBar = findViewById(R.id.progress_bar);
        newsListView = findViewById(R.id.news_list_view);
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, newsList, newsItem -> {
            Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
            intent.putExtra("newsItem", newsItem);
            startActivity(intent);
        });
        newsListView.setAdapter(newsAdapter);

        executorService = Executors.newSingleThreadExecutor();
        fetchNews();
    }

    private void fetchNews() {
        progressBar.setVisibility(android.view.View.VISIBLE);
        Future<List<NewsItem>> future = executorService.submit(() -> {
            try {
                String response = NetworkUtils.getResponseFromHttpUrl();
                Log.d(TAG, "Network response: " + response);
                return response != null ? parseXml(response) : null;
            } catch (Exception e) {
                Log.e(TAG, "Error fetching news", e);
                return null;
            }
        });

        executorService.execute(() -> {
            try {
                List<NewsItem> newsItems = future.get();
                runOnUiThread(() -> {
                    progressBar.setVisibility(android.view.View.GONE);
                    if (newsItems != null) {
                        Log.d(TAG, "News items fetched: " + newsItems.size());
                        newsList.clear();
                        newsList.addAll(newsItems);
                        newsAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(TAG, "No news items fetched");
                        Snackbar.make(findViewById(R.id.main_layout), "Failed to fetch news", Snackbar.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error processing news items", e);
            }
        });
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
                            currentItem = new NewsItem("", "", "", "");
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
        if (item.getItemId() == R.id.action_help) {
            showHelpDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHelpDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Help")
                .setMessage(getString(R.string.help_message))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close any open resources
        Log.d(TAG, "MainActivity destroyed. Closing resources.");
    }
}
