package com.androids.bbcnewsreader.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Represents a news item with details like title, description, date, link, and favorite status.
 * Implements {@link Parcelable} to allow passing instances between Android components.
 */
public class NewsItem implements Parcelable {
    private int id;
    private String title;
    private String description;
    private String date;
    private String link;
    private boolean isFavorite;

    /**
     * Default constructor.
     */
    public NewsItem() {}

    /**
     * Constructs a new {@code NewsItem} with the specified details.
     *
     * @param id The unique identifier of the news item.
     * @param title The title of the news item.
     * @param description The description of the news item.
     * @param date The publication date of the news item.
     * @param link The URL link to the news item.
     * @param isFavorite Whether the news item is marked as a favorite.
     */
    public NewsItem(int id, String title, String description, String date, String link, boolean isFavorite) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.link = link;
        this.isFavorite = isFavorite;
    }

    /**
     * Constructs a {@code NewsItem} from a {@link Parcel}.
     *
     * @param in The {@link Parcel} containing the news item data.
     */
    protected NewsItem(Parcel in) {
        id = in.readInt();
        title = in.readString();
        description = in.readString();
        date = in.readString();
        link = in.readString();
        isFavorite = in.readByte() != 0;
    }

    /**
     * {@link Creator} for {@code NewsItem} instances.
     */
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

    /**
     * Gets the unique identifier of the news item.
     *
     * @return The unique identifier of the news item.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the news item.
     *
     * @param id The unique identifier of the news item.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the title of the news item.
     *
     * @return The title of the news item.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the news item.
     *
     * @param title The title of the news item.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the description of the news item.
     *
     * @return The description of the news item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the news item.
     *
     * @param description The description of the news item.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the publication date of the news item.
     *
     * @return The publication date of the news item.
     */
    public String getDate() {
        return date;
    }

    /**
     * Sets the publication date of the news item.
     *
     * @param date The publication date of the news item.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the URL link to the news item.
     *
     * @return The URL link to the news item.
     */
    public String getLink() {
        return link;
    }

    /**
     * Sets the URL link to the news item.
     *
     * @param link The URL link to the news item.
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * Checks if the news item is marked as a favorite.
     *
     * @return {@code true} if the news item is a favorite, {@code false} otherwise.
     */
    public boolean isFavorite() {
        return isFavorite;
    }

    /**
     * Sets the favorite status of the news item.
     *
     * @param favorite {@code true} to mark the news item as a favorite, {@code false} otherwise.
     */
    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
