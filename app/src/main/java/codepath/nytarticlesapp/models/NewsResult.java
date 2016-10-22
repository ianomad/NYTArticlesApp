/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsResult {

    Meta meta;
    @SerializedName("docs")
    List<News> news;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }
}
