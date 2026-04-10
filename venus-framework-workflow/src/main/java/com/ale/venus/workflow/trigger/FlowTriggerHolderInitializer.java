package com.ale.venus.workflow.trigger;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * 流程触发器持有器初始化器
 *
 * @author Ale
 * @version 1.0.0
 */
public class FlowTriggerHolderInitializer implements InitializingBean {

    /**
     * 流程触发器提供器
     */
    private final ObjectProvider<FlowTrigger> flowTriggerProvider;

    public FlowTriggerHolderInitializer(ObjectProvider<FlowTrigger> flowTriggerProvider) {
        this.flowTriggerProvider = flowTriggerProvider;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            MethodHandles.privateLookupIn(FlowTriggerHolder.class, MethodHandles.lookup())
                .findStatic(FlowTriggerHolder.class, "init", MethodType.methodType(void.class, ObjectProvider.class))
                .invoke(flowTriggerProvider);
        } catch (Throwable e) {
            throw new RuntimeException("初始化流程触发器持有器失败！");
        }
    }
}
