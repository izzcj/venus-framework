package com.ale.venus.workflow.hook;

import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.entity.FlowTaskActor;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;

/**
 * 流程任务事件发布器
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class TaskEventPublisher {

    /**
     * 任务监听器集合
     */
    private static final List<TaskListener> TASK_LISTENERS = Lists.newArrayList();

    /**
     * 注册任务监听器
     *
     * @param taskListeners 任务监听器
     */
    static void registerTaskListener(ObjectProvider<TaskListener> taskListeners) {
        for (TaskListener taskListener : taskListeners) {
            TaskEventPublisher.TASK_LISTENERS.add(taskListener);
        }
    }

    /**
     * 发布流程任务事件
     *
     * @param eventType  流程任务事件类型
     * @param flowTask   流程任务
     * @param taskActors 流程任务参与人
     */
    public static void publishTaskEvent(TaskEventType eventType, FlowTask flowTask, List<FlowTaskActor> taskActors) {
        for (TaskListener taskListener : TASK_LISTENERS) {
            if (taskListener.supports(eventType)) {
                boolean result = taskListener.notify(eventType, flowTask, taskActors);
                if (!result) {
                    log.warn("流程任务监听器[{}]通知失败，事件类型：{}", taskListener, eventType);
                }
            }
        }
    }

}
