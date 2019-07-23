package com.example.mikita.ppo_lab.rss;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeedItem {
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    @SerializedName("title")
    @Expose
    protected String title = null;

    @SerializedName("link")
    @Expose
    protected String link = null;

    @SerializedName("description")
    @Expose
    protected String description = null;

    @SerializedName("pubDate")
    @Expose
    protected String pubDate = null;

    @SerializedName("thumbnailUrl")
    @Expose
    protected String thumbnailUrl = null;
}
