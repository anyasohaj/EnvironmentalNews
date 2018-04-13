package com.bagirapp.environmentalnews;

import android.util.Log;
import android.widget.Toast;

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

public class Utils {

    public static final String LOG_TAG = Utils.class.getSimpleName();

    public static ArrayList<News> getDataFromAPI(String url) {

        ArrayList news = new ArrayList();
        try {
            JSONObject jsonObject = new JSONObject(makeHttpRequest(url));
            JSONObject response = jsonObject.getJSONObject("response");
            JSONArray resultsArray = response.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentNews = resultsArray.getJSONObject(i);
                String webTitle = currentNews.getString("webTitle");
                String itemUrl = currentNews.getString("webUrl");
                String section = currentNews.getString("sectionName");

                String publicationDate = "Publication date is not available";
                if (currentNews.has("webPublicationDate")) {
                    publicationDate = currentNews.getString("webPublicationDate");
                }

                String author = "Unknown author";
                JSONArray tagsArray = currentNews.getJSONArray("tags");
                JSONObject tags = tagsArray.getJSONObject(0);
                if (tags.has("firstName") && tags.has("lastName")) {
                    author = "by " + tags.getString("firstName");
                    author += " " + tags.getString("lastName");
                }
                News eq = new News(webTitle, publicationDate, itemUrl, section, author);
                news.add(eq);
             }
         } catch (JSONException e) {
            Log.e("Utils.class", "There's something wrong in getDataFromAPI method ");
        }
        return news;
    }

    private static URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Something wrong with creating URL");
        }
        return url;
    }

    public static String makeHttpRequest(String urlString) {

        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        URL url = createUrl(urlString);


        // If URL does not exist, it returns with an empty jsonResponse
        if (url == null) {
            return jsonResponse;
        }
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(1000);
            urlConnection.setConnectTimeout(1500);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            Log.v("Utils", "Kapcsolati kód: " + urlConnection.getResponseCode());

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
                Log.v("Utils", "Got the connecton!!! Yeah");
            } else {
                Log.e("Utils", "There's no internet connection.");
            }
        } catch (IOException exception) {
            Log.e(LOG_TAG, "Error in the makeHttpRequest() method");
        } finally {
            return jsonResponse;
        }
    }

    private static String readFromStream(InputStream inputStream) {
        StringBuilder output = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
        BufferedReader bufferedReader = new BufferedReader(reader);
        try {

            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            Log.e("Utils.class", "There's something wrong with readFromStream method");
        }
        return output.toString();

    }
}
