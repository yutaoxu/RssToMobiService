package com.rtms.impl;

import com.rtms.core.contract.AbstractConfigManager;
import com.rtms.core.contract.IFullTxtOutput;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;


/**
 * User: yanghua
 * Date: 1/31/14
 * Time: 7:22 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class FullTxtOutput implements IFullTxtOutput {

    private static Logger logger = Logger.getLogger(FullTxtOutput.class);
    private static Runtime processor = Runtime.getRuntime();
    private static final String cmdPattern = "%s %s";
    private AbstractConfigManager configManager;

    public FullTxtOutput(AbstractConfigManager configManager) {
        this.configManager = configManager;
    }

    @Override
    public String fullTextFrom(String entryLink) {
        if (StringUtils.isEmpty(entryLink)) {
            throw new IllegalArgumentException("the arg: entryLink can not be null or empty");
        }

        String result = "";

        try {
            String cmd = String.format(cmdPattern, this.configManager.joinPathToToolDir("fullTxt"), entryLink);
            logger.debug("the fullTxt cmd is :" + cmd);
            Process p = processor.exec(cmd);
            p.waitFor();

            InputStream is = p.getInputStream();

            String errMsg = IOUtils.toString(p.getErrorStream());
            logger.error(errMsg);

            result = IOUtils.toString(is);

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

}
