package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static com.example.android.newsapp.NewsActivity.LOG_TAG;
/**
 * Helper methods related to requesting and receiving News data from Guardian.
 */
public final class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */

    private QueryUtils() {
    }

    /**
     * Query the Guardian dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl){

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> newsList = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}s
        return newsList;
    }


    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl){
        URL url = null;
        try{
            // Try to create an URL from String
            url = new URL(stringUrl);
        } catch (MalformedURLException e){
            // In case that request failed, print the error message into log
            Log.e(LOG_TAG,"Problem building the URL", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException{
        String jsonResponse = "";

    // if url is empty, return earlier
        if(url == null){
            return jsonResponse;
        }

        // Initialize variables for the HTTP connection and for the InputStream
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            // Send a request to connect
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if(urlConnection.getResponseCode() == 200){
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG,"Error response code " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            // If the connection was not established, print it to the log
            Log.e(LOG_TAG, "Problem retrieving the Guardian JSON results.", e);
        } finally {

            // Disconnect the HTTP connection if it has been not yet disconnected
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            if(inputStream != null){
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException{
        StringBuilder output = new StringBuilder();
        if(inputStream != null){
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            // Append the data of the BufferedReader line by line to the StringBuilder
            String line = bufferedReader.readLine();
            while(line != null){
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        // Convert the output into String and return it
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJson){
        // If the JSON string is empty or null, then return early.
        if(TextUtils.isEmpty(newsJson)){
            return null;
        }

        // Create an empty ArrayList that we can start adding News to
        List<News> newsList = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try{
            String newsAuthor = "N/A";
            JSONArray currentNewsAuthorArray;
            JSONObject newsTag;

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJson);

            JSONObject response = baseJsonResponse.getJSONObject("response");

            JSONArray results = response.getJSONArray("results");

            for (int i = 0; i <results.length() ; i++) {

                // Get a single News at position i within the list of News
                JSONObject currentNews = results.getJSONObject(i);

                // For a given News, extract the JSONObject associated with the key ,
                // which represents a list of all properties for that News.

                // Extract the News name (value) for the key "webTitle"
                String title = currentNews.getString("webTitle");

                // Extract the section name (value) for the key "sectionName"
                String section = currentNews.getString("sectionName");

                // Extract the date (value) for the key "webPublicationDate"
                String date = currentNews.getString("webPublicationDate");

                // Extract the url (value) for the key "webUrl"
                String url = currentNews.getString("webUrl");

                // Extract the author (value) for the key "webTitle"
                if (currentNews.has("tags")) {
                    currentNewsAuthorArray = currentNews.getJSONArray("tags");

                    if (currentNewsAuthorArray.length() > 0) {
                        for (int j = 0; j < 1; j++) {
                            newsTag = currentNewsAuthorArray.getJSONObject(j);
                            if (newsTag.has("webTitle")) {
                                newsAuthor = newsTag.getString("webTitle");
                            }
                        }
                    }
                }

                // Create a new {@link News} object with the title, date, section, author
                // and url from the JSON response.
                News newsObject = new News(title, section, date, newsAuthor, url);

                // Add the new {@link News} to the list of news.
                newsList.add(newsObject);
            }


        } catch (JSONException e) {
            Log.e("QueryUtils","Problem parsing the earthquake JSON results", e);
        }

        // Return the list of news
        return newsList;
    }


}
