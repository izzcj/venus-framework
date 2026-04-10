package com.ale.venus.security.config;

import com.ale.venus.common.config.ConventionalConfigProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * Venus安全框架配置提供器
 *
 * @author Ale
 * @version 1.0.0
 */
public class VenusSecurityConventionalConfigProvider implements ConventionalConfigProvider {

    @Override
    public Map<String, Object> provide(ConfigurableEnvironment environment, SpringApplication application) {
        return new HashMap<>();
    }
}
