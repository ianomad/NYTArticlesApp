/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.utils;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.SearchView;

import org.parceler.Parcels;

import codepath.nytarticlesapp.network.SearchRequest;

public class NewsSearchListener implements SearchView.OnQueryTextListener {

    private final Activity activity;
    private final SearchView searchView;

    public NewsSearchListener(Activity activity, SearchView searchView) {
        this.activity = activity;
        this.searchView = searchView;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
        // see https://code.google.com/p/android/issues/detail?id=24599
        searchView.clearFocus();

        SearchRequest request = new SearchRequest();
        request.setQ(query);
        request.setPage(1);

        Intent searchRequestIntent = new Intent(Constants.SEARCH_REQUEST_CREATED);
        searchRequestIntent.putExtra("data", Parcels.wrap(request));

        LocalBroadcastManager.getInstance(activity).sendBroadcast(searchRequestIntent);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
