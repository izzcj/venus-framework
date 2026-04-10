package com.ale.venus.common.porxy.cglib;

import com.ale.venus.common.porxy.ProxyFactory;
import com.ale.venus.common.porxy.invoker.ProxyMethodInvoker;

/**
 * Cglib代理工厂
 *
 * @author Ale
 * @version 1.0.0
 */
public class CglibProxyFactory implements ProxyFactory {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object createProxy(Object originalObject, ProxyMethodInvoker proxyMethodInvoker) {
        return new CglibProxy().createProxy(originalObject, proxyMethodInvoker);
    }
}
