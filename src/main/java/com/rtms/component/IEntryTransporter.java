package com.rtms.component;

import com.rtms.core.base.BaseEntry;
import com.rtms.core.base.BaseFeed;

import java.util.Map;

/**
 * User: yanghua
 * Date: 1/30/14
 * Time: 4:33 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public interface IEntryTransporter {

    /**
     * save a entry
     *
     * @param entry the instance of BaseEntry
     */
    void save(BaseEntry entry);

    /**
     * check is entry exists
     *
     * @param entry the instance of BaseEntry
     * @return if exists return true otherwise return false
     */
    boolean entryExists(BaseEntry entry);

    /**
     * get processed entry (the func for cache entry)
     *
     * @param entry the instance of BaseEntry
     * @return return the processed entry
     */
    BaseEntry getProcessedEntry(BaseEntry entry);

    /**
     * get a feed's all entry (processed)
     *
     * @param feed the instance of BaseFeed
     * @return the map of the feed's entries (key is entry's link)
     */
    Map<String, BaseEntry> getAllEntryPerFeed(BaseFeed feed);

}
