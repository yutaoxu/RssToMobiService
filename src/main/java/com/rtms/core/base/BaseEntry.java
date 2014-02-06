package com.rtms.core.base;

import java.util.Date;

/**
 * User: yanghua
 * Date: 1/11/14
 * Time: 2:23 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class BaseEntry {

    private String title;
    private String description;
    private String author;
    private Date pubDate;
    private String link;
    private String feedLink;                //reverse index

    public BaseEntry() {
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

    public String getFeedLink() {
        return feedLink;
    }

    public void setFeedLink(String feedLink) {
        this.feedLink = feedLink;
    }

    @Override
    public String toString() {
        return "BaseEntry{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", author='" + author + '\'' +
            ", pubDate=" + pubDate +
            ", link='" + link + '\'' +
            ", feedLink='" + feedLink + '\'' +
            '}';
    }
}
