package com.ale.venus.core.filter;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Venus过滤器链
 *
 * @author Ale
 * @version 1.0.0
 */

@RequiredArgsConstructor
public class VenusFilterChainFilter extends OncePerRequestFilter {

    /**
     * Venus过滤器
     */
    private final List<VenusFilter> venusFilters;

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        new VenusFilterChain(filterChain, venusFilters).doFilter(request, response);
    }
}
