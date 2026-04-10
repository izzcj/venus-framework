package com.ale.venus.workflow.hook;

import com.ale.venus.workflow.entity.FlowInstance;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;


/**
 * 流程实例事件发布器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class InstanceEventPublisher {

    /**
     * 流程实例监听器集合
     */
    private static final List<InstanceListener> INSTANCE_LISTENERS = Lists.newArrayList();

    /**
     * 注册流程实例监听器
     *
     * @param instanceListeners 流程实例监听器
     */
    static void registerInstanceListener(ObjectProvider<InstanceListener> instanceListeners) {
        for (InstanceListener instanceListener : instanceListeners) {
            InstanceEventPublisher.INSTANCE_LISTENERS.add(instanceListener);
        }
    }

    /**
     * 发布流程实例事件
     *
     * @param eventType 流程实例事件类型
     * @param instance  流程实例
     */
    public static void publishInstanceEvent(InstanceEventType eventType, FlowInstance instance) {
        for (InstanceListener instanceListener : INSTANCE_LISTENERS) {
            if (instanceListener.supports(eventType)) {
                boolean result = instanceListener.notify(eventType, instance);
                if (!result) {
                    log.warn("流程实例监听器[{}]通知失败，事件类型：{}", instanceListener, eventType);
                }
            }
        }
    }
}
