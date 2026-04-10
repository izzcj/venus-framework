package com.ale.venus.workflow.dao.mybatis;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.workflow.dao.FlowHistoryTaskActorDao;
import com.ale.venus.workflow.dao.mybatis.mapper.FlowHistoryTaskActorMapper;
import com.ale.venus.workflow.entity.FlowHistoryTaskActor;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;

import java.util.Collection;
import java.util.List;

/**
 * 基于MybatisPlus的FlowHistoryTaskActorDao实现
 *
 * @author Ale
 * @version 1.0.0
 */
public class MybatisPlusFlowHistoryTaskActorDao implements FlowHistoryTaskActorDao {

    /**
     * 历史流程任务参与者mapper
     */
    private final FlowHistoryTaskActorMapper flowHistoryTaskActorMapper;

    public MybatisPlusFlowHistoryTaskActorDao(FlowHistoryTaskActorMapper flowHistoryTaskActorMapper) {
        this.flowHistoryTaskActorMapper = flowHistoryTaskActorMapper;
    }

    @Override
    public FlowHistoryTaskActor selectByTaskIdAndActorId(String taskId, String actorId) {
        return this.flowHistoryTaskActorMapper.selectOne(
            Wrappers.<FlowHistoryTaskActor>lambdaQuery()
                .eq(FlowHistoryTaskActor::getDeleted, false)
                .eq(FlowHistoryTaskActor::getTaskId, taskId)
                .eq(FlowHistoryTaskActor::getActorId, actorId)
        );
    }

    @Override
    public List<FlowHistoryTaskActor> selectByInstanceId(String instanceId) {
        return this.flowHistoryTaskActorMapper.selectList(
            Wrappers.<FlowHistoryTaskActor>lambdaQuery()
                .eq(FlowHistoryTaskActor::getDeleted, false)
                .eq(FlowHistoryTaskActor::getInstanceId, instanceId)
        );
    }

    @Override
    public List<FlowHistoryTaskActor> selectByTaskId(String taskId) {
        return this.flowHistoryTaskActorMapper.selectList(
            Wrappers.<FlowHistoryTaskActor>lambdaQuery()
                .eq(FlowHistoryTaskActor::getDeleted, false)
                .eq(FlowHistoryTaskActor::getTaskId, taskId)
        );
    }

    @Override
    public boolean insert(FlowHistoryTaskActor flowHistoryTaskActor) {
        return this.flowHistoryTaskActorMapper.insert(flowHistoryTaskActor) > 0;
    }

    @Override
    public boolean batchInsert(Collection<FlowHistoryTaskActor> flowHistoryTaskActors) {
        return CollectionUtil.isNotEmpty(this.flowHistoryTaskActorMapper.insert(flowHistoryTaskActors));
    }

    @Override
    public boolean updateById(FlowHistoryTaskActor flowHistoryTaskActor) {
        return this.flowHistoryTaskActorMapper.updateById(flowHistoryTaskActor) > 0;
    }

    @Override
    public boolean deleteById(String id) {
        return this.flowHistoryTaskActorMapper.update(
            Wrappers.<FlowHistoryTaskActor>lambdaUpdate()
                .set(FlowHistoryTaskActor::getDeleted, true)
                .eq(FlowHistoryTaskActor::getId, id)
        ) > 0;
    }
}
