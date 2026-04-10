package com.ale.venus.workflow.dao;

import com.ale.venus.workflow.entity.FlowExecution;
import com.ale.venus.workflow.enumeration.FlowExecutionState;

import java.util.List;

/**
 * 流程执行记录dao
 *
 * @author Ale
 * @version 1.0.0
 */
public interface FlowExecutionDao {

    /**
     * 根据id查询流程执行记录
     *
     * @param id id
     * @return 流程执行记录
     */
    FlowExecution selectById(String id);

    /**
     * 查询父级节点未完成的流程执行记录
     *
     * @param parentId   父级id
     * @param instanceId 实例id
     * @return 执行记录列表
     */
    List<FlowExecution> selectUncompletedByParentId(String parentId, String instanceId);

    /**
     * 通过流程实例id查询流程执行记录
     *
     * @param instanceId 实例id
     * @param state      状态
     * @return 执行记录列表
     */
    List<FlowExecution> selectByInstanceIdAndState(String instanceId, FlowExecutionState... state);

    /**
     * 插入流程执行记录
     *
     * @param flowExecution 流程执行记录
     * @return 插入结果
     */
    boolean insert(FlowExecution flowExecution);

    /**
     * 更新流程执行记录
     *
     * @param flowExecution 流程执行记录
     *
     * @return 更新结果
     */
    boolean updateById(FlowExecution flowExecution);

    /**
     * 删除流程执行记录
     *
     * @param id id
     * @return 删除结果
     */
    boolean deleteById(String id);
}
