package com.ale.venus.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;

import java.io.IOException;

/**
 * Venus过滤器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface VenusFilter extends Ordered {

    /**
     * 过滤
     *
     * @param request     请求
     * @param response    响应
     * @param filterChain 过滤器链
     */
    void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException;

}
