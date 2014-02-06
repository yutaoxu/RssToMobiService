package com.rtms.impl.model;

import com.rtms.core.base.BaseEntry;
import com.rtms.core.base.BaseFeed;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * User: yanghua
 * Date: 1/11/14
 * Time: 12:39 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class RSSFeed extends BaseFeed {

    private static final Logger logger = Logger.getLogger(RSSFeed.class);

    protected SyndFeed innerFeed;

    public RSSFeed(SyndFeed innerFeed) {
        this.innerFeed = innerFeed;
    }

    public RSSFeed(SyndFeed innerFeed, int entryThreshold) {
        this(innerFeed);
        this.entryThreshold = entryThreshold;

        this.setTitle(this.innerFeed.getTitle());
        this.setDescription(this.innerFeed.getDescription());
        this.setAuthor(this.innerFeed.getAuthor());
        this.setLanguage(this.innerFeed.getLanguage());
        this.setPubDate(this.innerFeed.getPublishedDate());
        this.setLink(this.innerFeed.getLink());
        this.setEntries();
    }

    protected void setEntries() {
        if (this.innerFeed != null && this.innerFeed.getEntries() != null) {
            List entries = this.innerFeed.getEntries();
            int entryNum = this.entryThreshold == 0 ? entries.size() : this.entryThreshold;

            List<BaseEntry> theEntries = new ArrayList<BaseEntry>(entryNum);

            for (int i = 0; i < entryNum; i++) {
                RSSEntry entry = new RSSEntry((SyndEntry) entries.get(i));
                entry.setFeedLink(this.getLink());
                theEntries.add(entry);
            }

            super.setEntries(theEntries);
        }
    }

    @Override
    public String toString() {
        return "RSSFeed{" +
            "innerFeed=" + innerFeed +
            "} " + super.toString();
    }
}
