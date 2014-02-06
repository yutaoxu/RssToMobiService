package com.rtms.impl;

import com.rtms.core.contract.AbstractConfigManager;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * User: yanghua
 * Date: 1/21/14
 * Time: 4:06 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class ConfigManager extends AbstractConfigManager {

    private static final Logger logger = Logger.getLogger(ConfigManager.class);

    private Properties redisConfig;
    private Properties mailConfig;
    private String redisConfigFilePath;
    private String mailConfigFilePath;
    private boolean inited = false;

    public static final String ENTRY_KEY_PATTERN = "ENTRY:%s";
    public static final String IMAGE_KEY_PATTERN = "IMAGE:%s";
    public static final String STRIPPED_PATH_PATTERN = "%s_strip.%s";

    public ConfigManager(Properties rtmsConfig, Properties redisConfig, Properties mailConfig) {
        super(rtmsConfig);
        this.redisConfig = redisConfig;
        this.mailConfig = mailConfig;
        this.init();
    }

    public ConfigManager(String rtmsConfigFilePath, String redisConfigFilePath, String mailConfigFilePath) {
        super(rtmsConfigFilePath);
        this.redisConfigFilePath = redisConfigFilePath;
        this.mailConfigFilePath = mailConfigFilePath;
        this.init();
    }

    /**
     * init Object's inner status
     */
    private void init() {
        if (this.redisConfig != null && this.mailConfig != null) {
            this.inited = true;
        } else {
            this.redisConfig = new Properties();
            this.mailConfig = new Properties();

            try {
                this.redisConfig.load(new FileInputStream(this.redisConfigFilePath));
                this.mailConfig.load(new FileInputStream(this.mailConfigFilePath));
                this.inited = true;
            } catch (IOException e) {
                this.inited = false;
                e.printStackTrace();
            }
        }
    }

    public Properties redisConfig() {
        if (!this.inited) {
            throw new RuntimeException("the ConfigManager has wrong status");
        }

        return this.redisConfig;
    }

    public Properties mailConfig() {
        if (!this.inited) {
            throw new RuntimeException("the ConfigManager has wrong status");
        }

        return this.mailConfig;
    }

}
