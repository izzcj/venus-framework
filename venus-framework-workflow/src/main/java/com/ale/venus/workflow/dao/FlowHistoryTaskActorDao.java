package com.ale.venus.workflow.dao;

import com.ale.venus.workflow.entity.FlowHistoryTaskActor;

import java.util.Collection;
import java.util.List;

/**
 * 历史流程任务参与者数据访问层
 *
 * @author Ale
 * @version 1.0.0
 */
public interface FlowHistoryTaskActorDao {

    /**
     * 根据任务ID和参与者ID查询历史流程任务参与者
     *
     * @param taskId  任务ID
     * @param actorId 参与者ID
     * @return 历史流程任务参与者
     */
    FlowHistoryTaskActor selectByTaskIdAndActorId(String taskId, String actorId);

    /**
     * 根据实例ID查询历史流程任务参与者
     *
     * @param instanceId 实例ID
     * @return 历史流程任务参与者列表
     */
    List<FlowHistoryTaskActor> selectByInstanceId(String instanceId);

    /**
     * 根据任务ID查询历史流程任务参与者列表
     *
     * @param taskId 任务ID
     * @return 历史流程任务参与者列表
     */
    List<FlowHistoryTaskActor> selectByTaskId(String taskId);

    /**
     * 插入历史流程任务参与者
     *
     * @param flowHistoryTaskActor 历史流程任务参与者
     * @return 插入结果
     */
    boolean insert(FlowHistoryTaskActor flowHistoryTaskActor);

    /**
     * 批量插入历史流程任务参与者
     *
     * @param flowHistoryTaskActors 历史流程任务参与者集合
     * @return 批量插入结果
     */
    boolean batchInsert(Collection<FlowHistoryTaskActor> flowHistoryTaskActors);

    /**
     * 更新历史流程任务参与者
     *
     * @param flowHistoryTaskActor 历史流程任务参与者
     * @return 更新结果
     */
    boolean updateById(FlowHistoryTaskActor flowHistoryTaskActor);

    /**
     * 删除历史流程任务参与者
     *
     * @param id ID
     * @return 删除结果
     */
    boolean deleteById(String id);
}
