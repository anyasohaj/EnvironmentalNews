package com.bagirapp.environmentalnews;

import android.content.Context;
import android.content.res.Resources;
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
    private static final int SUCCESS_CODE = 200;
    Context UtilContext;

    public Utils(Context context){
        this.UtilContext = context;

    }

    public ArrayList<News> getDataFromAPI(String url) {

        ArrayList news = new ArrayList();
        try {
            JSONObject jsonObject = new JSONObject(makeHttpRequest(url));
            JSONObject response = jsonObject.getJSONObject(UtilContext.getString(R.string.JSON_response));
            JSONArray resultsArray = response.getJSONArray(UtilContext.getString(R.string.JSON_results));

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentNews = resultsArray.getJSONObject(i);
                String webTitle = currentNews.getString(UtilContext.getString(R.string.JSON_webTitle));
                String itemUrl = currentNews.getString(UtilContext.getString(R.string.JSON_webUrl));
                String section = currentNews.getString(UtilContext.getString(R.string.JSON_sectionName));

                String publicationDate = UtilContext.getString(R.string.no_date);
                if (currentNews.has(UtilContext.getString(R.string.JSON_pubDate))) {
                    publicationDate = currentNews.getString(UtilContext.getString(R.string.JSON_pubDate));
                }

                String author = UtilContext.getString(R.string.no_author);
                JSONObject fileds = currentNews.getJSONObject(UtilContext.getString(R.string.JSON_fields));
                if (fileds.has(UtilContext.getString(R.string.JSON_byline))) {
                    author = fileds.getString(UtilContext.getString(R.string.JSON_byline));
                }

                News eq = new News(webTitle, publicationDate, itemUrl, section, author);
                news.add(eq);
             }
         } catch (JSONException e) {
            Log.e(UtilContext.getString(R.string.Util), UtilContext.getString(R.string.error_getDateFromApi));
        }
        return news;
    }

    private  URL createUrl(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, UtilContext.getString(R.string.error_url));
        }
        return url;
    }

    public  String makeHttpRequest(String urlString) {

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
            urlConnection.setRequestMethod(UtilContext.getString(R.string.request_method));
            urlConnection.connect();
 //           Log.v(UtilContext.getString(R.string.Util), UtilContext.getString(R.string.connection_code) + urlConnection.getResponseCode());

            if (urlConnection.getResponseCode() == SUCCESS_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(UtilContext.getString(R.string.Util), UtilContext.getString(R.string.no_connection) + urlConnection.getResponseCode());
            }
        } catch (IOException exception) {
            Log.e(LOG_TAG, UtilContext.getString(R.string.error_makeHttpRequest));
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return jsonResponse;
        }
    }

    private  String readFromStream(InputStream inputStream) {
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
            Log.e(UtilContext.getString(R.string.Util), UtilContext.getString(R.string.error_readFromStream));
        }
        return output.toString();

    }
}
