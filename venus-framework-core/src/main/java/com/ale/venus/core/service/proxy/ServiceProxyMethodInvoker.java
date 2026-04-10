package com.ale.venus.core.service.proxy;

import com.ale.venus.common.porxy.invoker.ProxyMethodInvoker;
import com.ale.venus.core.service.ICrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Method;

/**
 * Service代理方法回调器
 * 预留用，目前没有用到
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings({"rawtypes"})
public class ServiceProxyMethodInvoker implements ProxyMethodInvoker<ICrudService> {

    @Override
    public void before(Object proxy, ICrudService target, Method method, Object[] args) {
    }

    @Override
    public void after(Object proxy, ICrudService target, Method method, Object[] args, Object returnValue) {
    }

    @Override
    public void afterException(Object proxy, ICrudService target, Method method, Object[] args, Throwable throwable) {
    }
}
