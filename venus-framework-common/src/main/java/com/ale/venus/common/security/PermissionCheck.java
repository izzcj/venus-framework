package com.ale.venus.common.security;

import java.lang.annotation.*;

/**
 * 权限检查注解
 *
 * @author Ale
 * @version 1.0.0
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionCheck {

    /**
     * 权限标识
     *
     * @return 权限标识
     */
    String value();

}
