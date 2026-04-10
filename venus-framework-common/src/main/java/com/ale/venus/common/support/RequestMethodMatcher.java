package com.ale.venus.common.support;

import org.springframework.http.HttpMethod;

/**
 * 请求方法匹配器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface RequestMethodMatcher {

    /**
     * 匹配任意方法
     */
    RequestMethodMatcher ANY = method -> true;

    /**
     * 是否匹配Http请求方法
     *
     * @param method Http请求方法
     * @return 是否匹配
     */
    boolean matches(HttpMethod method);

}
