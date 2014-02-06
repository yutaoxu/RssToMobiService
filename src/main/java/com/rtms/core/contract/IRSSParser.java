package com.rtms.core.contract;

import com.rtms.core.base.BaseFeed;

import java.net.URL;

/**
 * User: yanghua
 * Date: 1/29/14
 * Time: 8:24 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public interface IRSSParser {

    /**
     * parse feed with given urls
     *
     * @param urls the URL Array
     * @return the BaseFeed Array
     */
    BaseFeed[] parse(URL[] urls);

}
