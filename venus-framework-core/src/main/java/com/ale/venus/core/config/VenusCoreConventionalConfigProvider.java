package com.ale.venus.core.config;

import com.ale.venus.common.config.ConventionalConfigProvider;
import com.google.common.collect.Maps;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Map;

/**
 * Venus框架核心约定配置提供器
 *
 * @author Ale
 * @version 1.0.0
 */
public class VenusCoreConventionalConfigProvider implements ConventionalConfigProvider {

    @Override
    public Map<String, Object> provide(ConfigurableEnvironment environment, SpringApplication application) {

        Map<String, Object> configMap = Maps.newHashMap();
        configMap.put("mybatis-plus.global-config.banner", false);
        configMap.put("spring.mvc.format.date", "yyyy-MM-dd");
        configMap.put("spring.mvc.format.date-time", "yyyy-MM-dd HH:mm:ss");
        configMap.put("spring.mvc.format.time", "HH:mm:ss");
        configMap.put("spring.mvc.servlet.load-on-startup", 1);
        configMap.put("spring.mvc.throw-exception-if-no-handler-found", true);
        configMap.put("spring.data.web.pageable.page-parameter", "page");
        configMap.put("spring.data.web.pageable.size-parameter", "size");
        configMap.put("spring.servlet.multipart.enabled", true);
        configMap.put("spring.servlet.multipart.max-file-size", "512MB");
        configMap.put("spring.servlet.multipart.max-request-size", "512MB");
        configMap.put("spring.web.resources.add-mappings", false);
        configMap.put("spring.web.locale", "zh_CN");
        configMap.put("spring.web.locale-resolver", "fixed");

        return configMap;
    }
}
