package com.rtms.core.contract;

import com.rtms.core.base.BaseFeed;

import java.util.List;

/**
 * User: yanghua
 * Date: 1/11/14
 * Time: 12:33 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public interface IMobiGenerator {

    /**
     * generate mobi file with a list of feeds
     *
     * @param feeds the List of BaseFeed instances
     * @return return the generated mobi file path
     */
    String generate(List<BaseFeed> feeds);

}
