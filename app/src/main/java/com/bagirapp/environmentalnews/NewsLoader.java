package com.bagirapp.environmentalnews;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.ArrayList;

public class NewsLoader extends AsyncTaskLoader {
    private String url;

    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        onForceLoad();
    }

    @Override
    public ArrayList<News> loadInBackground() {

        if (url == null) {
            return null;
        }
        Utils u = new Utils(getContext());
        return u.getDataFromAPI(url);
    }

}
