package com.rtms.impl;

import com.rtms.component.IFileStripHandler;
import com.rtms.core.contract.AbstractConfigManager;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: yanghua
 * Date: 2/4/14
 * Time: 5:28 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class MobiFileStripHandler implements IFileStripHandler {

    private static final Logger logger = Logger.getLogger(MobiFileStripHandler.class);
    private static Runtime processor = Runtime.getRuntime();
    private static final String cmdPattern = "%s %s %s";
    private AbstractConfigManager configManager;

    public MobiFileStripHandler(AbstractConfigManager configManager) {
        this.configManager = configManager;
    }

    /**
     * do strip
     *
     * @param originalFilePath the original file path
     * @param newFilePath      the new path
     */
    @Override
    public void doStrip(String originalFilePath, String newFilePath) {
        if (StringUtils.isEmpty(originalFilePath)) {
            throw new IllegalArgumentException("the arg: originalFilePath can not be null or empty");
        }

        if (StringUtils.isEmpty(newFilePath)) {
            throw new IllegalArgumentException("the arg: newFilePath can not be null or empty");
        }

        String cmd = String.format(cmdPattern, this.configManager.joinPathToToolDir("kindlestrip"), originalFilePath, newFilePath);
        logger.debug("the kindlestrip cmd is :" + cmd);

        try {
            Process p = processor.exec(cmd);
            p.waitFor();

            InputStream is = p.getInputStream();
            String errMsg = IOUtils.toString(p.getErrorStream());
            logger.error(errMsg);
            logger.debug(IOUtils.toString(is));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
