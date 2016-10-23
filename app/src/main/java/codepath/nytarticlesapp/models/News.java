/*
 * Iliiazbek Akhmedov
 * Copyright (c) 2016.
 */

package codepath.nytarticlesapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class News {

    @SerializedName("web_url")
    private String webUrl;

    private String snippet;

    @SerializedName("lead_paragraph")
    private String leadParagraph;

    private Headline headline;

    private List<Multimedia> multimedia;

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getLeadParagraph() {
        return leadParagraph;
    }

    public void setLeadParagraph(String leadParagraph) {
        this.leadParagraph = leadParagraph;
    }

    public Headline getHeadline() {
        return headline;
    }

    public void setHeadline(Headline headline) {
        this.headline = headline;
    }

    public List<Multimedia> getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(List<Multimedia> multimedia) {
        this.multimedia = multimedia;
    }

    public Multimedia getBestImage() {

        Multimedia res = null;
        for (Multimedia candidate : multimedia) {
            if (null == res && candidate.getType().equals("image")) {
                res = candidate;
            } else if (res != null && candidate.getWidth() > res.getWidth()) {
                res = candidate;
            }
        }

        return res;
    }
}
