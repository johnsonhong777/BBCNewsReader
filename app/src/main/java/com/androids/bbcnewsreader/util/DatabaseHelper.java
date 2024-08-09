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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for managing the database and performing CRUD operations on news items.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "news.db";

    private static final String TABLE_NEWS = "news";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_FAVORITE = "favorite";

    /**
     * Constructor for DatabaseHelper.
     *
     * @param context The context in which the database is operating.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_NEWS_TABLE = "CREATE TABLE " + TABLE_NEWS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_LINK + " TEXT,"
                + COLUMN_FAVORITE + " INTEGER DEFAULT 0)";
        Log.d("DatabaseHelper", "Creating table with query: " + CREATE_NEWS_TABLE);
        db.execSQL(CREATE_NEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEWS);
        onCreate(db);
    }

    /**
     * Retrieves all favorite news items from the database.
     *
     * @return A list of favorite news items.
     */
    public List<NewsItem> getAllFavoriteNews() {
        List<NewsItem> favoriteNews = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NEWS, null, COLUMN_FAVORITE + " = 1", null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(COLUMN_ID);
                    int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
                    int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
                    int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
                    int linkIndex = cursor.getColumnIndex(COLUMN_LINK);
                    int favoriteIndex = cursor.getColumnIndex(COLUMN_FAVORITE);

                    if (idIndex >= 0 && titleIndex >= 0 && descriptionIndex >= 0 && dateIndex >= 0 && linkIndex >= 0 && favoriteIndex >= 0) {
                        NewsItem newsItem = new NewsItem(
                                cursor.getInt(idIndex),
                                cursor.getString(titleIndex),
                                cursor.getString(descriptionIndex),
                                cursor.getString(dateIndex),
                                cursor.getString(linkIndex),
                                cursor.getInt(favoriteIndex) == 1
                        );
                        favoriteNews.add(newsItem);
                    } else {
                        Log.e("DatabaseHelper", "Column not found");
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return favoriteNews;
    }

    /**
     * Inserts or updates a news item in the database.
     *
     * @param newsItem The news item to be inserted or updated.
     * @return True if the operation was successful, otherwise false.
     */
    public boolean insertOrUpdateFavorite(NewsItem newsItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Check if the item is already in the database
        Cursor cursor = db.query(TABLE_NEWS, null, COLUMN_LINK + " = ?", new String[]{newsItem.getLink()}, null, null, null);
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, newsItem.getTitle());
        values.put(COLUMN_DESCRIPTION, newsItem.getDescription());
        values.put(COLUMN_DATE, newsItem.getDate());
        values.put(COLUMN_LINK, newsItem.getLink());
        values.put(COLUMN_FAVORITE, newsItem.isFavorite() ? 1 : 0);

        boolean isSuccess;
        if (cursor != null && cursor.moveToFirst()) {
            // Update existing record
            isSuccess = db.update(TABLE_NEWS, values, COLUMN_LINK + " = ?", new String[]{newsItem.getLink()}) > 0;
            cursor.close();
        } else {
            // Insert new record
            isSuccess = db.insert(TABLE_NEWS, null, values) != -1;
        }
        return isSuccess;
    }



    /**
     * Deletes a news item from the favorites.
     *
     * @param newsId The ID of the news item to be deleted from favorites.
     */
    public void deleteFavoriteNews(int newsId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAVORITE, 0);
        db.update(TABLE_NEWS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(newsId)});
    }

    /**
     * Retrieves a news item from the database based on its link.
     *
     * @param link The link of the news item.
     * @return The news item if found, otherwise null.
     */
    public NewsItem getNewsItemByLink(String link) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NEWS, null, COLUMN_LINK + " = ?", new String[]{link}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(COLUMN_ID);
            int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
            int descriptionIndex = cursor.getColumnIndex(COLUMN_DESCRIPTION);
            int dateIndex = cursor.getColumnIndex(COLUMN_DATE);
            int linkIndex = cursor.getColumnIndex(COLUMN_LINK);
            int favoriteIndex = cursor.getColumnIndex(COLUMN_FAVORITE);

            if (idIndex >= 0 && titleIndex >= 0 && descriptionIndex >= 0 && dateIndex >= 0 && linkIndex >= 0 && favoriteIndex >= 0) {
                NewsItem newsItem = new NewsItem(
                        cursor.getInt(idIndex),
                        cursor.getString(titleIndex),
                        cursor.getString(descriptionIndex),
                        cursor.getString(dateIndex),
                        cursor.getString(linkIndex),
                        cursor.getInt(favoriteIndex) == 1
                );
                cursor.close();
                return newsItem;
            } else {
                Log.e("DatabaseHelper", "Column not found");
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return null;
    }
}
