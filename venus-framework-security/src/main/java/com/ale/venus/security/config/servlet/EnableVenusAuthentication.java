package com.ale.venus.security.config.servlet;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启Venus认证
 * 不开启则需要自行实现认证逻辑
 *
 * @author Ale
 * @version 1.0.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(LoginConfigurationClassSelector.class)
public @interface EnableVenusAuthentication {

    /**
     * 是否开启登录频率限制
     *
     * @return bool
     */
    boolean limitLoginRate() default true;
}
