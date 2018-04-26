package com.bagirapp.environmentalnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<News>> {
    private static final String URL = "https://content.guardianapis.com/search";
    NewsAdapter adapter;
    private ImageView emptyView;
    private ProgressBar progressBar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        PreferenceManager.setDefaultValues(this, R.xml.settings_main, false);

        ListView listView = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        emptyView = (ImageView) findViewById(R.id.empty_view);
        emptyView.setVisibility(View.GONE);

        listView.setEmptyView(emptyView);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            progressBar.setVisibility(View.GONE);
            emptyView.setImageDrawable((getResources().getDrawable(R.drawable.no_connection)));
            Log.e(getString(R.string.MainActivity), getString(R.string.no_internet));
        } else {

            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(0, null, this);
        }

        adapter = new NewsAdapter(NewsActivity.this, new ArrayList());

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                News clickedNews = (News) adapter.getItem(i);
                intent.setData(Uri.parse(clickedNews.getUrl()));
                startActivity(intent);
            }
        });
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;  }
            return onOptionsItemSelected(item);
    }

    @Override
    public Loader<ArrayList<News>> onCreateLoader(int i, Bundle bundle) {


       SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        boolean environment = sharedPrefs.getBoolean(getString(R.string.section_key_environment), true);
        boolean technology = sharedPrefs.getBoolean(getString(R.string.section_key_technology), false);
        boolean economy = sharedPrefs.getBoolean(getString(R.string.section_key_economy), false);
        boolean science = sharedPrefs.getBoolean(getString(R.string.section_key_science), false);
        boolean books = sharedPrefs.getBoolean(getString(R.string.section_key_books),false);

        String link = "";
        if (environment) {link+=getString(R.string.section_key_environment) + ",";}
        if (technology){link+=getString(R.string.section_key_technology) + ",";}
        if (economy){link+=getString(R.string.section_key_economy)+ ",";}
        if (science){link+=getString(R.string.section_key_science) + ",";}
        if (books){link+=getString(R.string.section_key_books);}

        Uri baseUri = Uri.parse(URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(getString(R.string.api_key), getString(R.string.api_key_value));
        uriBuilder.appendQueryParameter(getString(R.string.show_fields_key), getString(R.string.byline));
        uriBuilder.appendQueryParameter(getString(R.string.query), link);

        return new NewsLoader(NewsActivity.this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<News>> loader, ArrayList<News> news) {
        adapter.clear();
        progressBar.setVisibility(View.GONE);
        if (news != null && !news.isEmpty()) {
            adapter.addAll(news);
        } else {
            emptyView.setImageDrawable(getResources().getDrawable(R.drawable.lao_ce));
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<News>> loader) {
        adapter.clear();
    }
}
