package com.ale.venus.common.config;

import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * 约定配置提供器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface ConventionalConfigProvider {

    /**
     * 提供约定配置
     *
     * @param environment Spring环境对象
     * @param application Spring应用对象
     * @return 约定配置
     */
    Map<String, Object> provide(ConfigurableEnvironment environment, SpringApplication application);
}
