package com.rtms.core.contract;

import com.rtms.core.exception.RTMSConfigException;
import com.rtms.util.helper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * User: yanghua
 * Date: 1/21/14
 * Time: 4:09 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public abstract class AbstractConfigManager {

    private static final Logger logger = Logger.getLogger(AbstractConfigManager.class);

    private Properties rtmsConfig;
    private String configFilePath;
    private boolean validated = false;


    protected AbstractConfigManager(Properties rtmsConfig) {
        this.rtmsConfig = rtmsConfig;
        this.initAndValidate();
    }

    protected AbstractConfigManager(String rtmsConfigFilePath) {
        this.configFilePath = rtmsConfigFilePath;
        this.initAndValidate();
    }

    /**
     * get rtms's config instance
     *
     * @return the instance of Properties
     */
    public Properties rtmsConfig() {
        if (!this.validated) {
            throw new RuntimeException("the rtms's config can not be validated!");
        }

        return rtmsConfig;
    }

    /**
     * get full path from tool dir
     *
     * @param fileName the file name
     * @return the full path
     */
    public String joinPathToToolDir(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("the arg:fileName can not be null or empty");
        }

        return this.rtmsConfig().getProperty("rtms.toolDir") + "/" + fileName;
    }

    /**
     * get full path from data dir
     *
     * @param fileName the file name
     * @return the full path
     */
    public String joinPathToDataDir(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("the arg:fileName can not be null or empty");
        }

        return this.rtmsConfig().getProperty("rtms.dataDir") + "/" + fileName;
    }

    /**
     * get full path from templete dir
     *
     * @param fileName the file name
     * @return the full path
     */
    public String joinPathToTempleteDir(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("the arg:fileName can not be null or empty");
        }

        return this.rtmsConfig().getProperty("rtms.templeteDir") + "/" + fileName;
    }

    /**
     * get full path from config dir
     *
     * @param fileName the file name
     * @return the full path
     */
    public String joinPathToConfigDir(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            throw new IllegalArgumentException("the arg:fileName can not be null or empty");
        }

        return this.rtmsConfig().getProperty("rtms.configDir") + "/" + fileName;
    }

    /**
     * init and validate rtms's config item
     */
    private void initAndValidate() {
        try {
            if (this.rtmsConfig != null) {
                this.validated = this.validate();
            } else {
                this.rtmsConfig = new Properties();
                this.rtmsConfig.load(new FileInputStream(this.configFilePath));
                this.validated = this.validate();
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.validated = false;
        } catch (RTMSConfigException e) {
            e.printStackTrace();
            logger.error("please make sure run the script at :resources/dispatch.sh");
            this.validated = false;
        } catch (Exception e) {
            e.printStackTrace();
            this.validated = false;
        }
    }

    /**
     * check and validate default config items
     *
     * @return if validated then return true otherwise return false
     */
    private boolean validate() throws RTMSConfigException {
        if (StringUtils.isEmpty(this.rtmsConfig.getProperty("rtms.rootDir"))) {
            throw new RTMSConfigException("the rtms's config item:rtms.rootDir can not be null or empty");
        }

        if (StringUtils.isEmpty(this.rtmsConfig.getProperty("rtms.baseDir"))) {
            throw new RTMSConfigException("the rtms's config item:rtms.baseDir can not be null or empty");
        }

        if (StringUtils.isEmpty(this.rtmsConfig.getProperty("rtms.toolDir"))) {
            throw new RTMSConfigException("the rtms's config item:rtms.toolDir can not be null or empty");
        }

        if (StringUtils.isEmpty(this.rtmsConfig.getProperty("rtms.templeteDir"))) {
            throw new RTMSConfigException("the rtms's config item:rtms.templeteDir can not be null or empty");
        }

        if (StringUtils.isEmpty(this.rtmsConfig.getProperty("rtms.configDir"))) {
            throw new RTMSConfigException("the rtms's config item:rtms.configDir can not be null or empty");
        }

        if (StringUtils.isEmpty(this.rtmsConfig.getProperty("rtms.dataDir"))) {
            throw new RTMSConfigException("the rtms's config item:rtms.dataDir can not be null or empty");
        }

        if (StringUtils.isEmpty(this.rtmsConfig.getProperty("rtms.entryThreshold"))) {
            throw new RTMSConfigException("the rtms's config item:rtms.entryThreshold can not be null or empty");
        }

        if (StringUtils.isEmpty(this.rtmsConfig.getProperty("rtms.enableFullTxt"))) {
            throw new RTMSConfigException("the rtms's config item:rtms.enableFullTxt can not be null or empty");
        }

        if (StringUtils.isEmpty(this.rtmsConfig.getProperty("rtms.enableMailService"))) {
            throw new RTMSConfigException("the rtms's config item:rtms.enableMailService can not be null or empty");
        }

        if (StringUtils.isEmpty(this.rtmsConfig.getProperty("rtms.user"))) {
            throw new RTMSConfigException("the rtms's config item:rtms.user can not be null or empty");
        }

        if (StringUtils.isEmpty(this.rtmsConfig.getProperty("rtms.kindleformat"))) {
            throw new RTMSConfigException("the rtms's config item:rtms.kindleformat can not be null or empty");
        }

        if (!helper.isPathExists(this.rtmsConfig.getProperty("rtms.rootDir"))) {
            throw new RTMSConfigException("the rtms's comfig item:rtms.rootDir's path not exists");
        }

        if (!helper.isPathExists(this.rtmsConfig.getProperty("rtms.baseDir"))) {
            throw new RTMSConfigException("the rtms's comfig item:rtms.baseDir's path not exists");
        }

        if (!helper.isPathExists(this.rtmsConfig.getProperty("rtms.dataDir"))) {
            throw new RTMSConfigException("the rtms's comfig item:rtms.dataDir's path not exists");
        }

        if (!helper.isPathExists(this.rtmsConfig.getProperty("rtms.templeteDir"))) {
            throw new RTMSConfigException("the rtms's comfig item:rtms.templeteDir's path not exists");
        }

        if (!helper.isPathExists(this.rtmsConfig.getProperty("rtms.toolDir"))) {
            throw new RTMSConfigException("the rtms's comfig item:rtms.toolDir's path not exists");
        }

        if (!helper.isPathExists(this.rtmsConfig.getProperty("rtms.configDir"))) {
            throw new RTMSConfigException("the rtms's comfig item:rtms.configDir's path not exists");
        }

        return true;
    }

}
