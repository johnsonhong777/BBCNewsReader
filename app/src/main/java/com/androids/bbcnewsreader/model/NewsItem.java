package com.androids.bbcnewsreader.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsItem implements Parcelable {
    private int id;
    private String title;
    private String description;
    private String date;
    private String link;
    private boolean isFavorite;

    public NewsItem() {}

    public NewsItem(int id, String title, String description, String date, String link, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.link = link;
        this.isFavorite = isFavorite;
    }

    protected NewsItem(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        date = in.readString();
        link = in.readString();
        isFavorite = in.readByte() != 0;
    }

    public static final Creator<NewsItem> CREATOR = new Creator<NewsItem>() {
        @Override
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in);
        }

        @Override
        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(date);
        dest.writeString(link);
        dest.writeByte((byte) (isFavorite ? 1 : 0));
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
