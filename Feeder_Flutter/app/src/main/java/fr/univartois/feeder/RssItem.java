package fr.univartois.feeder;

import androidx.annotation.NonNull;

public class RssItem {
    public String title;
    public String description;
    public String pubDate;
    public String link;

    public RssItem(String title, String description, String pubDate, String link) {
        this.title = title;
        this.description = description;
        this.pubDate = pubDate;
        this.link = link;
    }

    public RssItem(String title) {
        this.title = title;
    }

    public RssItem() {
    }

    @NonNull
    @Override
    public String toString() {
        return this.title;
    }
}