package com.ale.venus.common.config;

import cn.hutool.core.collection.CollectionUtil;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 约定配置处理器
 *
 * @author Ale
 * @version 1.0.0
 */
public class ConventionalConfigEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(@NonNull ConfigurableEnvironment environment, SpringApplication application) {
        ClassLoader classLoader = application.getClassLoader();

        List<ConventionalConfigProvider> conventionalConfigProviders = SpringFactoriesLoader
            .forDefaultResourceLocation(classLoader)
            .load(ConventionalConfigProvider.class);

        AnnotationAwareOrderComparator.sort(conventionalConfigProviders);

        Map<String, Object> configMap = new HashMap<>();
        for (ConventionalConfigProvider conventionalConfigProvider : conventionalConfigProviders) {
            Map<String, Object> map = conventionalConfigProvider.provide(environment, application);
            if (CollectionUtil.isNotEmpty(map)) {
                configMap.putAll(map);
            }
        }
        // 最后添加到配置源中，如果配置文件存在相同配置则以配置文件配置为准
        environment.getPropertySources()
            .addLast(new MapPropertySource("conventionalConfig", configMap));
    }

}
