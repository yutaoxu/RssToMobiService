package com.rtms.impl;

import com.rtms.core.base.BaseFeed;
import com.rtms.core.base.BaseImage;
import com.rtms.core.contract.AbstractConfigManager;
import com.rtms.core.contract.IImgTransporter;
import com.rtms.util.helper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.Map;

/**
 * User: yanghua
 * Date: 1/19/14
 * Time: 9:08 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class ImageTransporter implements IImgTransporter {

    private static final Logger logger = Logger.getLogger(ImageTransporter.class);

    private Jedis jedis;
    private AbstractConfigManager configManager;
    private boolean hasInited = false;

    public ImageTransporter(AbstractConfigManager configManager) {
        this.configManager = configManager;
        this.init();
    }

    /**
     * push image obj to redis task list
     *
     * @param img the instance of RSSImage
     */
    public void push(BaseImage img) {
        this.pushIfNotExists(img);
    }

    /**
     * push image object to redis if  not exists
     *
     * @param img the instance of BaseImage
     */
    private void pushIfNotExists(BaseImage img) {
        if (!hasInited) {
            throw new RuntimeException("there is something error occured in init()");
        }

        if (img == null) {
            throw new NullPointerException("the arg: img can not be null");
        }

        if (StringUtils.isEmpty(img.getDomain())) {
            throw new IllegalArgumentException("the arg:img's property domain can not be null or empty");
        }

        if (StringUtils.isEmpty(img.getLocalPath())) {
            throw new IllegalArgumentException("the arg:img's property localPath can not be null or empty");
        }

        String key = this.getImageCacheKey(img.getDomain());
        String subKey = helper.getMD5Code(img.getSrc().getBytes());

        if (!this.jedis.hexists(key, subKey)) {
            String valueStr = helper.gson.toJson(img);
            jedis.hset(key, subKey, valueStr);
        }
    }

    /**
     * get all image objects with feed
     *
     * @param feed the instance of RSSFeed
     * @return a map of RSSImage
     */
    public Map<String, BaseImage> getAllImageObjsWithFeed(BaseFeed feed) {
        if (!hasInited) {
            throw new RuntimeException("there is something error occured in init()");
        }

        if (feed == null) {
            throw new NullPointerException("the arg:feed can not be null");
        }

        if (StringUtils.isEmpty(feed.getLink())) {
            throw new IllegalArgumentException("the arg:feed's property:link can not be null or empty");
        }


        String key = this.getImageCacheKey(feed.getLink());
        Map<String, String> imgsOfMap = this.jedis.hgetAll(key);
        Map<String, BaseImage> imgObjMap = new HashMap<String, BaseImage>(imgsOfMap.size());

        for (Map.Entry<String, String> kvPair : imgsOfMap.entrySet()) {
            imgObjMap.put(kvPair.getKey(), helper.gson.fromJson(kvPair.getValue(), BaseImage.class));
        }

        return imgObjMap;
    }

    /**
     * clear image store from redis
     *
     * @param feed the instance of RSSFeed
     */
    public void clearImageStore(BaseFeed feed) {
        if (!hasInited) {
            throw new RuntimeException("there is something error occured in init()");
        }

        if (feed == null) {
            throw new NullPointerException("the arg:feed can not be null");
        }

        if (StringUtils.isEmpty(feed.getLink())) {
            throw new IllegalArgumentException("the arg:feed's property:link can not be null or empty");
        }

        String key = this.getImageCacheKey(feed.getLink());
        this.jedis.del(key);
    }

    /**
     * init the object private feild and status
     */
    private void init() {
        try {
            if (this.configManager instanceof ConfigManager) {
                ConfigManager config = (ConfigManager) this.configManager;
                this.jedis = new Jedis(config.redisConfig().getProperty("redis.host"),
                    Integer.valueOf(config.redisConfig().getProperty("redis.port")));
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
     * get image cache key
     *
     * @param original the original identifier
     * @return the generated key
     */
    private String getImageCacheKey(String original) {
        if (this.configManager instanceof ConfigManager) {
            ConfigManager realConfigObj = (ConfigManager) this.configManager;
            return String.format(realConfigObj.IMAGE_KEY_PATTERN,
                helper.getMD5Code(original.getBytes()));
        } else {
            return "";
        }
    }
}
