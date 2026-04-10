package com.ale.venus.common.security;

import java.lang.annotation.*;

/**
 * 权限分组
 *
 * @author Ale
 * @version 1.0.0
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PermissionGroup {

    /**
     * 权限组标识
     *
     * @return 权限组标识
     */
    String value();
}
