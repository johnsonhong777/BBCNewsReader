package com.androids.bbcnewsreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.androids.bbcnewsreader.R;
import com.androids.bbcnewsreader.model.NewsItem;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for displaying a list of news items with an optional delete button.
 */
public class NewsAdapter extends BaseAdapter {

    private Context context;
    private List<NewsItem> newsItems;
    private List<NewsItem> filteredNewsItems; // List for filtered news items
    private OnItemClickListener onItemClickListener;
    private OnDeleteClickListener onDeleteClickListener;
    private boolean showDeleteButton;
    private View mainLayout; // Reference to main layout for showing Snackbar

    /**
     * Interface for handling news item click events.
     */
    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    /**
     * Interface for handling delete button click events.
     */
    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    /**
     * Constructs a new {@code NewsAdapter}.
     *
     * @param context The context in which the adapter is running.
     * @param newsItems The list of news items to display.
     * @param onItemClickListener The listener for item click events.
     * @param showDeleteButton Whether to show the delete button.
     * @param mainLayout The main layout of the activity for showing Snackbar.
     */
    public NewsAdapter(Context context, List<NewsItem> newsItems, OnItemClickListener onItemClickListener, boolean showDeleteButton, View mainLayout) {
        this.context = context;
        this.newsItems = newsItems;
        this.filteredNewsItems = new ArrayList<>(newsItems); // Initialize with all items
        this.onItemClickListener = onItemClickListener;
        this.showDeleteButton = showDeleteButton;
        this.mainLayout = mainLayout;
    }

    /**
     * Sets the listener for delete button clicks.
     *
     * @param onDeleteClickListener The listener for delete button clicks.
     */
    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @Override
    public int getCount() {
        return filteredNewsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredNewsItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Creates or updates a view for displaying a news item at the specified position.
     *
     * @param position The position of the news item in the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent The parent view that this view will be attached to.
     * @return The view for the news item at the specified position.
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.news_item_title);
        Button deleteButton = convertView.findViewById(R.id.delete_button);

        final NewsItem newsItem = filteredNewsItems.get(position);
        titleTextView.setText(newsItem.getTitle());

        convertView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(newsItem);
            }
        });

        if (showDeleteButton) {
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(v -> {
                if (onDeleteClickListener != null) {
                    onDeleteClickListener.onDeleteClick(position);
                }
            });
        } else {
            deleteButton.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * Filters the list of news items based on the given query.
     *
     * @param query The query to filter the news items by.
     */
    public void filter(String query) {
        filteredNewsItems.clear();
        if (query.isEmpty()) {
            filteredNewsItems.addAll(newsItems);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (NewsItem item : newsItems) {
                if (item.getTitle().toLowerCase().contains(lowerCaseQuery)) {
                    filteredNewsItems.add(item);
                }
            }
            if (filteredNewsItems.isEmpty()) {
                // Show Snackbar if no items match the search query
                Snackbar.make(mainLayout, "No items found", Snackbar.LENGTH_LONG).show();
            }
        }
        notifyDataSetChanged();
    }
}
