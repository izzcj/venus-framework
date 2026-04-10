package com.ale.venus.core.endpoint;

import com.ale.venus.common.domain.JsonResult;
import com.ale.venus.common.domain.Result;
import com.ale.venus.common.exception.ExceptionCode;
import com.ale.venus.common.utils.ServletUtils;
import com.ale.venus.core.constants.VenusFilterConstants;
import com.ale.venus.core.filter.VenusFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;

/**
 * Venus端点过滤器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class VenusEndpointFilter implements VenusFilter {

    /**
     * 端点对象提供器
     */
    private final ObjectProvider<Endpoint> endpointObjectProvider;

    @Override
    public int getOrder() {
        return VenusFilterConstants.VENUS_ENDPOINT_FILTER_ORDER;
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        for (Endpoint endpoint : this.endpointObjectProvider) {
            // 方法类型及路径匹配，优先方法匹配节省开销
            boolean isMatched = endpoint.getRequestMethodMatcher().matches(method)
                && endpoint.getRequestUriMatcher().matches(uri);
            if (isMatched) {
                this.handleRequest(endpoint, request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 处理请求
     *
     * @param endpoint 端点处理器
     * @param request  请求对象
     * @param response 响应对象
     */
    private void handleRequest(Endpoint endpoint, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Result<?> apiResult = endpoint.handleRequest(request, response);
            if (apiResult == null) {
                return;
            }

            ServletUtils.responseJson(
                response,
                HttpStatus.OK,
                apiResult
            );
        } catch (Exception e) {
            // 处理请求发生异常
            log.error("端点[{}]请求处理器处理请求[{} - {}]发生异常：{}", endpoint.getClass().getSimpleName(), request.getMethod(), request.getRequestURI(), e.getMessage(), e);
            if (response.isCommitted()) {
                return;
            }

            ServletUtils.responseJson(
                response,
                HttpStatus.INTERNAL_SERVER_ERROR,
                JsonResult.fail(ExceptionCode.DEFAULT_ERROR.getCode(), "服务器异常：{}", e.getMessage())
            );
        }
    }
}
