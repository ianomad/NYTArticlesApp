/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.network;

import android.util.Log;

import com.google.common.base.Joiner;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

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

    private Call currentSearchCall;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.US);


    @Inject
    public NYTApiClient(@Named("NY_API_KEY") String apiKey) {
        this.apiKey = apiKey;
    }

    public void searchNews(final SearchRequest searchRequest, final NewsCallback newsCallback) {
        if (null != currentSearchCall && !currentSearchCall.isExecuted()) {
            currentSearchCall.cancel();
        }

        newsCallback.before(searchRequest);

        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme("https")
                .host("api.nytimes.com")
                .addPathSegments("svc/search/v2/articlesearch.json")
                .addQueryParameter("api-key", apiKey);

        if (null != searchRequest.getQ() && !searchRequest.getQ().isEmpty()) {
            builder.addQueryParameter("q", searchRequest.getQ());
        }

        if (null != searchRequest.getSort()) {
            builder.addQueryParameter("sort", searchRequest.getSort().toLowerCase());
        }

        if (null != searchRequest.getPage()) {
            builder.addQueryParameter("page", String.valueOf(searchRequest.getPage()));
        }

        if (null != searchRequest.getCategories() && searchRequest.getCategories().length != 0) {
            String newsDesks = Joiner.on(" ").skipNulls().join(searchRequest.getCategories());
            builder.addQueryParameter("fq", "news_desk:(" + newsDesks + ")");
        }

        if (null != searchRequest.getDate()) {
            builder.addQueryParameter("begin_date", sdf.format(searchRequest.getDate()));
        }

        HttpUrl url = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Log.d("DEBUG", url.toString());

        Call call = client.newCall(request);
        currentSearchCall = call;

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error", "Couldn't get response from server", e);
                newsCallback.onError(e, null, searchRequest);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                if (!response.isSuccessful()) {
                    Log.e("Error", "Couldn't get response from server: " + responseData);
                    newsCallback.onError(new IOException("Unexpected code " + response), null, searchRequest);
                    response.close();
                    return;
                }

                NewsResponse newsResponse = gson.fromJson(responseData, NewsResponse.class);
                response.close();

                Log.d("Debug", gson.toJson(newsResponse));

                newsCallback.onSuccess(newsResponse, searchRequest);
            }
        });
    }

}
