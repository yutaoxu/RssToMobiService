package com.rtms.component;

/**
 * User: yanghua
 * Date: 2/4/14
 * Time: 5:16 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public interface IFileStripHandler {

    /**
     * do strip
     * @param originalFilePath the original file path
     * @param newFilePath the new path
     */
    void doStrip(String originalFilePath, String newFilePath);

}
