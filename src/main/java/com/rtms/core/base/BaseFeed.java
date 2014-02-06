package com.rtms.core.base;

import java.util.Date;
import java.util.List;

/**
 * User: yanghua
 * Date: 1/11/14
 * Time: 12:39 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class BaseFeed {

    private String title;
    private String description;         //when the feed is a article this field will be set the full content
    private String author;
    private String language;
    private Date pubDate;
    private String link;
    private List<BaseEntry> entries;
    protected int entryThreshold;


    public BaseFeed() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public List<BaseEntry> getEntries() {
        return this.entries;
    }

    public void setEntries(List<BaseEntry> entries) {
        this.entries = entries;
    }

    @Override
    public String toString() {
        return "BaseFeed{" +
            "title='" + title + '\'' +
            ", author='" + author + '\'' +
            ", language='" + language + '\'' +
            ", pubDate=" + pubDate +
            '}';
    }
}
