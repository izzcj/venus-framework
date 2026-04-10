package com.ale.venus.common.exception.config;

import com.ale.venus.common.exception.handler.GlobalExceptionCatchFilter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;

/**
 * 异常拓展自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ExceptionExtensionAutoConfiguration {

    /**
     * 全局异常捕获过滤器
     *
     * @return 过滤器Bean
     */
    @Bean
    public GlobalExceptionCatchFilter globalExceptionCatchFilter() {
        return new GlobalExceptionCatchFilter();
    }
}
