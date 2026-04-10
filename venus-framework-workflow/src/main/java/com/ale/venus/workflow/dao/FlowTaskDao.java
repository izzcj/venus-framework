package com.ale.venus.workflow.dao;

import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.enumeration.FlowTaskState;
import com.ale.venus.workflow.query.ActiveTaskQuery;

import java.util.Collection;
import java.util.List;

/**
 * 流程任务数据访问层
 *
 * @author Ale
 * @version 1.0.0
 */
public interface FlowTaskDao {

    /**
     * 创建流程任务查询构建器
     *
     * @return 流程任务查询构建器
     */
    ActiveTaskQuery createActiveTaskQuery();

    /**
     * 根据流程任务ID查询流程任务
     *
     * @param id 流程任务ID
     * @return 流程任务
     */
    FlowTask selectById(String id);

    /**
     * 根据流程执行ID查询流程任务
     *
     * @param executionId 流程执行ID
     * @return 流程任务集合
     */
    List<FlowTask> selectByExecutionId(String executionId);

    /**
     * 根据流程执行ID查询流程任务
     *
     * @param executionId 流程执行ID
     * @param state       流程任务状态
     * @return 流程任务集合
     */
    List<FlowTask> selectByExecutionIdAndState(String executionId, FlowTaskState state);

    /**
     * 根据任务受理人ID查询流程任务
     *
     * @param assigneeId   任务受理人
     * @param includeAgent 是否包含代理出去的任务
     * @return 流程任务集合
     */
    List<FlowTask> selectByAssigneeId(String assigneeId, boolean includeAgent);

    /**
     * 根据流程实例ID查询流程任务
     *
     * @param instanceId 流程实例ID
     * @return 流程任务集合
     */
    List<FlowTask> selectByInstanceId(String instanceId);

    /**
     * 插入流程任务
     *
     * @param flowTask 流程任务
     * @return 插入结果
     */
    boolean insert(FlowTask flowTask);

    /**
     * 更新流程任务
     *
     * @param flowTask 流程任务
     *
     * @return 更新结果
     */
    boolean updateById(FlowTask flowTask);

    /**
     * 批量更新流程任务
     *
     * @param flowTasks 批量更新流程任务
     * @return 批量更新结果
     */
    boolean batchUpdate(Collection<FlowTask> flowTasks);

    /**
     * 根据流程执行ID统计进行中的流程任务数量
     *
     * @param executionId 流程执行ID
     * @return 流程任务数量
     */
    int countByExecutionId(String executionId);

    /**
     * 删除流程任务
     *
     * @param id 流程任务ID
     * @return 删除结果
     */
    boolean deleteById(String id);

    /**
     * 批量删除流程任务
     *
     * @param ids 批量删除流程任务ID
     * @return 批量删除结果
     */
    boolean deleteByIds(Collection<String> ids);

    /**
     * 根据流程执行ID删除流程任务
     *
     * @param executionId 流程执行ID
     * @return 删除结果
     */
    boolean deleteByExecutionId(String executionId);

}
