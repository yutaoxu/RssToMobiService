package com.rtms;

import com.rtms.component.IMailSender;
import com.rtms.core.base.BaseFeed;
import com.rtms.core.contract.AbstractConfigManager;
import com.rtms.core.contract.IFeedLinkProvider;
import com.rtms.core.contract.IMobiGenerator;
import com.rtms.core.contract.IRSSParser;
import com.rtms.impl.*;
import org.apache.commons.lang3.ArrayUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * User: yanghua
 * Date: 1/10/14
 * Time: 6:56 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class Service {

    private BaseFeed[] rssFeeds;

    private IRSSParser parser;
    private IMobiGenerator generator;
    private AbstractConfigManager configManager;
    private IFeedLinkProvider feedLinkProvider;

    private static final String RTMS_PROPERTIES_PATH = "/usr/local/rtms/workspace/config/rtms.properties";
    private static final String REDIS_PROPERTIES_PATH = "/usr/local/rtms/workspace/config/redis.properties";
    private static final String MAIL_PROPERTIES_PATH = "/usr/local/rtms/workspace/config/mail.properties";

    public Service() {
        this.initConfigManager();
        this.feedLinkProvider = new FeedLinkProvider(this.configManager);
        this.parser = new RSSParser(this.configManager);
    }

    /**
     * launch it!
     */
    public void launch() {
        URL[] parsingUrls = this.convertUrlStrsToURLObjs(feedLinkProvider.getFeedLinks());
        rssFeeds = this.parser.parse(parsingUrls);
        boolean downloaded = this.downloadImages();
        this.generateMobi(downloaded);
    }

    /**
     * download image
     *
     * @return if task has finished return true otherwise return false
     */
    private boolean downloadImages() {
        ExecutorService pool = Executors.newFixedThreadPool(this.rssFeeds.length);
        FutureTask<Boolean> futureTask = new FutureTask<Boolean>(new ImageDownloader(this.rssFeeds, this.configManager));
        pool.execute(futureTask);

        try {
            return futureTask.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * generate mobi file when image has beed download complete.
     *
     * @param imgDownloaded whether images has been download successfully
     */
    private void generateMobi(Boolean imgDownloaded) {
        if (!imgDownloaded) {
            throw new RuntimeException("it occurs an error when download images ");
        }

        this.generator = new MobiGenerator(this.configManager);

        String filePath = this.generator.generate(Arrays.asList(this.rssFeeds));
        if (this.configManager.rtmsConfig().getProperty("rtms.enableMailService").equalsIgnoreCase("true")){
            this.sendToKindle(filePath);
        }

        System.exit(0);
    }

    /**
     * send mail to kindle
     *
     * @param filePath the generated file path
     */
    private void sendToKindle(String filePath) {
        IMailSender mailSender = new MailSender(this.configManager);
        mailSender.sendFrom(filePath);
    }

    /**
     * convert url strs to url objects
     *
     * @param urlStrs the String array of url string
     * @return the converted URL Array
     */
    private URL[] convertUrlStrsToURLObjs(String[] urlStrs) {
        if (ArrayUtils.isEmpty(urlStrs)) {
            throw new IllegalArgumentException("the arg:urlStrs can not be null or empty");
        }

        URL[] urls;
        try {
            if (ArrayUtils.isEmpty(urlStrs)) {
                throw new IllegalArgumentException("the ctor's param: urlStrs can not be null or empty");
            }

            urls = new URL[urlStrs.length];
            for (int i = 0; i < urlStrs.length; i++) {
                urls[i] = new URL(urlStrs[i]);
            }

        } catch (Exception e) {
            urls = new URL[0];
        }

        return urls;
    }

    /**
     * init config manager
     */
    private void initConfigManager() {
        Properties rtmsConfig = null;
        Properties redisConfig = null;
        Properties mailConfig = null;

        rtmsConfig = new Properties();
        redisConfig = new Properties();
        mailConfig = new Properties();

        try {
            rtmsConfig.load(new FileInputStream(RTMS_PROPERTIES_PATH));
            redisConfig.load(new FileInputStream(REDIS_PROPERTIES_PATH));
            mailConfig.load(new FileInputStream(MAIL_PROPERTIES_PATH));
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.configManager = new ConfigManager(rtmsConfig, redisConfig, mailConfig);
    }

    public static void main(String[] args) {
        Service service = new Service();
        service.launch();

    }

}
