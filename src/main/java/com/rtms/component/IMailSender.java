package com.rtms.component;

/**
 * User: yanghua
 * Date: 1/31/14
 * Time: 7:18 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public interface IMailSender {

    /**
     * send mobi file from path
     * @param filePath the mobi file path
     */
    void sendFrom(String filePath);

}
