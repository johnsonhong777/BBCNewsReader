package com.androids.bbcnewsreader;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.androids.bbcnewsreader.model.NewsItem;
import com.androids.bbcnewsreader.util.DatabaseHelper;

/**
 * Activity to display detailed information about a news article, including options to save to favorites and view the article.
 */
public class NewsDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private NewsItem newsItem;
    private ImageButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        // Set up the toolbar and enable the back button
        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Initialize DatabaseHelper
        databaseHelper = new DatabaseHelper(this);

        // Get the news item from the Intent
        Intent intent = getIntent();
        newsItem = intent.getParcelableExtra("newsItem");

        if (newsItem != null) {
            // Check if the news item is already stored as favorite
            NewsItem storedItem = databaseHelper.getNewsItemByLink(newsItem.getLink());
            if (storedItem != null) {
                newsItem = storedItem; // Update the newsItem with the stored favorite status
            }
        }

        // Set up the views to display news details
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

        // Set up the save button and its click listener
        saveButton = findViewById(R.id.favorite_button);
        updateSaveButtonIcon();
        saveButton.setOnClickListener(v -> {
            newsItem.setFavorite(!newsItem.isFavorite());
            databaseHelper.insertOrUpdateFavorite(newsItem);

            // Show a Toast message based on the new favorite status
            if (newsItem.isFavorite()) {
                Toast.makeText(this, getString(R.string.save_to_favorites), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.remove_from_favorites), Toast.LENGTH_SHORT).show();
            }

            // Update the save button icon
            updateSaveButtonIcon();
        });

        // Set up the view article button and its click listener
        findViewById(R.id.view_article_button).setOnClickListener(v -> {
            if (newsItem != null && newsItem.getLink() != null && !newsItem.getLink().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(newsItem.getLink()));
                startActivity(browserIntent);
            } else {
                Toast.makeText(NewsDetailActivity.this, getString(R.string.no_link_available), Toast.LENGTH_SHORT).show();
            }
        });

        // Handle the back button press to return to the MainActivity
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

    /**
     * Updates the save button icon based on whether the news item is a favorite.
     */
    private void updateSaveButtonIcon() {
        if (newsItem.isFavorite()) {
            saveButton.setImageResource(R.drawable.ic_favorite);
        } else {
            saveButton.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}