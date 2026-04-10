package com.ale.venus.workflow.support;

import com.ale.venus.workflow.config.VenusFlowProperties;
import com.ale.venus.workflow.service.*;
import org.springframework.beans.factory.InitializingBean;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * 流程上下文初始化器
 *
 * @author Ale
 * @version 1.0.0
 */
public class FlowContextInitializer implements InitializingBean {

    /**
     * Venus工作流配置
     */
    private final VenusFlowProperties venusFlowProperties;

    /**
     * 流程定义服务
     */
    private final DefinitionService definitionService;

    /**
     * 流程实例服务
     */
    private final InstanceService instanceService;

    /**
     * 流程执行服务
     */
    private final ExecutionService executionService;

    /**
     * 流程任务服务
     */
    private final TaskService taskService;

    /**
     * 查询服务
     */
    private final QueryService queryService;

    public FlowContextInitializer(VenusFlowProperties venusFlowProperties, DefinitionService definitionService, InstanceService instanceService,
                                  ExecutionService executionService, TaskService taskService, QueryService queryService) {
        this.venusFlowProperties = venusFlowProperties;
        this.definitionService = definitionService;
        this.instanceService = instanceService;
        this.executionService = executionService;
        this.taskService = taskService;
        this.queryService = queryService;
    }

    @Override
    public void afterPropertiesSet() {
        try {
            MethodHandles.privateLookupIn(FlowContext.class, MethodHandles.lookup())
                .findStatic(
                    FlowContext.class,
                    "init",
                    MethodType.methodType(void.class, VenusFlowProperties.class, DefinitionService.class, InstanceService.class, ExecutionService.class, TaskService.class, QueryService.class)
                )
                .invoke(this.venusFlowProperties, this.definitionService, this.instanceService, this.executionService, this.taskService, this.queryService);
        } catch (Throwable e) {
            throw new RuntimeException("初始化流程上下文失败！");
        }
    }
}
