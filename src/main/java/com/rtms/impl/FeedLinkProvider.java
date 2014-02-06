package com.rtms.impl;

import com.rtms.core.contract.AbstractConfigManager;
import com.rtms.core.contract.IFeedLinkProvider;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * User: yanghua
 * Date: 1/29/14
 * Time: 8:43 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class FeedLinkProvider implements IFeedLinkProvider {


    private static final Logger logger = Logger.getLogger(FeedLinkProvider.class);
    private AbstractConfigManager configManager;

    public FeedLinkProvider(AbstractConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public String[] getFeedLinks() {
        String[] feedLinksArr = null;

        String feedLinkConfigFile = this.configManager.joinPathToConfigDir("feedlinks.txt");

        try {
            List links;
            links = FileUtils.readLines(new File(feedLinkConfigFile));
            feedLinksArr = new String[links.size()];

            for (int i = 0; i < links.size(); i++) {
                feedLinksArr[i] = links.get(i).toString();
                logger.debug("feed link at line " + i + " is :" + feedLinksArr[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return feedLinksArr;
    }
}
