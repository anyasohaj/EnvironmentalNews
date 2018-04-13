package com.bagirapp.environmentalnews;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<News>> {
    private static final String URL = "http://content.guardianapis.com/environment?show-tags=contributor&order-by=newest&page-size=12&api-key=3fe2484b-9a0f-4029-a280-c1e7911e5d77";
    NewsAdapter adapter;
    private ImageView emptyView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

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
            Log.e("Newsactivity", "There's no internet connection");
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
    public Loader<ArrayList<News>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(NewsActivity.this, URL);
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
