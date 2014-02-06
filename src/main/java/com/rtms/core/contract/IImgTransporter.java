package com.rtms.core.contract;

import com.rtms.core.base.BaseFeed;
import com.rtms.core.base.BaseImage;

import java.util.Map;

/**
 * User: yanghua
 * Date: 1/29/14
 * Time: 8:20 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public interface IImgTransporter {

    /**
     * push a img obj to the image store
     *
     * @param img the instance of BaseImage
     */
    public void push(BaseImage img);

    /**
     * get all image objs with the feed
     *
     * @param feed the instance of BaseFeed
     * @return the map of all the images per feed
     */
    public Map<String, BaseImage> getAllImageObjsWithFeed(BaseFeed feed);

    /**
     * clear all image from store by feed
     *
     * @param feed the instance of BaseFeed
     */
    public void clearImageStore(BaseFeed feed);

}
