package com.ale.venus.workflow.service;

import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.entity.FlowTaskActor;
import com.ale.venus.workflow.enumeration.FlowInstanceState;
import com.ale.venus.workflow.model.AssigneeConfig;
import com.ale.venus.workflow.support.FinishTaskParam;

import java.util.List;
import java.util.Map;

/**
 * 流程任务服务
 *
 * @author Ale
 * @version 1.0.0
 */
public interface TaskService {

    /**
     * 添加任务
     *
     * @param flowTasks  任务
     * @param taskActors 任务参与人
     * @param isActive   是否激活
     * @return 结果
     */
    boolean addTask(List<FlowTask> flowTasks, List<FlowTaskActor> taskActors, boolean isActive);

    /**
     * 更新任务
     *
     * @param flowTask 任务
     * @return 更新结果
     */
    boolean updateTask(FlowTask flowTask);

    /**
     * 添加变量
     *
     * @param taskId   任务ID
     * @param variable 变量
     * @return 结果
     */
    boolean addVariable(String taskId, Map<String, Object> variable);

    /**
     * 完成任务
     *
     * @param completeTaskParam 完成任务参数
     *
     * @return 是否成功
     */
    boolean completeTask(FinishTaskParam completeTaskParam);

    /**
     * 强制结束任务
     *
     * @param executionId   流程执行ID
     * @param instanceState 实例状态
     *
     * @return 是否成功
     */
    boolean forceFinishTask(String executionId, FlowInstanceState instanceState);

    /**
     * 挂历任务
     *
     * @param executionId 流程执行ID
     *
     * @return 是否成功
     */
    boolean suspendTask(String executionId);

    /**
     * 激活任务
     *
     * @param executionId 流程执行ID
     *
     * @return 是否成功
     */
    boolean activeTask(String executionId);

    /**
     * 回退任务
     *
     * @param rollbackNodeId     回退节点ID
     * @param jumpCurrentNode    是否跳回当前节点
     * @param rollbackTaskParam  回退任务参数
     *
     * @return 是否成功
     */
    boolean rollbackTask(String rollbackNodeId, Boolean jumpCurrentNode, FinishTaskParam rollbackTaskParam);

    /**
     * 驳回任务
     *
     * @param rejectTaskParam 驳回任务参数
     *
     * @return 是否成功
     */
    boolean rejectTask(FinishTaskParam rejectTaskParam);

    /**
     * 转办任务
     *
     * @param assigneeId        处理人ID
     * @param transferTaskParam 转办任务参数
     *
     * @return 是否成功
     */
    boolean transferTask(String assigneeId, FinishTaskParam transferTaskParam);

    /**
     * 委派任务
     *
     * @param assigneeId        处理人ID
     * @param delegateTaskParam 委派任务参数
     *
     * @return 是否成功
     */
    boolean delegateTask(String assigneeId, FinishTaskParam delegateTaskParam);

    /**
     * 拿回任务
     * 在转办或委派后，若处理人尚未执行任务，允许原处理人拿回任务
     *
     * @param taskId     任务ID
     * @param assigneeId 处理人ID
     *
     * @return 是否成功
     */
    boolean reclaimTask(String taskId, String assigneeId);

    /**
     * 撤销任务
     * 指当处理人处理任务后，后续任务尚未处理时可以撤回任务
     *
     * @param taskId 任务ID
     *
     * @return 是否成功
     */
    boolean revokeTask(String taskId);

    /**
     * 添加处理人（加签）
     *
     * @param taskId         任务ID
     * @param assigneeConfig 处理人配置
     *
     * @return 是否成功
     */
    boolean addAssignee(String taskId, List<AssigneeConfig> assigneeConfig);

    /**
     * 移除处理人（减签）
     *
     * @param taskId      任务ID
     * @param assigneeIds 受理人ID集合
     *
     * @return 是否成功
     */
    boolean removeAssignee(String taskId, List<String> assigneeIds);

    /**
     * 交接所有任务
     *
     * @param originAssigneeId 原处理人ID
     * @param targetAssigneeId 目标处理人ID
     *
     * @return 是否成功
     */
    boolean handoverAllTask(String originAssigneeId, String targetAssigneeId);

    /**
     * 是否为历史处理人，指受理人是否参与过本次流程
     *
     * @param instanceId 流程实例ID
     * @param assigneeId 处理人ID
     *
     * @return 是否为历史处理人
     */
    boolean isHistoryAssignee(String instanceId, String assigneeId);
}
