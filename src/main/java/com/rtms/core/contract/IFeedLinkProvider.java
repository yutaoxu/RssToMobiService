package com.rtms.core.contract;

/**
 * User: yanghua
 * Date: 1/29/14
 * Time: 8:41 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public interface IFeedLinkProvider {

    /**
     * get parsing rss links
     *
     * @return the String array of all links
     */
    String[] getFeedLinks();

}
