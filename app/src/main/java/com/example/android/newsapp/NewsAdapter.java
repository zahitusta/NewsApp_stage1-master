package com.example.android.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.ola.newsapp1.R;

import java.util.ArrayList;



/**
 * {@link NewsAdapter} is an {@link ArrayAdapter} that can provide the layout for each list item
 * based on a data source, which is a list of {@link News} objects.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * Create a new {@link NewsAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param News is the list of {@link News}s to be displayed.
     */
    private static final String LOG_TAG = NewsAdapter.class.getName();


    public NewsAdapter(Context context, ArrayList<News> news){
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for some TextViews, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context,0,news);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(
               //     R.layout.news_activity,parent,false);
                    R.layout.activity_main,parent,false);
        }

        // Get the {@link NewsClass} object located at this position in the list
        News currentNewsClass = getItem(position);

        // Find the TextView with news title in the news_activity.xml layout with the ID
        TextView newsTitleTextView = listItemView.findViewById(R.id.news_title);
        // Get the title from the current object and set this text on that TextView
        assert currentNewsClass != null;
        newsTitleTextView.setText(currentNewsClass.getNewsTitle());

        // Find the TextView with news section in the news_activity.xml layout with the ID
        TextView newsSectionTextView = listItemView.findViewById(R.id.section);
        // Get the section from the current object and set this text on that TextView
        newsSectionTextView.setText(currentNewsClass.getNewsSection());

        // Find the TextView with author of the news in the news_activity.xml layout with the ID
        TextView authorNewsTextView = listItemView.findViewById(R.id.author);
        // Get the author of the news from the current object and set this text on that TextView
        authorNewsTextView.setText(currentNewsClass.getAuthorsName());

        // Find the TextView with date of News in the news_activity.xml layout with the ID
        TextView dateNewsTextView = listItemView.findViewById(R.id.date);
        // Get the date of the News from the current object and set this text on that TextView
        dateNewsTextView.setText(currentNewsClass.getNewsDate());

        // Return the whole list item layout
        // so that it can be shown in the ListView
        return listItemView;
    }
}
