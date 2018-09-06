/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.newsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.ola.newsapp1.R;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderCallbacks<List<News>> {

    /** Adapter for the list of news */
    private NewsAdapter mAdapter;


    private static final int NEWS_LOADER_ID = 1;
    public static final String LOG_TAG = NewsActivity.class.getName();
    private static final String GUARDIAN_REQUEST_URL =
           "http://content.guardianapis.com/search?order-by=newest&show-tags=contributor&page-size=20&q=politics&api-key=6f2b2fc3-131e-4731-8fbb-dd514a95c728";

    /** TextView that is displayed when the list is empty */
    private TextView mEmptyStateTextView;

    /** Message for the user */
    private String mMessageForTheUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.news_list_item);


        setContentView(R.layout.list_item);
        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);

        // No news have been found. Display this information on the screen
        mEmptyStateTextView = findViewById(R.id.empty_view);
        newsListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of news as input
        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Find the current News that was clicked on
                News currentNews = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                assert currentNews != null;
                Uri newsUri = Uri.parse(currentNews.getNewsUrl());

                // Create a new intent to view the news URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Check if there is any web browser available. If there is not, display toast message
                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(websiteIntent,
                        PackageManager.MATCH_DEFAULT_ONLY);

                boolean isIntentSafe = activities.size() > 0;

                if (isIntentSafe) {

                    // Start the intent
                    startActivity(websiteIntent);

                } else {
                    // Update an empty state with no internet connection error message
                    mMessageForTheUser = (String) getText(R.string.no_webbrowser);
                    mEmptyStateTextView.setText(mMessageForTheUser);
                }
            }

            });

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        assert connMgr != null;
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, fetch data
        if(networkInfo != null && networkInfo.isConnected()){
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).

            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
            // Update an empty state with no internet connection error message
            mMessageForTheUser = (String) getText(R.string.no_internet);
            mEmptyStateTextView.setText(mMessageForTheUser);
        }

    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );
      //  Uri baseUri = Uri.parse(GUARDIAN_REQUEST_URL);
      //  Uri.Builder uriBuilder = baseUri.buildUpon();

     //   uriBuilder.appendQueryParameter("format", "geojson");
  //      uriBuilder.appendQueryParameter("limit", "10");
 //       uriBuilder.appendQueryParameter("minmag", minMagnitude);
//        uriBuilder.appendQueryParameter("orderby", orderBy);

    //    return new NewsLoader(this, uriBuilder.toString());

        Log.e(LOG_TAG, "Loader created");
        // Create a new loader for the given URL
        return new NewsLoader(this, GUARDIAN_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        // Hide loading indicator because the data has been loaded
        View progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        // Clear the adapter of previous news data
        mAdapter.clear();

        // If there is a valid list of {@link News}s, then add them to the adapter's
        // data set. This will trigger the ListView to update
        if(news != null && !news.isEmpty()){
            Log.e(LOG_TAG, "Loader load finished");
            mAdapter.addAll(news);

            if (news.isEmpty()) {
                // Set empty state text to display "No news found."
                mMessageForTheUser = (String) getText(R.string.no_news);
                mEmptyStateTextView.setText(mMessageForTheUser);
            }
        }
    }

    // We need onLoaderReset(), we're being informed that the data from our loader is no longer valid
    // The correct thing to do is to remove all the news data from our UI by clearing out the adapter’s data set
    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.e(LOG_TAG, "Loader reset");
        // Loader reset, so we can clear out our existing data
        mAdapter.clear();
    }
    @Override
    // This method initialize the contents of the Activity's options menu.
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the Options Menu we specified in XML
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
