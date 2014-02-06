package com.rtms.core.contract;

/**
 * User: yanghua
 * Date: 1/31/14
 * Time: 7:20 PM
 * Copyright (c) 2013 yanghua. All rights reserved.
 */
public interface IFullTxtOutput {

    /**
     * output full text from a entrylink
     *
     * @param entryLink the string of the entry link url
     * @return the full text
     */
    String fullTextFrom(String entryLink);

}
