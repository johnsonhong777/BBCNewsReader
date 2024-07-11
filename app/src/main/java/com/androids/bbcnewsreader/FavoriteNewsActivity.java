package com.androids.bbcnewsreader;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androids.bbcnewsreader.adapter.NewsAdapter;
import com.androids.bbcnewsreader.model.NewsItem;
import com.androids.bbcnewsreader.util.DatabaseHelper;

import java.util.List;

public class FavoriteNewsActivity extends AppCompatActivity {

    private ListView favoriteListView;
    private DatabaseHelper databaseHelper;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_news);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        favoriteListView = findViewById(R.id.favorite_list_view);
        databaseHelper = new DatabaseHelper(this);

        List<NewsItem> favoriteNews = databaseHelper.getAllFavorites();
        newsAdapter = new NewsAdapter(this, favoriteNews, newsItem -> {
            // Handle item click if needed
        });
        favoriteListView.setAdapter(newsAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close any open resources
        Log.d(TAG, "FavoriteNewsActivity destroyed. Closing resources.");
    }
}

