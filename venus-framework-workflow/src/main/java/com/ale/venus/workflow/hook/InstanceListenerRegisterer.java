package com.ale.venus.workflow.hook;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * 实例监听器注册器
 *
 * @author Ale
 * @version 1.0.0
 */
public class InstanceListenerRegisterer implements InitializingBean {

    /**
     * 实例监听器
     */
    private final ObjectProvider<InstanceListener> instanceListeners;

    public InstanceListenerRegisterer(ObjectProvider<InstanceListener> instanceListeners) {
        this.instanceListeners = instanceListeners;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            MethodHandles.privateLookupIn(InstanceEventPublisher.class, MethodHandles.lookup())
                .findStatic(InstanceEventPublisher.class, "registerInstanceListener", MethodType.methodType(void.class, ObjectProvider.class))
                .invoke(this.instanceListeners);
        } catch (Throwable e) {
            throw new RuntimeException("注册流程实例监听器失败！");
        }
    }
}
