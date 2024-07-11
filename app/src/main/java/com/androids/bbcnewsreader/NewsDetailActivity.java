package com.androids.bbcnewsreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androids.bbcnewsreader.model.NewsItem;
import com.androids.bbcnewsreader.util.DatabaseHelper;

public class NewsDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private NewsItem newsItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        databaseHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        newsItem = (NewsItem) intent.getSerializableExtra("newsItem");

        TextView titleTextView = findViewById(R.id.news_detail_title);
        TextView descriptionTextView = findViewById(R.id.news_detail_description);
        TextView dateTextView = findViewById(R.id.news_detail_date);
        TextView linkTextView = findViewById(R.id.news_detail_link);

        if (newsItem != null) {
            titleTextView.setText(newsItem.getTitle());
            descriptionTextView.setText(newsItem.getDescription());
            dateTextView.setText(newsItem.getDate());
            linkTextView.setText(newsItem.getLink());
        }

        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> saveToFavorites());

        Button viewArticleButton = findViewById(R.id.view_article_button);
        viewArticleButton.setOnClickListener(v -> {
            if (newsItem != null && newsItem.getLink() != null && !newsItem.getLink().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getLink()));
                startActivity(browserIntent);
            } else {
                Toast.makeText(NewsDetailActivity.this, "No link available", Toast.LENGTH_SHORT).show();
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(NewsDetailActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void saveToFavorites() {
        boolean isInserted = databaseHelper.insertFavorite(newsItem);
        if (isInserted) {
            Toast.makeText(this, "Saved to favorites", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Already in favorites", Toast.LENGTH_SHORT).show();
        }
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
