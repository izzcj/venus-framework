package com.ale.venus.workflow.support;

import com.ale.venus.workflow.config.VenusFlowProperties;
import com.ale.venus.workflow.service.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 流程上下文
 * 使用静态服务可能会导致服务间依赖关系混乱、不明确，后续可优化
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FlowContext {

    /**
     * 流程定义服务
     */
    @Getter
    private static DefinitionService definitionService;

    /**
     * 流程实例服务
     */
    @Getter
    private static InstanceService instanceService;

    /**
     * 流程执行服务
     */
    @Getter
    private static ExecutionService executionService;

    /**
     * 流程任务服务
     */
    @Getter
    private static TaskService taskService;

    /**
     * 查询服务
     */
    @Getter
    private static QueryService queryService;

    /**
     * 配置文件
     */
    @Getter
    private static VenusFlowProperties properties;

    /**
     * 初始化
     *
     * @param flowDefinitionService 流程定义服务
     * @param flowInstanceService   流程实例服务
     * @param flowExecutionService  流程执行服务
     * @param flowTaskService       流程任务服务
     * @param flowQueryService      流程查询服务
     * @param venusFlowProperties   流程配置
     */
    static void init(VenusFlowProperties venusFlowProperties, DefinitionService flowDefinitionService, InstanceService flowInstanceService,
                     ExecutionService flowExecutionService, TaskService flowTaskService, QueryService flowQueryService) {
        definitionService = flowDefinitionService;
        instanceService = flowInstanceService;
        executionService = flowExecutionService;
        taskService = flowTaskService;
        queryService = flowQueryService;
        properties = venusFlowProperties;
    }
}
