package com.ale.venus.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

/**
 * Venus过滤器链
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class VenusFilterChain implements FilterChain {

    /**
     * 原始过滤器链
     */
    private final FilterChain origineFilterChain;

    /**
     * venus过滤器
     */
    private final List<VenusFilter> venusFilters;

    /**
     * 过滤器数量
     */
    private final int size;

    /**
     * 过滤器当前位置
     */
    private int currentPosition;

    public VenusFilterChain(FilterChain filterChain, List<VenusFilter> venusFilters) {
        this.origineFilterChain = filterChain;
        this.venusFilters = venusFilters;
        this.size = venusFilters.size();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        this.currentPosition++;
        VenusFilter next = this.venusFilters.get(this.currentPosition - 1);
        next.doFilter((HttpServletRequest) request, (HttpServletResponse) response, this.currentPosition == this.size ? this.origineFilterChain : this);
    }
}
