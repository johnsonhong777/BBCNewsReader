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
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "news.db";

    private static final String TABLE_NEWS = "news";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_LINK = "link";
    private static final String COLUMN_FAVORITE = "favorite";

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
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return favoriteNews;
    }

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

        if (cursor != null && cursor.moveToFirst()) {
            // Update existing record
            db.update(TABLE_NEWS, values, COLUMN_LINK + " = ?", new String[]{newsItem.getLink()});
            cursor.close();
        } else {
            // Insert new record
            db.insert(TABLE_NEWS, null, values);
        }
        return true;
    }

    public void saveFavoriteStatus(int newsId, boolean isFavorite) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAVORITE, isFavorite ? 1 : 0);
        db.update(TABLE_NEWS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(newsId)});
    }

    public void deleteFavoriteNews(int newsId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FAVORITE, 0);
        db.update(TABLE_NEWS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(newsId)});
    }

    public void cleanUpDuplicates() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Query to find duplicates based on the unique link
        String query = "SELECT " + COLUMN_ID + ", " + COLUMN_LINK + " FROM " + TABLE_NEWS
                + " GROUP BY " + COLUMN_LINK + " HAVING COUNT(" + COLUMN_LINK + ") > 1";

        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String link = cursor.getString(cursor.getColumnIndex(COLUMN_LINK));

                // Delete duplicates, keeping only the first entry
                String deleteQuery = "DELETE FROM " + TABLE_NEWS + " WHERE " + COLUMN_ID
                        + " NOT IN (SELECT MIN(" + COLUMN_ID + ") FROM " + TABLE_NEWS
                        + " WHERE " + COLUMN_LINK + " = ?)";
                db.execSQL(deleteQuery, new String[]{link});
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public NewsItem getNewsItemByLink(String link) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NEWS, null, COLUMN_LINK + " = ?", new String[]{link}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            NewsItem newsItem = new NewsItem(
                    cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_DATE)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_LINK)),
                    cursor.getInt(cursor.getColumnIndex(COLUMN_FAVORITE)) == 1
            );
            cursor.close();
            return newsItem;
        }
        return null;
    }
}
