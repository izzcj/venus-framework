package com.ale.venus.workflow.model;

import com.ale.venus.workflow.parser.TaskAssigneeParser;
import org.springframework.beans.factory.SmartInitializingSingleton;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * 任务受理人支持初始化器
 *
 * @author Ale
 * @version 1.0.0
 */
public class TaskAssigneeSupportInitializer implements SmartInitializingSingleton {

    /**
     * 任务受理人解析器
     */
    private final TaskAssigneeParser taskAssigneeParser;

    public TaskAssigneeSupportInitializer(TaskAssigneeParser taskAssigneeParser) {
        this.taskAssigneeParser = taskAssigneeParser;
    }

    @Override
    public void afterSingletonsInstantiated() {
        try {
            MethodHandles.privateLookupIn(TaskAssigneeSupport.class, MethodHandles.lookup())
                .findStatic(TaskAssigneeSupport.class, "setAssigneeParser", MethodType.methodType(void.class, TaskAssigneeParser.class))
                .invoke(this.taskAssigneeParser);
        } catch (Throwable e) {
            throw new RuntimeException("初始化任务受理人支持失败！");
        }
    }
}
