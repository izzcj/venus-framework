package com.ale.venus.common.porxy;

import com.ale.venus.common.porxy.invoker.ProxyMethodInvoker;

/**
 * 代理工厂
 *
 * @author Ale
 * @version 1.0.0
 */
public interface ProxyFactory {

    /**
     * 创建代理对象
     *
     * @param originalObject     原始对象
     * @param proxyMethodInvoker 代理方法调用器
     * @return 代理工厂实例
     */
    @SuppressWarnings("rawtypes")
    Object createProxy(Object originalObject, ProxyMethodInvoker proxyMethodInvoker);
}
