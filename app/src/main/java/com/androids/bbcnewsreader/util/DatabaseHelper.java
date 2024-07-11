package com.androids.bbcnewsreader.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.androids.bbcnewsreader.model.NewsItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bbc_news.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_FAVORITES = "favorites";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LINK = "link";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createFavoritesTable = "CREATE TABLE " + TABLE_FAVORITES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_LINK + " TEXT" + ")";
        db.execSQL(createFavoritesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        onCreate(db);
    }

    public boolean insertFavorite(NewsItem newsItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, newsItem.getTitle());
        values.put(COLUMN_DESCRIPTION, newsItem.getDescription());
        values.put(COLUMN_DATE, newsItem.getDate());
        values.put(COLUMN_LINK, newsItem.getLink());

        long result = db.insert(TABLE_FAVORITES, null, values);
        return result != -1;
    }

    public List<NewsItem> getAllFavorites() {
        List<NewsItem> favoriteNews = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_FAVORITES, null, null, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
                    String link = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LINK));
                    NewsItem newsItem = new NewsItem(title, description, date, link);
                    favoriteNews.add(newsItem);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return favoriteNews;
    }

    public void deleteFavorite(String title) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FAVORITES, COLUMN_TITLE + "=?", new String[]{title});
    }
}
