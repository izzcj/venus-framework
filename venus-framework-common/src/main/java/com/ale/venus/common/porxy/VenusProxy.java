package com.ale.venus.common.porxy;

import com.ale.venus.common.porxy.invoker.ProxyMethodInvoker;

/**
 * Venus代理
 *
 * @param <T> 原始对象类型
 * @author Ale
 * @version 1.0.0
 */
public interface VenusProxy<T> {

    /**
     * 创建代理
     *
     * @param originalObject     原始对象
     * @param proxyMethodInvoker 代理方法回调器
     * @return 代理对象
     */
    Object createProxy(T originalObject, ProxyMethodInvoker<T> proxyMethodInvoker);

}
