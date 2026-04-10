package com.ale.venus.security.support;

import com.ale.venus.common.enumeration.RequestMethod;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Mvc路径模式
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
public class MvcPattern {

    /**
     * 请求方法
     */
    private RequestMethod method;

    /**
     * 请求路径
     */
    private String pattern;
}
