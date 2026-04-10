package com.ale.venus.common.support;

import cn.hutool.core.util.ArrayUtil;
import com.ale.venus.common.exception.ReflectionException;
import com.ale.venus.common.utils.CastUtils;
import com.google.common.collect.Lists;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * 反射方法
 *
 * @param method       方法反射对象
 * @param methodHandle 方法句柄
 * @author Ale
 * @version 1.0.0
 */
public record ReflectionMethod(Method method, MethodHandle methodHandle) {

    /**
     * 调用方法
     *
     * @param args 调用参数
     * @param <T>  结果类型
     * @return 返回结果
     */
    public <T> T invoke(Object instance, Object... args) {
        if (instance == null) {
            throw new ReflectionException("调用方法【{}.{}】的实例不能为空", this.method.getDeclaringClass().getSimpleName(), this.method.getName());
        }

        try {
            Object result;
            if (ArrayUtil.isEmpty(args)) {
                result = this.methodHandle.invoke(instance);
            } else {
                List<Object> invokeArgs = Lists.newArrayListWithCapacity(args.length + 1);
                invokeArgs.add(instance);
                invokeArgs.addAll(Arrays.asList(args));
                result = this.methodHandle.invokeWithArguments(invokeArgs);
            }

            return CastUtils.cast(result);
        } catch (Throwable e) {
            throw new ReflectionException("调用方法【{}】失败：{}", e, this.method.getName(), e.getMessage());
        }
    }
}
