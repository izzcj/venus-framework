package com.ale.venus.workflow.dao;

import com.ale.venus.workflow.entity.FlowTaskActor;

import java.util.Collection;
import java.util.List;

/**
 * 流程任务参与者数据访问层
 *
 * @author Ale
 * @version 1.0.0
 */
public interface FlowTaskActorDao {

    /**
     * 根据流程实例ID查询流程任务参与者
     *
     * @param instanceId 流程实例ID
     * @return 流程任务参与者列表
     */
    List<FlowTaskActor> selectByInstanceId(String instanceId);

    /**
     * 根据流程执行ID查询流程任务参与者
     *
     * @param executionId 流程执行ID
     * @return 流程任务参与者集合
     */
    List<FlowTaskActor> selectByExecutionId(String executionId);

    /**
     * 根据任务ID查询流程任务参与者
     *
     * @param taskId 任务ID
     * @return 流程任务参与者集合
     */
    List<FlowTaskActor> selectByTaskId(String taskId);

    /**
     * 根据参与者ID查询流程任务参与者
     *
     * @param actorId         参与者ID
     * @param includeAssignee 是否包含代理参与
     * @return 流程任务参与者集合
     */
    List<FlowTaskActor> selectByActorId(String actorId, boolean includeAssignee);

    /**
     * 根据任务ID和参与者ID查询流程任务参与者
     *
     * @param taskId  任务ID
     * @param actorId 参与者ID
     * @return 流程任务参与者
     */
    FlowTaskActor selectByTaskIdAndActorId(String taskId, String actorId);

    /**
     * 插入流程任务参与者
     *
     * @param flowTaskActor 流程任务参与者
     * @return 插入结果
     */
    boolean insert(FlowTaskActor flowTaskActor);

    /**
     * 插入流程任务参与者
     *
     * @param flowTaskActors 流程任务参与者集合
     * @return 批量插入结果
     */
    boolean batchInsert(Collection<FlowTaskActor> flowTaskActors);

    /**
     * 批量更新流程任务参与者
     *
     * @param flowTaskActors 批量更新流程任务参与者集合
     * @return 批量更新结果
     */
    boolean batchUpdate(Collection<FlowTaskActor> flowTaskActors);

    /**
     * 删除流程任务参与者
     *
     * @param id 流程任务参与者ID
     * @return 删除结果
     */
    boolean deleteById(String id);
    /**
     * 根据流程执行ID删除流程任务参与者
     *
     * @param executionId 流程执行ID
     * @return 删除结果
     */
    boolean deleteByExecutionId(String executionId);

    /**
     * 根据任务ID删除流程任务参与者
     *
     * @param taskId 任务ID
     * @return 删除结果
     */
    boolean deleteByTaskId(String taskId);

    /**
     * 根据任务ID批量删除流程任务参与者
     *
     * @param taskIds 任务ID集合
     * @return 删除结果
     */
    boolean deleteByTaskIds(Collection<String> taskIds);
}
