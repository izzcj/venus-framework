package com.ale.venus.workflow.service;

import com.ale.venus.workflow.entity.FlowExecution;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.enumeration.FlowInstanceState;
import com.ale.venus.workflow.model.TaskAssignee;

import java.util.Map;

/**
 * 流程执行记录服务
 *
 * @author Ale
 * @version 1.0.0
 */
public interface ExecutionService {

    /**
     * 获取流程执行记录
     *
     * @param executionId 流程执行记录ID
     * @return 流程执行记录
     */
    FlowExecution fetchExecution(String executionId);

    /**
     * 创建流程执行记录
     *
     * @param instance      流程实例
     * @param nodeId        节点ID
     * @param lastExecution 上一个执行记录
     * @param variable 变量
     * @return 流程执行记录
     */
    FlowExecution createExecution(FlowInstance instance, String nodeId, FlowExecution lastExecution, Map<String, Object> variable);

    /**
     * 强制结束流程执行记录
     *
     * @param instanceId    流程实例ID
     * @param instanceState 流程实例状态
     * @return 结果
     */
    boolean forceFinishExecution(String instanceId, FlowInstanceState instanceState);

    /**
     * 挂起流程执行记录
     *
     * @param instanceId 流程实例ID
     * @return 挂起结果
     */
    boolean suspendExecution(String instanceId);

    /**
     * 激活流程执行记录
     *
     * @param instanceId 流程实例ID
     * @return 激活结果
     */
    boolean activeExecution(String instanceId);

    /**
     * 添加流程执行记录变量
     *
     * @param executionId 执行记录ID
     * @param variable    变量
     * @return 添加结果
     */
    boolean addVariable(String executionId, Map<String, Object> variable);

    /**
     * 更新流程执行记录
     *
     * @param execution 执行记录
     * @return 结果
     */
    boolean updateExecution(FlowExecution execution);

    /**
     * 完成流程执行记录
     *
     * @param execution 执行记录
     * @return 结果
     */
    boolean completeExecution(FlowExecution execution);

    /**
     * 完成流程执行记录
     *
     * @param execution     执行记录
     * @param lastExecution 上次执行记录
     * @return 结果
     */
    boolean completeExecution(FlowExecution execution, FlowExecution lastExecution);

    /**
     * 获取下一个任务受理人
     *
     * @param executionId 执行记录ID
     * @return 下一个任务分配者
     */
    TaskAssignee getNextTaskAssignee(String executionId);
}
