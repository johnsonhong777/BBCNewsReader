package com.androids.bbcnewsreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.androids.bbcnewsreader.R;
import com.androids.bbcnewsreader.model.NewsItem;

import java.util.List;

public class NewsAdapter extends BaseAdapter {

    private Context context;
    private List<NewsItem> newsList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(NewsItem newsItem);
    }

    public NewsAdapter(Context context, List<NewsItem> newsList, OnItemClickListener listener) {
        this.context = context;
        this.newsList = newsList;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return newsList.size();
    }

    @Override
    public Object getItem(int position) {
        return newsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        }

        TextView title = convertView.findViewById(R.id.news_item_title);
        NewsItem newsItem = newsList.get(position);
        title.setText(newsItem.getTitle());

        convertView.setOnClickListener(v -> listener.onItemClick(newsItem));

        return convertView;
    }
}
