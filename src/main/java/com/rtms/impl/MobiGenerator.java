package com.rtms.impl;

import com.rtms.component.IFileStripHandler;
import com.rtms.core.base.BaseEntry;
import com.rtms.core.base.BaseFeed;
import com.rtms.core.contract.AbstractConfigManager;
import com.rtms.core.contract.IMobiGenerator;
import com.rtms.util.helper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: yanghua
 * Date: 1/11/14
 * Time: 12:35 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class MobiGenerator implements IMobiGenerator {

    private static final Logger logger = Logger.getLogger(MobiGenerator.class);

    private Configuration cfg;
    private static String[] templeteFileNames = {
        "templete_content.html",
        "templete_content.opf",
        "templete_toc.ncx"
    };
    private AbstractConfigManager configManager;

    public MobiGenerator(AbstractConfigManager configManager) {
        this.configManager = configManager;
        init();
    }

    /**
     * init the object's env
     */
    private void init() {
        try {
            this.cfg = new Configuration();
            logger.debug("the rtms.templeteDir is:" + this.configManager.rtmsConfig().getProperty("rtms.templeteDir"));
            File resourceDir = new File(this.configManager.rtmsConfig().getProperty("rtms.templeteDir"));
            this.cfg.setDirectoryForTemplateLoading(resourceDir);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String generate(List<BaseFeed> feeds) {
        initDirs(feeds);
        Map<String, Object> mobiInfo = this.generateRawObjForTemplate(feeds);
        this.generateComponentFileWithTemplate(mobiInfo, templeteFileNames);
        return this.generateMobiFile();
    }

    /**
     * generate the raw Map object for template
     *
     * @param feeds the instance of RSSFeed
     * @return the instance of Map
     */
    private Map<String, Object> generateRawObjForTemplate(List<BaseFeed> feeds) {
        Map<String, Object> result = new HashMap<String, Object>();

        String user = this.configManager.rtmsConfig().getProperty("rtms.user");

        String kindleFormat = this.configManager.rtmsConfig().getProperty("rtms.kindleFormat");
        if (kindleFormat == null) {
            kindleFormat = "book";
        }

        result.put("user", user);
        result.put("feeds", this.generateFeedArrForTemplate(feeds));
        result.put("uuid", helper.getMD5Code(user.getBytes()));
        result.put("format", kindleFormat);
        result.put("mobitime", new Date());

        return result;
    }

    /**
     * generateComponentFileWithTemplate the content to the template
     *
     * @param mobiInfo  the mobi info
     * @param fileNames the array of file names
     */
    private void generateComponentFileWithTemplate(Map<String, Object> mobiInfo, String[] fileNames) {
        try {
            for (int i = 0; i < fileNames.length; i++) {
                Template templete = this.cfg.getTemplate(fileNames[i]);
                StringWriter sw = new StringWriter();
                templete.process(mobiInfo, sw);
                sw.flush();

                StringBuffer sb = sw.getBuffer();
                sw.close();

                writeContentToFile(sb.toString(), fileNames[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * generate feed obj array for freemarker template
     *
     * @param feeds the RSSFeed's instance array
     * @return the generated obj arr
     */
    private List<HashMap<String, Object>> generateFeedArrForTemplate(List<BaseFeed> feeds) {
        List<HashMap<String, Object>> mobiFeedArr = new ArrayList<HashMap<String, Object>>(feeds.size());
        for (int i = 0; i < feeds.size(); i++) {
            mobiFeedArr.add(generateFeedObjForTemplate(feeds.get(i)));
        }

        return mobiFeedArr;
    }

    /**
     * generate feed object for template
     *
     * @param feed the instance of RSSFeed
     * @return the generated Object
     */
    private HashMap<String, Object> generateFeedObjForTemplate(BaseFeed feed) {

        HashMap<String, Object> feedObj = new HashMap<String, Object>();
        feedObj.put("title", feed.getTitle());
        feedObj.put("entryCount", feed.getEntries().size());

        List<HashMap<String, Object>> feedEntries = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < feed.getEntries().size(); i++) {
            BaseEntry rssItem = feed.getEntries().get(i);
            HashMap<String, Object> item = new HashMap<String, Object>();
            item.put("title", rssItem.getTitle());
            item.put("published", rssItem.getPubDate());
            item.put("url", rssItem.getLink());
            item.put("content", rssItem.getDescription());

            feedEntries.add(item);
        }

        feedObj.put("entries", feedEntries);

        return feedObj;
    }

    /**
     * write content to file
     *
     * @param content          a instance of StringWriter
     * @param templateFileName the template's file name
     */
    private void writeContentToFile(String content, String templateFileName) {
        if (content == null) {
            throw new NullPointerException("the arg:sw can not be null");
        }

        String fileName = processTemplateFileName(templateFileName);
        String fileFullPath = this.configManager.joinPathToDataDir(fileName);

        try {
            File newFile = new File(fileFullPath);
            if (!newFile.exists()) {
                boolean isSuccess = newFile.createNewFile();
                if (!isSuccess) {
                    throw new RuntimeException("create new file failed at path:" + fileFullPath);
                }
            }

            FileOutputStream is = new FileOutputStream(newFile);
            is.write(content.getBytes());
            is.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * generate real mobi file
     *
     * @return the generated mobi file path
     */
    private String generateMobiFile() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        String mobi8FileName = String.format("KindleReader8-%s.mobi", sdf.format(new Date()));
        String mobiFileName = String.format("KindleReader-%s.mobi", sdf.format(new Date()));
        String optFilePath = this.configManager.joinPathToDataDir(processTemplateFileName(templeteFileNames[1]));
        String mobi8FilePath = this.configManager.joinPathToDataDir(mobi8FileName);

        String cmdStr = String.format("kindlegen %s -o %s ", optFilePath, mobi8FileName);

        String finalPath = mobi8FilePath;

        try {
            Process process;
            process = Runtime.getRuntime().exec(cmdStr);
            process.waitFor();

            String result = IOUtils.toString(process.getInputStream());
            logger.debug("kindlegen output info :" + result);
            logger.info("mobi file path is :" + mobi8FilePath);

            Properties rtmsConfig = this.configManager.rtmsConfig();
            if (rtmsConfig.getProperty("rtms.enableStrip").equalsIgnoreCase("true")) {
                finalPath = this.stripMobiFile(mobi8FilePath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return finalPath;
    }

    /**
     * handle real file name (remove string {template_} )
     *
     * @param templateFileName the template's file name
     * @return fixed file name
     */
    private String processTemplateFileName(String templateFileName) {
        if (StringUtils.isEmpty(templateFileName)) {
            throw new IllegalArgumentException("the arg: templateFileName can not be null or empty");
        }

        int idx = templateFileName.indexOf("_");
        if (idx == -1) {
            return templateFileName;
        }

        return templateFileName.substring(idx + 1, templateFileName.length());
    }

    /**
     * init dirs for feed
     */
    private void initDirs(List<BaseFeed> feeds) {
        for (int i = 0; i < feeds.size(); i++) {
            this.initFeedDir(feeds.get(i));
        }
    }

    /**
     * create dir for feed
     *
     * @param feed the instance of RSSFeed
     */
    private void initFeedDir(BaseFeed feed) {
        if (feed == null) {
            throw new NullPointerException("the arg: feed can not be null");
        }

        String feedUUID = helper.getMD5Code(feed.getLink().getBytes());
        String fullPath = this.configManager.joinPathToDataDir(feedUUID);

        if (!helper.isPathExists(fullPath)) {
            boolean isSuccess = new File(fullPath).mkdir();
            if (!isSuccess) {
                throw new RuntimeException("can not create dir at path:" + fullPath);
            }
        }

        initDirsForEveryEntry(feed, fullPath);
    }

    /**
     * create dirs for every entry
     *
     * @param feed    the instance of RSSFeed
     * @param baseDir the feed's full path
     */
    private void initDirsForEveryEntry(BaseFeed feed, String baseDir) {
        String entryUUID, fullPath;

        for (BaseEntry entry : feed.getEntries()) {
            entryUUID = helper.getMD5Code(entry.getLink().getBytes());
            fullPath = String.format("%s/%s", baseDir, entryUUID);

            if (!helper.isPathExists(fullPath)) {
                boolean success = new File(fullPath).mkdir();
                if (!success) {
                    throw new RuntimeException("can not create dir at path:" + fullPath);
                }
            }
        }
    }

    /**
     * strip mobi file
     *
     * @param originalFilePath the generated original mobi file's path
     * @return the stripped file path
     */
    private String stripMobiFile(String originalFilePath) {
        String strippedFilePath;
        IFileStripHandler mobiStripHandler;
        String strippedFileName;

        if (this.configManager instanceof ConfigManager) {
            ConfigManager realConfig = (ConfigManager) this.configManager;

            strippedFileName = String.format(
                realConfig.STRIPPED_PATH_PATTERN,
                FilenameUtils.getBaseName(originalFilePath),
                FilenameUtils.getExtension(originalFilePath)
            );

        } else {
            strippedFileName = String.format("%s_strip.%s",
                FilenameUtils.getBaseName(originalFilePath),
                FilenameUtils.getExtension(originalFilePath));
        }

        strippedFilePath = FilenameUtils.getFullPath(originalFilePath) + strippedFileName;

        mobiStripHandler = new MobiFileStripHandler(this.configManager);
        mobiStripHandler.doStrip(originalFilePath, strippedFilePath);

        return strippedFilePath;
    }
}
