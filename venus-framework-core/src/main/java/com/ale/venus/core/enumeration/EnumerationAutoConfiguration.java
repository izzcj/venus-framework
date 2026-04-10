package com.ale.venus.core.enumeration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 枚举自动配置类
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
public class EnumerationAutoConfiguration {

    /**
     * 获取枚举选项端点bean
     *
     * @return 枚举选项端点
     */
    @Bean
    public EnumerationOptionsEndpoint enumerationOptionsEndpoint() {
        return new EnumerationOptionsEndpoint();
    }

}
