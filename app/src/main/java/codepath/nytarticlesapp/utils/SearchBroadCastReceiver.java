/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;

import org.parceler.Parcels;

import codepath.nytarticlesapp.interfaces.NewsCallback;
import codepath.nytarticlesapp.models.NewsResponse;
import codepath.nytarticlesapp.network.NYTApiClient;
import codepath.nytarticlesapp.network.SearchRequest;

public class SearchBroadCastReceiver extends BroadcastReceiver {

    private final Activity activity;
    private final NewsCallback newsCallback;
    private final NYTApiClient nyApiClient;

    public SearchBroadCastReceiver(Activity activity, NYTApiClient nyApiClient, NewsCallback newsCallback) {
        this.activity = activity;
        this.nyApiClient = nyApiClient;
        this.newsCallback = newsCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        SearchRequest searchRequest = Parcels.unwrap(intent.getParcelableExtra("data"));

        nyApiClient.searchNews(searchRequest, new NewsCallback() {
            @Override
            public void before(final SearchRequest request) {
                activity.runOnUiThread(() -> newsCallback.before(request));
            }

            @Override
            public void onSuccess(final NewsResponse response, final SearchRequest searchRequest) {
                activity.runOnUiThread(() -> newsCallback.onSuccess(response, searchRequest));
            }

            @Override
            public void onError(final Exception e, final String message, final SearchRequest searchRequest) {
                activity.runOnUiThread(() -> newsCallback.onError(e, message, searchRequest));
            }
        });
    }
}
