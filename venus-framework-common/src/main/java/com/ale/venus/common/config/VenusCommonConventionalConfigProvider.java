package com.ale.venus.common.config;

import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * Venus框架通用约定配置提供器
 *
 * @author Ale
 * @version 1.0.0
 */
public class VenusCommonConventionalConfigProvider implements ConventionalConfigProvider {

    @Override
    public Map<String, Object> provide(ConfigurableEnvironment environment, SpringApplication application) {
        Map<String, Object> configMap = new HashMap<>();
        configMap.put("spring.main.allow-circular-references", true);
        configMap.put("spring.jmx.enabled", false);
        configMap.put("spring.data.redis.client-type", "lettuce");
        configMap.put("spring.data.redis.client-name", "venus-client");
        configMap.put("spring.autoconfigure.exclude[0]", "org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration");
        configMap.put("management.metrics.export.jmx.enabled", false);
        configMap.put("logging.pattern.console", "%clr(%d{${LOG_DATEFORMAT_PATTERN:yyyy-MM-dd HH:mm:ss.SSS}}){faint} %clr(${LOG_LEVEL_PATTERN:%-5p}) %clr(${PID:- }){magenta} %clr([%t]){faint} %clr(%logger){cyan} %clr(:){faint} %m%n${LOG_EXCEPTION_CONVERSION_WORD:%xEx}");
        configMap.put("server.error.whitelabel.enabled", "false");
        configMap.put("spring.mvc.throwExceptionIfNoHandlerFound", "true");

        return configMap;
    }

}
