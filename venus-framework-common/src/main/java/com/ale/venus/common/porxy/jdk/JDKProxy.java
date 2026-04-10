package com.ale.venus.common.porxy.jdk;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.porxy.AbstractProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 基于JDK的动态代理
 *
 * @param <T> 原始对象类型
 * @author Ale
 * @version 1.0.0
 */
public class JDKProxy<T> extends AbstractProxy<T> implements InvocationHandler {

    @Override
    protected Object create(T originalObject) {
        if (AopUtils.isCglibProxy(originalObject)) {
            throw new UnsupportedOperationException(StrUtil.format("被代理类[{}]为Cglib代理类，无法使用Jdk代理", originalObject.getClass()));
        }
        Class<?> originalObjectClass = this.originalObject.getClass();
        Class<?>[] interfaces = originalObjectClass.getInterfaces();
        if (interfaces.length == 0) {
            throw new IllegalArgumentException("JDK代理必须实现接口");
        }
        return Proxy.newProxyInstance(
            originalObjectClass.getClassLoader(),
            interfaces,
            this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this.originalObject, args);
        }
        return this.methodInvoke(method, args);
    }
}
