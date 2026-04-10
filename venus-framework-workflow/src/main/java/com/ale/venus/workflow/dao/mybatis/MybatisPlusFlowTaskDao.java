package com.ale.venus.workflow.dao.mybatis;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.workflow.dao.FlowTaskDao;
import com.ale.venus.workflow.dao.mybatis.mapper.FlowTaskMapper;
import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.enumeration.FlowTaskState;
import com.ale.venus.workflow.query.ActiveTaskQuery;
import com.ale.venus.workflow.query.mybatis.MybatisPlusActiveTaskQuery;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.Collection;
import java.util.List;

/**
 * 基于MybatisPlus的FlowTaskDao实现
 *
 * @author Ale
 * @version 1.0.0
 */
public class MybatisPlusFlowTaskDao implements FlowTaskDao {

    /**
     * 流程任务mapper
     */
    private final FlowTaskMapper taskMapper;

    public MybatisPlusFlowTaskDao(FlowTaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public ActiveTaskQuery createActiveTaskQuery() {
        return new MybatisPlusActiveTaskQuery(this.taskMapper);
    }

    @Override
    public FlowTask selectById(String id) {
        return this.taskMapper.selectById(id);
    }

    @Override
    public List<FlowTask> selectByExecutionId(String executionId) {
        return this.taskMapper.selectList(
            Wrappers.<FlowTask>lambdaQuery()
                .eq(FlowTask::getExecutionId, executionId)
        );
    }

    @Override
    public List<FlowTask> selectByExecutionIdAndState(String executionId, FlowTaskState state) {
        LambdaQueryWrapper<FlowTask> queryWrapper = Wrappers.<FlowTask>lambdaQuery()
            .eq(FlowTask::getExecutionId, executionId);
        if (state != null) {
            queryWrapper.eq(FlowTask::getState, state.getValue());
        }
        return this.taskMapper.selectList(queryWrapper);
    }

    @Override
    public List<FlowTask> selectByAssigneeId(String assigneeId, boolean includeAgent) {
        return this.taskMapper.selectList(
            Wrappers.<FlowTask>lambdaQuery()
                .eq(FlowTask::getAssigneeId, assigneeId)
                .or(includeAgent)
                .eq(FlowTask::getOwnerId, assigneeId)
        );
    }

    @Override
    public List<FlowTask> selectByInstanceId(String instanceId) {
        return this.taskMapper.selectList(
            Wrappers.<FlowTask>lambdaQuery()
                .eq(FlowTask::getInstanceId, instanceId)
        );
    }

    @Override
    public boolean insert(FlowTask flowTask) {
        return this.taskMapper.insert(flowTask) > 0;
    }

    @Override
    public boolean updateById(FlowTask flowTask) {
        return this.taskMapper.updateById(flowTask) > 0;
    }

    @Override
    public boolean batchUpdate(Collection<FlowTask> flowTasks) {
        return CollectionUtil.isNotEmpty(this.taskMapper.updateById(flowTasks));
    }

    @Override
    public int countByExecutionId(String executionId) {
        return this.taskMapper.selectCount(
            Wrappers.<FlowTask>lambdaQuery()
                .eq(FlowTask::getExecutionId, executionId)
        ).intValue();
    }

    @Override
    public boolean deleteById(String id) {
        return this.taskMapper.deleteById(id) > 0;
    }

    @Override
    public boolean deleteByIds(Collection<String> ids) {
        return this.taskMapper.deleteByIds(ids) > 0;
    }

    /**
     * 删除指定执行实例的任务
     *
     * @param executionId 执行实例id
     * @return 删除结果
     */
    @Override
    public boolean deleteByExecutionId(String executionId) {
        return this.taskMapper.delete(
            Wrappers.<FlowTask>lambdaQuery()
                .eq(FlowTask::getExecutionId, executionId)
        ) > 0;
    }
}
