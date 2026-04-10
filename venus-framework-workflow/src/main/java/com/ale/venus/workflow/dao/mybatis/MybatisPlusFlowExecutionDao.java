package com.ale.venus.workflow.dao.mybatis;

import com.ale.venus.workflow.dao.FlowExecutionDao;
import com.ale.venus.workflow.dao.mybatis.mapper.FlowExecutionMapper;
import com.ale.venus.workflow.entity.FlowExecution;
import com.ale.venus.workflow.enumeration.FlowExecutionState;
import com.ale.venus.workflow.enumeration.FlowInstanceState;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * 基于MybatisPlus的FlowExecutionDao实现
 *
 * @author Ale
 * @version 1.0.0
 */
public class MybatisPlusFlowExecutionDao implements FlowExecutionDao {

    /**
     * 流程执行记录mapper
     */
    private final FlowExecutionMapper executionMapper;

    public MybatisPlusFlowExecutionDao(FlowExecutionMapper executionMapper) {
        this.executionMapper = executionMapper;
    }

    @Override
    public FlowExecution selectById(String id) {
        return this.executionMapper.selectById(id);
    }

    @Override
    public List<FlowExecution> selectUncompletedByParentId(String parentId, String instanceId) {
        return this.executionMapper.selectList(
            Wrappers.<FlowExecution>lambdaQuery()
                .eq(FlowExecution::getDeleted, false)
                .eq(FlowExecution::getState, FlowInstanceState.ACTIVE.getValue())
                .eq(FlowExecution::getInstanceId, instanceId)
                .eq(FlowExecution::getParentId, parentId)
                .orderByAsc(FlowExecution::getCreatedAt)
        );
    }

    @Override
    public List<FlowExecution> selectByInstanceIdAndState(String instanceId, FlowExecutionState... state) {
        LambdaQueryWrapper<FlowExecution> queryWrapper = Wrappers.<FlowExecution>lambdaQuery()
            .eq(FlowExecution::getDeleted, false)
            .eq(FlowExecution::getInstanceId, instanceId)
            .orderByAsc(FlowExecution::getCreatedAt);
        if (state != null && state.length > 0) {
            List<FlowExecutionState> states = Lists.newArrayList(state);
            queryWrapper.in(FlowExecution::getState, states);
        }
        return this.executionMapper.selectList(queryWrapper);
    }

    @Override
    public boolean insert(FlowExecution flowExecution) {
        return this.executionMapper.insert(flowExecution) > 0;
    }

    @Override
    public boolean updateById(FlowExecution flowExecution) {
        return this.executionMapper.updateById(flowExecution) > 0;
    }

    @Override
    public boolean deleteById(String id) {
        return this.executionMapper.update(
            Wrappers.<FlowExecution>lambdaUpdate()
                .set(FlowExecution::getDeleted, true)
                .eq(FlowExecution::getId, id)
        ) > 0;
    }
}
