package com.ale.venus.common.porxy.invoker;

import java.lang.reflect.Method;

/**
 * 代理方法回调器
 *
 * @param <T> 原始对象类型
 * @author Ale
 * @version 1.0.0
 */
public interface ProxyMethodInvoker<T> {

    /**
     * 代理方法前回调
     *
     * @param proxy  代理对象
     * @param target 原始对象
     * @param method 方法
     * @param args   参数
     */
    void before(Object proxy, T target, Method method, Object[] args);

    /**
     * 代理方法后回调
     * 如果 target.method 抛出异常且 afterException 返回true,则不会执行此操作
     * 如果 afterException 返回false, 则无论target.method是否抛出异常，均会执行此操作
     *
     * @param proxy       代理对象
     * @param target      原始对象
     * @param method      方法
     * @param args        参数
     * @param returnValue 返回值
     */
    void after(Object proxy, T target, Method method, Object[] args, Object returnValue);

    /**
     * 代理方法异常回调
     *
     * @param proxy     代理对象
     * @param target    原始对象
     * @param method    方法
     * @param args      参数
     * @param throwable 异常
     */
    void afterException(Object proxy, T target, Method method, Object[] args, Throwable throwable);

}
