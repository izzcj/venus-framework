package com.ale.venus.workflow.dao;

import com.ale.venus.workflow.entity.FlowHistoryTask;
import com.ale.venus.workflow.enumeration.FlowTaskState;
import com.ale.venus.workflow.query.HistoryTaskQuery;

import java.util.Collection;
import java.util.List;

/**
 * 历史流程任务数据访问层
 *
 * @author Ale
 * @version 1.0.0
 */
public interface FlowHistoryTaskDao {

    /**
     * 创建历史流程任务查询构建器
     *
     * @return 历史流程任务查询构建器
     */
    HistoryTaskQuery createHistoryTaskQuery();

    /**
     * 根据ID查询历史流程任务
     *
     * @param id 历史流程任务ID
     * @return 历史流程任务
     */
    FlowHistoryTask selectById(String id);

    /**
     * 根据实例ID查询历史流程任务
     *
     * @param instanceId 实例ID集合
     * @return 历史流程任务
     */
    List<FlowHistoryTask> selectByInstanceId(String instanceId);

    /**
     * 根据执行ID和状态查询历史流程任务
     * state为null则查询所有状态
     *
     * @param executionId 执行ID
     * @param state       状态
     * @return 历史流程任务
     */
    List<FlowHistoryTask> selectByExecutionIdAndState(String executionId, FlowTaskState state);

    /**
     * 插入历史流程任务
     *
     * @param flowHistoryTask 历史流程任务
     * @return 插入结果
     */
    boolean insert(FlowHistoryTask flowHistoryTask);

    /**
     * 批量插入历史流程任务
     *
     * @param flowHistoryTasks 历史流程任务列表
     * @return 批量插入结果
     */
    boolean batchInsert(Collection<FlowHistoryTask> flowHistoryTasks);

    /**
     * 更新历史流程任务
     *
     * @param flowHistoryTask 历史流程任务
     * @return 是否更新成功
     */
    boolean updateById(FlowHistoryTask flowHistoryTask);

    /**
     * 判断历史流程任务是否存在
     *
     * @param instanceId 流程实例ID
     * @param assigneeId 流程任务处理人ID
     * @return 是否存在
     */
    boolean exists(String instanceId, String assigneeId);

    /**
     * 删除历史流程任务
     *
     * @param id 历史流程任务ID
     * @return 删除结果
     */
    boolean deleteById(String id);
}
