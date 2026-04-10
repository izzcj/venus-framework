package com.ale.venus.workflow.dao.mybatis;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.workflow.dao.FlowHistoryTaskDao;
import com.ale.venus.workflow.dao.mybatis.mapper.FlowHistoryTaskMapper;
import com.ale.venus.workflow.entity.FlowHistoryTask;
import com.ale.venus.workflow.enumeration.FlowTaskState;
import com.ale.venus.workflow.query.HistoryTaskQuery;
import com.ale.venus.workflow.query.mybatis.MybatisPlusHistoryTaskQuery;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.Collection;
import java.util.List;

/**
 * 基于MybatisPlus的FlowHistoryTaskDao实现
 *
 * @author Ale
 * @version 1.0.0
 */
public class MybatisPlusFlowHistoryTaskDao implements FlowHistoryTaskDao {

    /**
     * 历史流程任务mapper
     */
    private final FlowHistoryTaskMapper historyTaskMapper;

    public MybatisPlusFlowHistoryTaskDao(FlowHistoryTaskMapper historyTaskMapper) {
        this.historyTaskMapper = historyTaskMapper;
    }

    @Override
    public HistoryTaskQuery createHistoryTaskQuery() {
        return new MybatisPlusHistoryTaskQuery(this.historyTaskMapper);
    }

    @Override
    public FlowHistoryTask selectById(String id) {
        return this.historyTaskMapper.selectById(id);
    }

    @Override
    public List<FlowHistoryTask> selectByInstanceId(String instanceId) {
        return this.historyTaskMapper.selectList(
            Wrappers.<FlowHistoryTask>lambdaQuery()
                .eq(FlowHistoryTask::getInstanceId, instanceId)
        );
    }

    @Override
    public List<FlowHistoryTask> selectByExecutionIdAndState(String executionId, FlowTaskState state) {
        return this.historyTaskMapper.selectList(
            Wrappers.<FlowHistoryTask>lambdaQuery()
                .eq(FlowHistoryTask::getDeleted, false)
                .eq(FlowHistoryTask::getExecutionId, executionId)
                .eq(FlowHistoryTask::getState, state)
        );
    }

    @Override
    public boolean insert(FlowHistoryTask flowHistoryTask) {
        return this.historyTaskMapper.insert(flowHistoryTask) > 0;
    }

    @Override
    public boolean batchInsert(Collection<FlowHistoryTask> flowHistoryTasks) {
        return CollectionUtil.isNotEmpty(this.historyTaskMapper.insert(flowHistoryTasks));
    }

    @Override
    public boolean updateById(FlowHistoryTask flowHistoryTask) {
        return this.historyTaskMapper.updateById(flowHistoryTask) > 0;
    }

    @Override
    public boolean exists(String instanceId, String assigneeId) {
        return this.historyTaskMapper.exists(
            Wrappers.<FlowHistoryTask>lambdaQuery()
                .eq(FlowHistoryTask::getInstanceId, instanceId)
                .eq(FlowHistoryTask::getAssigneeId, assigneeId)
        );
    }

    @Override
    public boolean deleteById(String id) {
        return this.historyTaskMapper.update(
            Wrappers.<FlowHistoryTask>lambdaUpdate()
                .set(FlowHistoryTask::getDeleted, true)
                .eq(FlowHistoryTask::getId, id)
        ) > 0;
    }
}
