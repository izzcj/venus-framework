package com.ale.venus.security.authorization.method;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.common.exception.ExceptionCode;
import com.ale.venus.common.security.PermissionCheck;
import com.ale.venus.common.security.PermissionGroup;
import com.ale.venus.security.exception.InsufficientPermissionException;
import lombok.RequiredArgsConstructor;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.ObjectProvider;

import java.lang.reflect.Method;

/**
 * 权限校验方法拦截器
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class PermissionCheckingMethodInterceptor implements MethodInterceptor {

    /**
     * 方法权限检查器
     */
    private final ObjectProvider<MethodPermissionChecker> methodPermissionCheckers;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
        Object target = invocation.getThis();
        if (target == null) {
            throw new NullPointerException("target is null");
        }
        Class<?> targetClass = target.getClass();

        String permission = this.buildPermission(method, targetClass);
        this.checkPermission(targetClass, target, method, permission);

        return invocation.proceed();
    }

    /**
     * 检查权限
     *
     * @param targetClass 目标类
     * @param target      目标对象
     * @param method      方法反射对象
     * @param permission  权限标识
     */
    private void checkPermission(Class<?> targetClass, Object target, Method method, String permission) {
        for (MethodPermissionChecker methodPermissionChecker : this.methodPermissionCheckers) {
            boolean result = methodPermissionChecker.check(targetClass, target, method, permission);
            if (result) {
                return;
            }
        }

        throw new InsufficientPermissionException(ExceptionCode.ACCESS_DENIED);
    }

    /**
     * 构建权限字符串
     *
     * @param method      方法反射对象
     * @param targetClass 目标类
     * @return 权限字符串
     */
    private String buildPermission(Method method, Class<?> targetClass) {
        String group = null;
        PermissionGroup permissionGroup = targetClass.getAnnotation(PermissionGroup.class);
        if (permissionGroup != null) {
            group = permissionGroup.value();
        }
        PermissionCheck permissionChecking = method.getAnnotation(PermissionCheck.class);
        String permission = permissionChecking.value();

        if (StrUtil.isNotBlank(group)) {
            permission = group + StringConstants.COLON + permission;
        }

        return permission;
    }
}
