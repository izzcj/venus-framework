package com.ale.venus.core.filter;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.web.servlet.DelegatingFilterProxyRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.OrderComparator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Venus过滤器自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
public class VenusFilterAutoConfiguration {

    /**
     * Venus过滤器名称
     */
    private static final String VENUS_FILTER_NAME = "venusFilter";

    /**
     * 代理过滤器Bean
     *
     * @param securityProperties 安全配置
     * @return 过滤器Bean
     */
    @Bean
    public DelegatingFilterProxyRegistrationBean venusFilterChainRegistration(ObjectProvider<SecurityProperties> securityProperties) {
        DelegatingFilterProxyRegistrationBean registration = new DelegatingFilterProxyRegistrationBean(
            VENUS_FILTER_NAME
        );
        registration.setOrder(
            this.getFilterOrder(securityProperties)
        );

        return registration;
    }

    /**
     * Venus过滤器链Bean
     *
     * @param filters Venus过滤器
     * @return 过滤器链Bean
     */
    @Bean(name = VENUS_FILTER_NAME)
    public VenusFilterChainFilter venusFilterChainFilter(ObjectProvider<VenusFilter> filters) {
        List<VenusFilter> vefFilters = filters.orderedStream().collect(Collectors.toList());
        OrderComparator.sort(vefFilters);
        return new VenusFilterChainFilter(vefFilters);
    }

    /**
     * 获取Venus过滤器链的排序
     *
     * @param securityProperties 安全配置
     * @return 排序
     */
    private int getFilterOrder(ObjectProvider<SecurityProperties> securityProperties) {
        // 保证过滤器在Spring Security过滤器之后执行
        return securityProperties.stream()
            .map(properties -> properties.getFilter().getOrder() + 20)
            .findFirst()
            .orElse(SecurityProperties.DEFAULT_FILTER_ORDER + 20);
    }

}
