package com.ale.venus.common.porxy.cglib;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ale.venus.common.porxy.AbstractProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 基于Cglib的代理
 *
 * @param <T> 原始对象类型
 * @author Ale
 * @version 1.0.0
 */
public class CglibProxy<T> extends AbstractProxy<T> implements MethodInterceptor {

    /**
     * Enhancer
     */
    private final Enhancer enhancer = new Enhancer();

    @Override
    protected Object create(T originalObject) {
        if (AopUtils.isJdkDynamicProxy(originalObject)) {
            throw new UnsupportedOperationException(StrUtil.format("被代理类[{}]为Jdk代理类，无法使用Cglib代理", originalObject.getClass()));
        }
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        Class<?> objectClass = originalObject.getClass();
        if (AopUtils.isCglibProxy(originalObject)) {
            enhancer.setSuperclass(objectClass.getSuperclass());
        } else {
            enhancer.setSuperclass(objectClass);
        }
        enhancer.setCallback(this);
        Constructor<?>[] constructors = objectClass.getConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                return enhancer.create();
            }
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            Object[] constructorArgs = Arrays.stream(parameterTypes)
                .map(applicationContext::getBean)
                .toArray();
            if (constructorArgs.length == parameterTypes.length) {
                return enhancer.create(parameterTypes, constructorArgs);
            }
        }
        throw new RuntimeException(StrUtil.format("无法代理类：{}", objectClass.getName()));
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        return this.methodInvoke(method, args);
    }
}
