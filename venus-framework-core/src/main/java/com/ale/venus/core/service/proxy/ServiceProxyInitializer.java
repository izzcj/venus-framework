package com.ale.venus.core.service.proxy;

import com.ale.venus.common.porxy.ProxyFactory;
import com.ale.venus.core.service.ICrudService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Service代理初始化器
 *
 * @author Ale
 * @version 1.0.0
 */
public class ServiceProxyInitializer implements BeanPostProcessor {

    /**
     * 代理工厂
     */
    private final ObjectProvider<ProxyFactory> proxyFactoryObjectProvider;

    public ServiceProxyInitializer(ObjectProvider<ProxyFactory> proxyFactoryObjectProvider) {
        this.proxyFactoryObjectProvider = proxyFactoryObjectProvider;
    }

    @NonNull
    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof ICrudService) {
            return Objects.requireNonNull(this.proxyFactoryObjectProvider.getIfAvailable()).createProxy(bean, new ServiceProxyMethodInvoker());
        }
        return bean;
    }
}
