package com.ale.venus.workflow.service;

import com.ale.venus.common.support.Option;
import com.ale.venus.workflow.entity.*;
import com.ale.venus.workflow.model.node.FlowNode;
import com.ale.venus.workflow.query.*;
import com.ale.venus.workflow.support.InstanceExecutionInfo;

import java.util.List;

/**
 * 查询服务
 *
 * @author Ale
 * @version 1.0.0
 */
public interface QueryService {

    /**
     * 创建流程定义查询构建器
     *
     * @return 流程定义查询构建器
     */
    DefinitionQuery createDefinitionQuery();

    /**
     * 获取流程定义
     *
     * @param id 流程定义ID
     * @return 流程定义
     */
    FlowDefinition fetchDefinitionById(String id);

    /**
     * 获取流程定义
     *
     * @param key      流程定义Key
     * @param tenantId 租户ID
     * @return 流程定义
     */
    FlowDefinition fetchDefinitionByKey(String key, String tenantId);

    /**
     * 获取流程定义列表
     * type为null则查询所有类型
     *
     * @param type     流程定义类型
     * @param tenantId 租户ID
     * @return 流程定义列表
     */
    List<FlowDefinition> fetchDefinitionByType(String type, String tenantId);

    /**
     * 创建流程实例查询构建器
     *
     * @return 流程实例查询构建器
     */
    ActiveInstanceQuery createActiveInstanceQuery();

    /**
     * 获取流程实例
     *
     * @param id 流程实例ID
     * @return 流程实例
     */
    FlowInstance fetchInstanceById(String id);

    /**
     * 通过业务ID和业务类型获取流程实例
     *
     * @param businessId   业务ID
     * @param businessType 业务类型
     * @return 流程实例
     */
    FlowInstance fetchInstanceByBusinessIdAndType(String businessId, String businessType);

    /**
     * 创建流程实例查询
     *
     * @return 流程实例查询
     */
    HistoryInstanceQuery createHistoryInstanceQuery();

    /**
     * 通过业务类型获取历史流程实例列表
     *
     * @param businessType 业务类型
     * @return 流程实例列表
     */
    List<FlowHistoryInstance> fetchHistoryInstanceByBusinessType(String businessType);

    /**
     * 通过业务id获取历史流程实例列表
     *
     * @param businessId 业务ID
     * @return 流程实例列表
     */
    List<FlowHistoryInstance> fetchHistoryInstanceByBusinessId(String businessId);

    /**
     * 创建流程任务查询构建器
     *
     * @return 流程任务查询构建器
     */
    ActiveTaskQuery createActiveTaskQuery();

    /**
     * 获取流程任务
     *
     * @param id 流程任务ID
     * @return 流程任务
     */
    FlowTask fetchTaskById(String id);

    /**
     * 创建历史任务查询构建器
     *
     * @return 历史任务查询构建器
     */
    HistoryTaskQuery createHistoryTaskQuery();

    /**
     * 获取历史任务
     *
     * @param id 历史任务ID
     * @return 历史任务
     */
    FlowHistoryTask fetchHistoryTaskById(String id);

    /**
     * 获取流程节点
     * 附带执行轨迹
     *
     * @param instanceId 流程实例ID
     * @return 流程节点
     */
    FlowNode fetchNodeByInstanceId(String instanceId);

    /**
     * 获取流程实例执行信息
     *
     * @param instanceId 流程实例ID
     * @return 流程实例执行信息
     */
    InstanceExecutionInfo fetchInstanceExecutionInfo(String instanceId);

    /**
     * 获取可回滚节点选项
     * 可回退节点为当前节点同级节点或顶级节点
     *
     * @param taskId 流程任务ID
     * @return 可回滚节点选项
     */
    List<Option> fetchRollbackNodeOption(String taskId);
}
