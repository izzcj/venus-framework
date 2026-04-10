package com.ale.venus.security.authorization.method;

import java.lang.reflect.Method;

/**
 * 方法权限校验器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface MethodPermissionChecker {

    /**
     * 检查权限
     *
     * @param targetClass 目标类
     * @param target      目标类实例
     * @param method      目标方法
     * @param permission  权限字符串
     * @return 是否有权限
     */
    boolean check(Class<?> targetClass, Object target, Method method, String permission);
}
