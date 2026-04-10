package com.ale.venus.core.endpoint;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 端点自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
public class EndpointAutoConfiguration {

    /**
     * Venus端点过滤器Bean
     *
     * @param endpointObjectProvider 端点对象提供器
     * @return Venus端点过滤器
     */
    @Bean
    public VenusEndpointFilter venusEndpointFilter(ObjectProvider<Endpoint> endpointObjectProvider) {
        return new VenusEndpointFilter(endpointObjectProvider);
    }

}
