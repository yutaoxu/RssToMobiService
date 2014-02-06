package com.rtms.core.base;

/**
 * User: yanghua
 * Date: 1/15/14
 * Time: 6:40 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public class BaseImage {

    private String domain;
    private String src;
    private String ref;
    private String localPath;

    public BaseImage() {
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    @Override
    public String toString() {
        return "BaseImage{" +
            "domain='" + domain + '\'' +
            ", src='" + src + '\'' +
            ", ref='" + ref + '\'' +
            ", localPath='" + localPath + '\'' +
            '}';
    }
}
