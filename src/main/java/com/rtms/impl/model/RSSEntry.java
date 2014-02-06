package com.rtms.impl.model;

import com.rtms.core.base.BaseEntry;
import com.sun.syndication.feed.synd.SyndEntry;

/**
 * User: yanghua
 * Date: 1/11/14
 * Time: 2:24 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class RSSEntry extends BaseEntry {

    private SyndEntry innerEntry;

    public RSSEntry(SyndEntry entry) {
        this.innerEntry = entry;

        this.setLink(this.innerEntry.getLink());
        this.setTitle(this.innerEntry.getTitle());
        this.setDescription(this.innerEntry.getDescription().getValue());
        this.setAuthor(this.innerEntry.getAuthor());
        this.setPubDate(this.innerEntry.getPublishedDate());
    }

    @Override
    public String toString() {
        return "RSSEntry{" +
            "} " + super.toString();
    }
}
