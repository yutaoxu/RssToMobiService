package com.rtms.impl;

import com.rtms.core.base.BaseEntry;
import com.rtms.core.base.BaseFeed;
import com.rtms.core.contract.AbstractConfigManager;
import com.rtms.core.contract.IEntryTransporter;
import com.rtms.util.helper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * User: yanghua
 * Date: 1/30/14
 * Time: 4:38 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class EntryTransporter implements IEntryTransporter {

    private static final Logger logger = Logger.getLogger(EntryTransporter.class);

    private AbstractConfigManager configManager;
    private Jedis jedis;
    private boolean hasInited = false;

    public EntryTransporter(AbstractConfigManager configManager) {
        this.configManager = configManager;
        init();
    }

    @Override
    public void save(BaseEntry entry) {
        if (!this.hasInited) {
            throw new RuntimeException("the EntryTransporter's status has error");
        }

        if (entry == null) {
            throw new NullPointerException("the arg: entry can not be null");
        }

        if (StringUtils.isEmpty(entry.getLink())) {
            throw new IllegalArgumentException("the arg: entry's property: link can not be null or empty");
        }

        String key = this.getFeedCacheKey(entry.getFeedLink());

        String subKey = helper.getMD5Code(entry.getLink().getBytes());
        String entryJSonStr = helper.gson.toJson(entry, BaseEntry.class);
        this.jedis.hset(key, subKey, entryJSonStr);
    }

    @Override
    public boolean entryExists(BaseEntry entry) {
        if (!this.hasInited) {
            throw new RuntimeException("the EntryTransporter's status has error");
        }

        String feedKey = this.getFeedCacheKey(entry.getFeedLink());
        String entryKey = helper.getMD5Code(entry.getLink().getBytes());

        return this.jedis.hexists(feedKey, entryKey);
    }

    @Override
    public BaseEntry getProcessedEntry(BaseEntry entry) {
        if (!this.hasInited) {
            throw new RuntimeException("the EntryTransporter's status has error");
        }

        String feedKey = this.getFeedCacheKey(entry.getFeedLink());
        String entryKey = helper.getMD5Code(entry.getLink().getBytes());

        String entryItemStr = this.jedis.hget(feedKey, entryKey);

        return helper.gson.fromJson(entryItemStr, BaseEntry.class);
    }

    @Override
    public Map<String, BaseEntry> getAllEntryPerFeed(BaseFeed feed) {
        if (!this.hasInited) {
            throw new RuntimeException("the EntryTransporter's status has error");
        }

        String feedKey = this.getFeedCacheKey(feed.getLink());

        Map<String, String> entryItemMap = this.jedis.hgetAll(feedKey);
        Map<String, BaseEntry> entryObjMap = new HashMap<String, BaseEntry>(entryItemMap.size());

        for (Map.Entry<String, String> entryKVPair : entryItemMap.entrySet()) {
            BaseEntry entryObj = helper.gson.fromJson(entryKVPair.getValue(), BaseEntry.class);
            entryObjMap.put(entryKVPair.getKey(), entryObj);
        }

        return entryObjMap;
    }

    /**
     * init the object
     */
    private void init() {
        try {
            if (this.configManager instanceof ConfigManager) {
                ConfigManager realConfig = (ConfigManager) this.configManager;
                this.jedis = new Jedis(realConfig.redisConfig().getProperty("redis.host"),
                    Integer.valueOf(realConfig.redisConfig().getProperty("redis.port")));
                this.hasInited = true;
            } else {
                this.hasInited = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.hasInited = false;
        }
    }

    /**
     * get entry cache key
     *
     * @param original the entry's original identifier
     * @return the generated entry cache key
     */
    private String getFeedCacheKey(String original) {
        ConfigManager realConfigObj = (ConfigManager) this.configManager;
        return String.format(realConfigObj.ENTRY_KEY_PATTERN, helper.getMD5Code(original.getBytes()));
    }
}
