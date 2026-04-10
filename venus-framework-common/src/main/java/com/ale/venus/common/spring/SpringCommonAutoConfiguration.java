package com.ale.venus.common.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * Spring通用自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@AutoConfigureBefore({WebMvcAutoConfiguration.class, WebFluxAutoConfiguration.class})
@EnableConfigurationProperties(VenusSpringCommonProperties.class)
public class SpringCommonAutoConfiguration {

    /**
     * 配置跨域过滤器
     *
     * @return 跨域过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "venus.common", name = "enable-cors", havingValue = "true")
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    public OrderedCorsFilter orderedCorsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration configuration = this.buildCorsConfiguration();
        source.setCorsConfigurations(Map.of("/**", configuration));
        return new OrderedCorsFilter(source);
    }

    /**
     * 构建跨域配置信息
     *
     * @return 跨域配置
     */
    private CorsConfiguration buildCorsConfiguration() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Collections.singletonList(CorsConfiguration.ALL));
        configuration.setAllowedMethods(Collections.singletonList(CorsConfiguration.ALL));
        configuration.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
        configuration.setExposedHeaders(Collections.singletonList(CorsConfiguration.ALL));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(Duration.ofDays(1));
        return configuration;
    }
}
