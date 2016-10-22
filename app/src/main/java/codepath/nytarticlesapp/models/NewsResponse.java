/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.models;

public class NewsResponse {

    private NewsResult response;

    public void setResponse(NewsResult response) {
        this.response = response;
    }

    public NewsResult getResponse() {
        return response;
    }

    @Override
    public String toString() {
        return "NewsResponse{" +
                "response=" + response +
                '}';
    }
}
