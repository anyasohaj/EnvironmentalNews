package com.bagirapp.environmentalnews;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsAdapter extends ArrayAdapter {
    public NewsAdapter(@NonNull Context context, @NonNull List objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        News currentNews = (News) getItem(position);

        TextView title = (TextView) convertView.findViewById(R.id.newsTitle);
        title.setText(currentNews.getTitle());

        TextView date = (TextView) convertView.findViewById(R.id.date);
        date.setText(currentNews.getPublicationDate());

        TextView section = (TextView) convertView.findViewById(R.id.section);
        section.setText(currentNews.getSectionName());

        TextView author = (TextView) convertView.findViewById(R.id.author);
        author.setText(currentNews.getAuthor());

        return convertView;
    }
}
