package com.ale.venus.core.share;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 共享数据上下文自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@RequiredArgsConstructor
@EnableConfigurationProperties(SharedDataContextProperties.class)
public class SharedDataContextAutoConfiguration {

    /**
     * 共享数据上下文配置
     */
    private final SharedDataContextProperties properties;

    /**
     * 创建共享数据上下文工厂
     *
     * @param redisTemplate redis模板
     * @return 共享数据上下文工厂
     */
    @Bean
    public SharedDataContextFactory sharedDataContextFactory(RedisTemplate<String, Object> redisTemplate) {
        return new SharedDataContextFactory(redisTemplate, this.properties.isClearOnLifecycle());
    }

}
