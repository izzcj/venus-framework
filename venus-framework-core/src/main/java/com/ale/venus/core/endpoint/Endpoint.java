package com.ale.venus.core.endpoint;

import com.ale.venus.common.domain.Result;
import com.ale.venus.common.support.RequestMethodMatcher;
import com.ale.venus.common.support.RequestUriMatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 端点接口
 *
 * @author Ale
 * @version 1.0.0
 */
public interface Endpoint {

    /**
     * 获取请求URI匹配器
     *
     * @return 请求URI匹配器
     */
    RequestUriMatcher getRequestUriMatcher();

    /**
     * 获取请求方法匹配器
     *
     * @return 请求方法匹配器
     */
    RequestMethodMatcher getRequestMethodMatcher();

    /**
     * 处理请求
     *
     * @param request 请求对象
     * @param response 响应对象
     * @return 结果对象
     */
    Result<?> handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
}
