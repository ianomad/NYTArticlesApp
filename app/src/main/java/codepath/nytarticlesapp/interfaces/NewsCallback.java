/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.interfaces;

import codepath.nytarticlesapp.models.NewsResponse;

public interface NewsCallback {
    public void before();

    public void onSuccess(NewsResponse response);

    public void onError(Exception e, String message);
}
