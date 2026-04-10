package com.ale.venus.workflow.trigger;

import com.ale.venus.common.support.Comment;
import com.ale.venus.common.support.Option;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 流程触发器持有器
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FlowTriggerHolder {

    /**
     * 流程触发器映射
     */
    private static final Map<String, FlowTrigger> FLOW_TRIGGER_MAPPING = new ConcurrentHashMap<>();

    /**
     * 触发器选项
     */
    private static final List<Option> FLOW_TRIGGER_OPTIONS = Lists.newArrayList();

    /**
     * 初始化
     *
     * @param flowTriggerProvider 触发器提供器
     */
    static void init(ObjectProvider<FlowTrigger> flowTriggerProvider) {
        for (FlowTrigger flowTrigger : flowTriggerProvider) {
            Class<? extends FlowTrigger> triggerClass = flowTrigger.getClass();
            Comment comment = triggerClass.getAnnotation(Comment.class);
            if (comment != null) {
                FLOW_TRIGGER_OPTIONS.add(Option.of(comment.value(), triggerClass.getName()));
            } else {
                FLOW_TRIGGER_OPTIONS.add(Option.of(triggerClass.getSimpleName(), triggerClass.getName()));
            }
            FLOW_TRIGGER_MAPPING.put(triggerClass.getName(), flowTrigger);
        }
    }

    /**
     * 获取流程触发器选项
     *
     * @return 流程触发器选项
     */
    public static List<Option> getFlowTriggerOptions() {
        return FLOW_TRIGGER_OPTIONS;
    }

    /**
     * 获取流程触发器
     *
     * @param triggerClass 流程触发器类名
     * @return 流程触发器
     */
    public static FlowTrigger getFlowTrigger(String triggerClass) {
        return FLOW_TRIGGER_MAPPING.get(triggerClass);
    }
}
