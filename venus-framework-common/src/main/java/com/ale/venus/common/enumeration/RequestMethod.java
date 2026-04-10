package com.ale.venus.common.enumeration;

/**
 * http请求方法枚举
 *
 * @author Ale
 * @version 1.0.0
 */
public enum RequestMethod implements BaseEnum<String> {

    /**
     * 所有方法
     */
    ALL,
    /**
     * GET方法
     */
    GET,
    /**
     * POST方法
     */
    POST,
    /**
     * PUT方法
     */
    PUT,
    /**
     * DELETE方法
     */
    DELETE;

    RequestMethod() {
        init();
    }
}
