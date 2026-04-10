package com.ale.venus.core.controller;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Controller拓展自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
public class ControllerExtensionAutoConfiguration {

    /**
     * 控制器异常处理器
     *
     * @return 控制器异常处理器Bean
     */
    @Bean
    public ControllerExceptionHandler controllerExceptionHandler() {
        return new ControllerExceptionHandler();
    }
}
