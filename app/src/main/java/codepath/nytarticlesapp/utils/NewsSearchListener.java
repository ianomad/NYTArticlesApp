/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.utils;

import android.app.Activity;
import android.support.v7.widget.SearchView;

import codepath.nytarticlesapp.interfaces.NewsCallback;
import codepath.nytarticlesapp.models.NewsResponse;
import codepath.nytarticlesapp.network.NYTApiClient;
import okhttp3.Call;

public class NewsSearchListener implements SearchView.OnQueryTextListener {

    private final Activity activity;
    private final NYTApiClient client;
    private final NewsCallback newsCallback;
    private Call currentSearchCall;

    public NewsSearchListener(Activity activity, NYTApiClient client, NewsCallback newsCallback) {
        this.activity = activity;
        this.client = client;
        this.newsCallback = newsCallback;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (null != currentSearchCall && !currentSearchCall.isExecuted()) {
            currentSearchCall.cancel();
        }

        currentSearchCall = client.searchNews(query, new NewsCallback() {
            @Override
            public void before() {
                newsCallback.before();
            }

            @Override
            public void onSuccess(final NewsResponse response) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsCallback.onSuccess(response);
                    }
                });
            }

            @Override
            public void onError(final Exception e, final String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsCallback.onError(e, message);
                    }
                });
            }
        });

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
