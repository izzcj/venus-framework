package com.ale.venus.workflow.dao.mybatis;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.workflow.dao.FlowTaskActorDao;
import com.ale.venus.workflow.dao.mybatis.mapper.FlowTaskActorMapper;
import com.ale.venus.workflow.entity.FlowTaskActor;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.Collection;
import java.util.List;

/**
 * 基于MyBatisPlus的FlowTaskActorDao实现
 *
 * @author Ale
 * @version 1.0.0
 */
public class MybatisPlusFlowTaskActorDao implements FlowTaskActorDao {

    /**
     * 流程任务参与人mapper
     */
    private final FlowTaskActorMapper flowTaskActorMapper;

    public MybatisPlusFlowTaskActorDao(FlowTaskActorMapper flowTaskActorMapper) {
        this.flowTaskActorMapper = flowTaskActorMapper;
    }

    @Override
    public List<FlowTaskActor> selectByInstanceId(String instanceId) {
        return this.flowTaskActorMapper.selectList(
            Wrappers.<FlowTaskActor>lambdaQuery()
                .eq(FlowTaskActor::getInstanceId, instanceId)
        );
    }

    @Override
    public List<FlowTaskActor> selectByExecutionId(String executionId) {
        return this.flowTaskActorMapper.selectList(
            Wrappers.<FlowTaskActor>lambdaQuery()
                .eq(FlowTaskActor::getExecutionId, executionId)
        );
    }

    @Override
    public List<FlowTaskActor> selectByTaskId(String taskId) {
        return this.flowTaskActorMapper.selectList(
            Wrappers.<FlowTaskActor>lambdaQuery()
                .eq(FlowTaskActor::getTaskId, taskId)
        );
    }

    @Override
    public List<FlowTaskActor> selectByActorId(String actorId, boolean includeAssignee) {
        return this.flowTaskActorMapper.selectList(
            Wrappers.<FlowTaskActor>lambdaQuery()
                .eq(FlowTaskActor::getActorId, actorId)
                .or(includeAssignee)
                .eq(FlowTaskActor::getAssigneeId, actorId)
        );
    }

    @Override
    public FlowTaskActor selectByTaskIdAndActorId(String taskId, String actorId) {
        return this.flowTaskActorMapper.selectOne(
            Wrappers.<FlowTaskActor>lambdaQuery()
                .eq(FlowTaskActor::getTaskId, taskId)
                .eq(FlowTaskActor::getActorId, actorId)
        );
    }

    @Override
    public boolean insert(FlowTaskActor flowTaskActor) {
        return this.flowTaskActorMapper.insert(flowTaskActor) > 0;
    }

    @Override
    public boolean batchInsert(Collection<FlowTaskActor> flowTaskActors) {
        return CollectionUtil.isNotEmpty(this.flowTaskActorMapper.insert(flowTaskActors));
    }

    @Override
    public boolean batchUpdate(Collection<FlowTaskActor> flowTaskActors) {
        return CollectionUtil.isNotEmpty(this.flowTaskActorMapper.updateById(flowTaskActors));
    }

    @Override
    public boolean deleteById(String id) {
        return this.flowTaskActorMapper.deleteById(id) > 0;
    }

    @Override
    public boolean deleteByExecutionId(String executionId) {
        return this.flowTaskActorMapper.delete(
            Wrappers.<FlowTaskActor>lambdaQuery()
                .eq(FlowTaskActor::getExecutionId, executionId)
        ) > 0;
    }

    @Override
    public boolean deleteByTaskId(String taskId) {
        return this.flowTaskActorMapper.delete(
            Wrappers.<FlowTaskActor>lambdaQuery()
                .eq(FlowTaskActor::getTaskId, taskId)
        ) > 0;
    }

    @Override
    public boolean deleteByTaskIds(Collection<String> taskIds) {
        return this.flowTaskActorMapper.delete(
            Wrappers.<FlowTaskActor>lambdaQuery()
                .in(FlowTaskActor::getTaskId, taskIds)
        ) > 0;
    }
}
