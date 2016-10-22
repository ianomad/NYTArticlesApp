/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.network;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import codepath.nytarticlesapp.interfaces.NewsCallback;
import codepath.nytarticlesapp.models.NewsResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

@Singleton
public class NYTApiClient {

    @Inject
    OkHttpClient client;

    @Inject
    Gson gson;

    private final String apiKey;

    @Inject
    public NYTApiClient(@Named("NY_API_KEY") String apiKey) {
        this.apiKey = apiKey;
    }

    public void searchNews(String searchKey, final NewsCallback newsCallback) {

        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.nytimes.com")
                .addPathSegments("svc/search/v2/articlesearch.json")
                .addQueryParameter("q", searchKey)
                .addQueryParameter("api-key", apiKey)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                newsCallback.onError(e, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    newsCallback.onError(new IOException("Unexpected code " + response), null);
                }

                NewsResponse newsResponse = gson.fromJson(response.body().string(), NewsResponse.class);
                newsCallback.onSuccess(newsResponse);
            }
        });
    }

}
