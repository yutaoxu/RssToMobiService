package com.rtms.impl;

import com.rtms.core.base.BaseFeed;
import com.rtms.core.base.BaseImage;
import com.rtms.core.contract.AbstractConfigManager;
import com.rtms.util.helper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * User: yanghua
 * Date: 1/15/14
 * Time: 5:09 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class ImageDownloader implements Callable<Boolean> {

    private static final Logger logger = Logger.getLogger(ImageDownloader.class);

    private Jedis jedis;
    private AbstractConfigManager configManager;
    private BaseFeed[] feeds;
    private boolean hasInited = false;
    private ImageTransporter imageTransporter;

    public ImageDownloader(BaseFeed[] feeds, AbstractConfigManager configManager) {
        this.feeds = feeds;
        this.configManager = configManager;
        this.imageTransporter = new ImageTransporter(this.configManager);
        this.init();
    }

    /**
     * init some object status
     */
    private void init() {
        try {
            if (this.configManager instanceof ConfigManager) {
                ConfigManager config = (ConfigManager) this.configManager;
                logger.debug("redis's port is :" + config.redisConfig().getProperty("redis.port"));
                this.jedis = new Jedis(config.redisConfig().getProperty("redis.host"),
                    Integer.parseInt(config.redisConfig().getProperty("redis.port")));
                this.hasInited = true;
            } else {
                this.hasInited = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.hasInited = false;
        }
    }

    @Override
    public Boolean call() throws Exception {
        downloadImg();
        logger.info("downloadImg method has finished");
        return true;
    }

    /**
     * the main download method
     */
    private void downloadImg() {
        for (BaseFeed feed : feeds) {
            downloadImgWithFeed(feed);
        }
    }

    /**
     * download images with feed
     *
     * @param feed the instance of RSSFeed
     */
    private void downloadImgWithFeed(BaseFeed feed) {
        Map<String, BaseImage> images = imageTransporter.getAllImageObjsWithFeed(feed);

        logger.debug("------------------------downloading feed image start-----------------------");
        logger.debug("feed link:" + feed.getLink());
        logger.debug("total images's count is:" + images.size());
        int counter = 0;

        for (Map.Entry<String, BaseImage> imageKVPair : images.entrySet()) {
            String currentImgPath = imageKVPair.getValue().getLocalPath();
            logger.debug("downloadImgWithFeed local path is :" + imageKVPair.getValue().toString());
            boolean imgFileExists = helper.isPathExists(currentImgPath);

            if (imgFileExists) {
                continue;
            }

            byte[] imgData = downloadImageWithUrl(imageKVPair.getValue().getSrc());
            if (imgData != null) {
                writeImgDataToFile(imgData, imageKVPair.getValue());
            }

            counter++;
            logger.debug(counter + " downloaded");
        }
        logger.debug("------------------------downloading feed image end-----------------------");
    }

    /**
     * download image with given url and return image's byte[]
     *
     * @param urlStr the image's url
     * @return the array of image's byte
     */
    private byte[] downloadImageWithUrl(String urlStr) {
        try {
            URL imgUrl = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream is = conn.getInputStream();

            return getBytesFromInputStram(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get byte array from input stream
     *
     * @param is the instance of InputStream
     * @return the input stream's byte array
     * @throws Exception
     */
    private byte[] getBytesFromInputStram(InputStream is) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        is.close();
        return outStream.toByteArray();
    }


    /**
     * write image data to local file
     *
     * @param imgData the image's byte data
     * @param imgObj  the instance of RSSImage
     */
    private void writeImgDataToFile(byte[] imgData, BaseImage imgObj) {
        if (ArrayUtils.isEmpty(imgData)) {
            throw new IllegalArgumentException("the arg: imgData can not be null or empty");
        }

        if (imgObj == null || StringUtils.isEmpty(imgObj.getLocalPath())) {
            throw new IllegalArgumentException("the arg:imgObj can not be null " +
                "and the it's property: localPath can not be null or empty");
        }

        FileOutputStream fs = null;
        try {
            File imgFile = new File(imgObj.getLocalPath());
            logger.debug("will create image file at path:" + imgObj.getLocalPath());
            boolean createImgFileSuccess = imgFile.createNewFile();
            if (createImgFileSuccess) {
                fs = new FileOutputStream(imgFile);
                fs.write(imgData);
                fs.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
