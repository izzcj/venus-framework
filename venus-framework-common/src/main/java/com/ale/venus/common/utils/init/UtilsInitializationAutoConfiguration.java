package com.ale.venus.common.utils.init;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 工具类初始化自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
public class UtilsInitializationAutoConfiguration {

    /**
     * Redis工具类初始化Bean
     *
     * @return 初始化Bean
     */
    @Bean(autowireCandidate = false)
    public static RedisUtilsInitializer redisUtilsInitializer() {
        return new RedisUtilsInitializer();
    }

    /**
     * Json工具类初始化Bean
     *
     * @return 初始化Bean
     */
    @Bean(autowireCandidate = false)
    public static JsonUtilsInitializer jsonUtilsInitializer() {
        return new JsonUtilsInitializer();
    }
}
