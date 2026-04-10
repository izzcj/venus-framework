package com.ale.venus.common.spring;

import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.boot.web.servlet.filter.OrderedFormContentFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 带顺序的CORS过滤器
 *
 * @author Ale
 * @version 1.0.0
 */
public class OrderedCorsFilter extends CorsFilter implements OrderedFilter {

    /**
     * 默认排序
     */
    public static final int DEFAULT_ORDER = OrderedFormContentFilter.DEFAULT_ORDER - 9900;

    public OrderedCorsFilter(CorsConfigurationSource configSource) {
        super(configSource);
    }

    @Override
    public int getOrder() {
        return DEFAULT_ORDER;
    }
}
