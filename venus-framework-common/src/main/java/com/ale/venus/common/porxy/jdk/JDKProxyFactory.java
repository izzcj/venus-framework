package com.ale.venus.common.porxy.jdk;

import com.ale.venus.common.porxy.ProxyFactory;
import com.ale.venus.common.porxy.invoker.ProxyMethodInvoker;

/**
 * JDK代理工厂
 *
 * @author Ale
 * @version 1.0.0
 */
public class JDKProxyFactory implements ProxyFactory {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Object createProxy(Object originalObject, ProxyMethodInvoker proxyMethodInvoker) {
        return new JDKProxy().createProxy(originalObject, proxyMethodInvoker);
    }
}
