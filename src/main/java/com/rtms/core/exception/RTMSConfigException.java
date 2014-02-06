package com.rtms.core.exception;

/**
 * User: yanghua
 * Date: 2/4/14
 * Time: 3:03 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class RTMSConfigException extends Exception {

    public RTMSConfigException(String s) {
        super(s);
    }

    public RTMSConfigException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public RTMSConfigException(Throwable throwable) {
        super(throwable);
    }
}
