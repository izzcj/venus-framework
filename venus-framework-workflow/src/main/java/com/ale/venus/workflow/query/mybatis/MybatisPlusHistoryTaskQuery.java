package com.ale.venus.workflow.query.mybatis;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.workflow.dao.mybatis.mapper.FlowHistoryTaskMapper;
import com.ale.venus.workflow.entity.FlowEntity;
import com.ale.venus.workflow.entity.FlowHistoryTask;
import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.enumeration.FlowTaskState;
import com.ale.venus.workflow.query.HistoryTaskQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;

/**
 * 基于MyBatisPlus的历史流程任务查询构建器
 *
 * @author Ale
 * @version 1.0.0
 */
public class MybatisPlusHistoryTaskQuery extends AbstractMybatisPlusBaseQuery<FlowHistoryTask, HistoryTaskQuery> implements HistoryTaskQuery {

    /**
     * 历史流程任务查询构建器
     */
    private final FlowHistoryTaskMapper historyTaskMapper;

    public MybatisPlusHistoryTaskQuery(FlowHistoryTaskMapper historyTaskMapper) {
        this.historyTaskMapper = historyTaskMapper;
    }

    /**
     * 流程实例ID
     */
    private String instanceId;

    /**
     * 流程实例名称
     */
    private String instanceName;

    /**
     * 创建时间大于等于
     */
    private LocalDateTime createdAtGe;

    /**
     * 创建时间小于等于
     */
    private LocalDateTime createdAtLe;

    /**
     * 任务状态
     */
    private FlowTaskState state;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务处理人
     */
    private String assigneeId;

    @Override
    public HistoryTaskQuery instanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    @Override
    public HistoryTaskQuery instanceNameLike(String instanceName) {
        this.instanceName = instanceName;
        return this;
    }

    @Override
    public HistoryTaskQuery createdAtGe(LocalDateTime createdAt) {
        this.createdAtGe = createdAt;
        return this;
    }

    @Override
    public HistoryTaskQuery createdAtLe(LocalDateTime createdAt) {
        this.createdAtLe = createdAt;
        return this;
    }

    @Override
    public HistoryTaskQuery state(FlowTaskState state) {
        this.state = state;
        return this;
    }

    @Override
    public HistoryTaskQuery nameLike(String name) {
        this.name = name;
        return this;
    }

    @Override
    public HistoryTaskQuery assigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
        return this;
    }

    /**
     * 构建wrapper
     */
    @Override
    protected void executeBuildWrapper(QueryWrapper<FlowHistoryTask> queryWrapper) {
        queryWrapper.eq(StrUtil.toUnderlineCase(FlowHistoryTask.Fields.deleted), false)
            .eq(StrUtil.isNotBlank(this.instanceId), StrUtil.toUnderlineCase(FlowTask.Fields.instanceId), this.instanceId)
            .ge(this.createdAtGe != null, StrUtil.toUnderlineCase(FlowEntity.Fields.createdAt), this.createdAtGe)
            .le(this.createdAtLe != null, StrUtil.toUnderlineCase(FlowEntity.Fields.createdAt), this.createdAtLe)
            .eq(StrUtil.isNotBlank(this.name), StrUtil.toUnderlineCase(FlowTask.Fields.name), this.name)
            .eq(StrUtil.isNotBlank(this.assigneeId), StrUtil.toUnderlineCase(FlowTask.Fields.assigneeId), this.assigneeId);
        if (StrUtil.isNotBlank(this.instanceName)) {
            queryWrapper.exists(
                "select 1 from flow_history_instance hi where hi.id = flow_history_task.instance_id and hi.name like '{0}%''",
                this.instanceName
            );
        }
        if (this.state != null) {
            queryWrapper.eq(StrUtil.toUnderlineCase(FlowTask.Fields.state), this.state.getValue());
        }
    }

    @Override
    protected BaseMapper<FlowHistoryTask> provideMapper() {
        return this.historyTaskMapper;
    }
}
