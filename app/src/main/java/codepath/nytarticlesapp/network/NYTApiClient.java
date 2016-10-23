/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import codepath.nytarticlesapp.interfaces.NewsCallback;
import codepath.nytarticlesapp.models.NewsResponse;
import okhttp3.Call;
import okhttp3.Callback;
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

    public Call searchNews(String searchKey, final NewsCallback newsCallback) {
        newsCallback.before();

        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme("https")
                .host("api.nytimes.com")
                .addPathSegments("svc/search/v2/articlesearch.json")
                .addQueryParameter("api-key", apiKey);

        if (null != searchKey) {
            builder.addQueryParameter("q", searchKey);
        }

        HttpUrl url = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", "Couldn't get response from server", e);
                newsCallback.onError(e, null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("Error", "Couldn't get response from server");
                    newsCallback.onError(new IOException("Unexpected code " + response), null);
                    response.close();
                    return;
                }

                NewsResponse newsResponse = gson.fromJson(response.body().string(), NewsResponse.class);
                response.close();
                newsCallback.onSuccess(newsResponse);
            }
        });

        return call;
    }

}
