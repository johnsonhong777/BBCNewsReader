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

import java.util.List;

public class NewsAdapter extends BaseAdapter {
    private Context context;
    private List<NewsItem> newsItems;
    private OnItemClickListener onItemClickListener;
    private OnDeleteClickListener onDeleteClickListener;
    private boolean showDeleteButton;

    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public NewsAdapter(Context context, List<NewsItem> newsItems, OnItemClickListener onItemClickListener, boolean showDeleteButton) {
        this.context = context;
        this.newsItems = newsItems;
        this.onItemClickListener = onItemClickListener;
        this.showDeleteButton = showDeleteButton;
    }

    public void setOnDeleteClickListener(OnDeleteClickListener onDeleteClickListener) {
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @Override
    public int getCount() {
        return newsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return newsItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        }

        TextView titleTextView = convertView.findViewById(R.id.news_item_title);
        Button deleteButton = convertView.findViewById(R.id.delete_button);

        final NewsItem newsItem = newsItems.get(position);
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
}
