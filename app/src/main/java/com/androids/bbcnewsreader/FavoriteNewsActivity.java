package com.androids.bbcnewsreader;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.androids.bbcnewsreader.adapter.NewsAdapter;
import com.androids.bbcnewsreader.model.NewsItem;
import com.androids.bbcnewsreader.util.DatabaseHelper;

import java.util.List;

public class FavoriteNewsActivity extends AppCompatActivity {
    private ListView favoriteListView;
    private NewsAdapter adapter;
    private List<NewsItem> favoriteArticles;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_news);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseHelper = new DatabaseHelper(this);
        favoriteArticles = databaseHelper.getAllFavoriteNews();

        favoriteListView = findViewById(R.id.favorite_list_view);
        adapter = new NewsAdapter(this, favoriteArticles, newsItem -> {
            Intent intent = new Intent(FavoriteNewsActivity.this, NewsDetailActivity.class);
            intent.putExtra("newsItem", newsItem);
            startActivity(intent);
        }, true);
        favoriteListView.setAdapter(adapter);

        favoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewsItem newsItem = favoriteArticles.get(position);
                Intent intent = new Intent(FavoriteNewsActivity.this, NewsDetailActivity.class);
                intent.putExtra("newsItem", newsItem);
                startActivity(intent);
            }
        });

        adapter.setOnDeleteClickListener(position -> {
            NewsItem newsItem = favoriteArticles.get(position);
            databaseHelper.deleteFavoriteNews(newsItem.getId());
            favoriteArticles.remove(position);
            adapter.notifyDataSetChanged();
            Toast.makeText(FavoriteNewsActivity.this, "Article deleted", Toast.LENGTH_SHORT).show();
        });
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


