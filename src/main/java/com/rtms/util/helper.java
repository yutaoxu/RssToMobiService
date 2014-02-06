package com.rtms.util;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * User: yanghua
 * Date: 1/11/14
 * Time: 4:49 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class helper {

    private static final Logger logger = Logger.getLogger(helper.class);

    public static Gson gson = new Gson();

    /**
     * judge whether the original is ending with one of the items
     *
     * @param original the original string
     * @param items    the matching items
     * @return if end with one of items then return true otherwise return false
     */
    public static boolean endWithOneOfMulti(String original, String[] items) {
        if (original == null || original.isEmpty() || items == null || items.length == 0) {
            return false;
        }

        for (int i = 0; i < items.length; i++) {
            if (original.endsWith(items[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * get current project's root path
     *
     * @return the absolute path
     */
    public static String getProjectRootPath() {
        return new File("").getAbsolutePath();
    }

    /**
     * judge a path has existed
     *
     * @param path a str of dir path
     * @return if dir has existed then return true otherwise return false
     */
    public static boolean isPathExists(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("the arg: path can not be null or empty");
        }

        return new File(path).exists();
    }


    /**
     * create dir with a absolute path
     *
     * @param path the dir path
     */
    public static void createDirWithAbsolutePath(String path) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("the arg: path can not be null or empty");
        }

        new File(path).mkdir();
    }

    /**
     * get MD5 code
     *
     * @param source the sorce string's bytes
     * @return the md5 chars
     */
    public static String getMD5Code(byte[] source) {
        String s = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            java.security.MessageDigest md = java.security.MessageDigest
                .getInstance("MD5");
            md.update(source);
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];

            }
            s = new String(str);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return s;
    }

    /**
     * get file name from path or url's str
     *
     * @param originalStr file path or url str
     * @return real file name
     */
    public static String getFileNameFromPathOrUrl(String originalStr) {
        if (originalStr == null || originalStr.length() == 0) {
            throw new IllegalArgumentException("the arg: originalStr is illegal");
        }

        int lastSlashIndex = originalStr.lastIndexOf("/");
        if (lastSlashIndex == -1) {
            return originalStr;
        }

        return originalStr.substring(lastSlashIndex + 1);
    }

}
