package com.rtms.impl;

import com.rtms.component.IEntryTransporter;
import com.rtms.core.base.BaseEntry;
import com.rtms.core.base.BaseFeed;
import com.rtms.core.base.BaseImage;
import com.rtms.core.contract.*;
import com.rtms.impl.model.RSSFeed;
import com.rtms.impl.model.RSSImage;
import com.rtms.util.helper;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * User: yanghua
 * Date: 1/11/14
 * Time: 12:43 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class RSSParser implements IRSSParser {

    private static final Logger logger = Logger.getLogger(RSSParser.class);

    private RSSFeed[] feeds;
    private static String[] removeAttrs = {"class", "id", "title", "style", "width", "height", "onclick"};
    private static String[] removeTags = {"script", "object", "video", "embed", "iframe", "noscript", "style"};
    private static String[] imgExtends = {"jpg", "jpeg", "gif", "png", "bmp"};
    private AbstractConfigManager configManager;
    private IImgTransporter imgSender;
    private IEntryTransporter entryTransporter;
    private IFullTxtOutput ftoHandler;
    private boolean needFullTxt;


    public RSSParser(AbstractConfigManager configManager) {
        this.configManager = configManager;
        imgSender = new ImageTransporter(this.configManager);
        this.entryTransporter = new EntryTransporter(this.configManager);
        this.ftoHandler = new FullTxtOutput(this.configManager);
        this.needFullTxt = this.configManager.rtmsConfig().getProperty("rtms.enableFullTxt").toLowerCase().equals("true");
    }

    /**
     * parse a rss feed
     *
     * @return the instance of RSSFeed
     */
    @Override
    public BaseFeed[] parse(URL[] urls) {
        boolean hasEntryThresholdConfig = this.configManager.rtmsConfig().getProperty("rtms.entryThreshold") != null;
        int entryThreshold = 5;
        if (hasEntryThresholdConfig) {
            entryThreshold = Integer.parseInt(this.configManager.rtmsConfig().getProperty("rtms.entryThreshold"));
        }

        this.feeds = new RSSFeed[urls.length];
        SyndFeedInput input = new SyndFeedInput();

        try {
            for (int i = 0; i < urls.length; i++) {
                SyndFeed feed = input.build(new XmlReader(urls[i]));
                this.feeds[i] = hasEntryThresholdConfig ? new RSSFeed(feed, entryThreshold) : new RSSFeed(feed);
            }

            this.initForFeed();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (FeedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (RSSFeed feed : this.feeds) {
            this.parseEntries(feed);
        }

        return this.feeds;
    }

    /**
     * parse all the rss entries
     *
     * @param feed the instance of RSSFeed
     */
    private void parseEntries(RSSFeed feed) {
        for (int i = 0; i < feed.getEntries().size(); i++) {
            BaseEntry currentEntry = feed.getEntries().get(i);
            if (this.entryTransporter.entryExists(currentEntry)) {
                feed.getEntries().set(i, this.entryTransporter.getProcessedEntry(currentEntry));
                continue;
            }

            parseEntryOneByOne(currentEntry, feed);
        }
    }

    /**
     * parse every entry
     *
     * @param entry the instance of RSSEntry
     * @param feed  the instance of RSSFeed
     */
    private void parseEntryOneByOne(BaseEntry entry, RSSFeed feed) {
        if (entry == null) {
            throw new NullPointerException("the param:entry can not be empty");
        }

        logger.debug("needFullTxt: " + this.needFullTxt);
        if (this.needFullTxt) {
            // full text
            String fullTxtOfEntry = this.ftoHandler.fullTextFrom(entry.getLink());
            if (fullTxtOfEntry != null && fullTxtOfEntry.length() != 0) {
                entry.setDescription(fullTxtOfEntry);
            }
        }

        this.initForEntry(entry, feed);
        this.parseContent(entry, feed);
        this.parseImg(entry, feed);

        entryTransporter.save(entry);
    }

    /**
     * parse entry's content (reformat html content)
     *
     * @param entry one entry
     * @param feed  the instance of RSSFeed
     */
    private void parseContent(BaseEntry entry, RSSFeed feed) {
        String htmlStr = wrapDesc(entry.getDescription());
        Document htmlDoc = Jsoup.parse(htmlStr);

        htmlDoc.select("[display=none]").remove();
        for (int i = 0; i < removeAttrs.length; i++) {
            htmlDoc.select("[" + removeAttrs[i] + "]").removeAttr(removeAttrs[i]);
        }

        for (int i = 0; i < removeTags.length; i++) {
            htmlDoc.select(removeTags[i]).remove();
        }

        entry.setDescription(htmlDoc.html());
    }

    /**
     * parse entry's image url
     *
     * @param entry the entry
     * @param feed  the instance of RSSFeed
     */
    private void parseImg(BaseEntry entry, RSSFeed feed) {
        String htmlStr = wrapDesc(entry.getDescription());
        Document htmlDoc = Jsoup.parse(htmlStr);
        htmlDoc.select("img[src='']").remove();

        Elements imgs = htmlDoc.select("img");
        for (int i = 0; i < imgs.size(); i++) {
            processImg(imgs.get(i), entry, feed);
        }

        entry.setDescription(htmlDoc.html());
    }

    /**
     * process every image tag
     *
     * @param img   the processing Element's instance
     * @param entry the instance of RSSEntry
     * @param feed  the instance of RSSFeed
     */
    private void processImg(Element img, BaseEntry entry, RSSFeed feed) {
        if (img == null) {
            throw new NullPointerException("the arg:img can not be empty");
        }

        String src = img.attr("src");
        logger.debug("the original src attr is :" + src);
        if (StringUtils.isEmpty(src)) {
            return;
        }

        String urlStr = src.startsWith("http://") ? src : entry.getFeedLink() + src;
        logger.debug("processed url str is :" + urlStr);

        try {
            URLEncoder.encode(urlStr, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (!helper.endWithOneOfMulti(urlStr.toLowerCase(), imgExtends)) {
            logger.debug("matched extend url :" + urlStr);
            img.remove();
        } else {
            BaseImage imgObj = new RSSImage();
            imgObj.setDomain(feed.getLink());
            imgObj.setRef(entry.getLink());
            imgObj.setSrc(urlStr);
            String localPath = this.generateImageLocalPath(this.getEntryPath(feed, entry),
                helper.getFileNameFromPathOrUrl(src));
            imgObj.setLocalPath(localPath);
            img.attr("src", localPath);

            this.imgSender.push(imgObj);
        }

    }

    /**
     * wrap description as a html document
     *
     * @param content the content text
     * @return the html document
     */
    private String wrapDesc(String content) {
        if (StringUtils.isEmpty(content)) {
            throw new IllegalArgumentException("the arg: content can not be null or empty");
        }

        return String.format("<html><head></head><body>%s</body></html>", content);
    }

    /**
     * init for feed
     *
     * @throws IOException
     */
    private void initForFeed() throws IOException {
        for (RSSFeed feed : this.feeds) {
            String feedLinkMD5Code = helper.getMD5Code(feed.getLink().getBytes());
            String fullPath = this.configManager.joinPathToDataDir(feedLinkMD5Code);
            boolean dirExists = helper.isPathExists(fullPath);

            if (!dirExists) {
                boolean success = new File(fullPath).mkdir();
                if (!success) {
                    throw new RuntimeException("create dir failed at path:" + fullPath);
                }
            }
        }

    }

    /**
     * init for entry
     *
     * @param entry the instance of RSSEntry
     * @param feed  the instance of RSSFeed
     */
    private void initForEntry(BaseEntry entry, RSSFeed feed) {
        if (entry == null) {
            throw new NullPointerException("the arg: entry can not be null");
        }

        if (StringUtils.isEmpty(entry.getLink())) {
            throw new IllegalArgumentException("the arg:entry's property: link can not be null or empty");
        }

        String fullPath = this.getEntryPath(feed, entry);

        boolean dirExists = helper.isPathExists(fullPath);
        if (!dirExists) {
            boolean success = new File(fullPath).mkdir();

            if (!success) {
                throw new RuntimeException("create dir failed at path:" + fullPath);
            }
        }
    }

    /**
     * get feed's dir path
     *
     * @param feed the instance of RSSFeed
     * @return path
     */
    private String getFeedPath(RSSFeed feed) {
        String feedLinkMD5Code = helper.getMD5Code(feed.getLink().getBytes());
        return this.configManager.joinPathToDataDir(feedLinkMD5Code);
    }

    /**
     * get entry's dir path
     *
     * @param feed  the instance of RSSFeed
     * @param entry the instance of RSSEntry
     * @return the entry's path
     */
    private String getEntryPath(RSSFeed feed, BaseEntry entry) {
        String entryLinkMD5Code = helper.getMD5Code(entry.getLink().getBytes());
        String feedPath = this.getFeedPath(feed);
        return String.format("%s/%s", feedPath, entryLinkMD5Code);
    }

    /**
     * generate image local path with entryPath and image's name
     *
     * @param entryPath the entry's absoulate path
     * @param fileName  image's file name
     * @return generated full absolute path
     */
    private String generateImageLocalPath(String entryPath, String fileName) {
        return String.format("%s/%s", entryPath, fileName);
    }


}
