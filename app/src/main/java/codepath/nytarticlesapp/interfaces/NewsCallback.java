/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.interfaces;

import codepath.nytarticlesapp.models.NewsResponse;
import codepath.nytarticlesapp.network.SearchRequest;

public interface NewsCallback {
    public void before(SearchRequest searchRequest);

    public void onSuccess(NewsResponse response, SearchRequest searchRequest);

    public void onError(Exception e, String message, SearchRequest searchRequest);
}
