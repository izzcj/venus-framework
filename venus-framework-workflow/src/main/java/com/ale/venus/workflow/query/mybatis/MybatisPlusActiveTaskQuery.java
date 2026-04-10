package com.ale.venus.workflow.query.mybatis;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.workflow.dao.mybatis.mapper.FlowTaskMapper;
import com.ale.venus.workflow.entity.FlowEntity;
import com.ale.venus.workflow.entity.FlowTask;
import com.ale.venus.workflow.enumeration.FlowTaskType;
import com.ale.venus.workflow.query.ActiveTaskQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;

/**
 * 基于MyBatisPlus的流程任务查询构建器
 *
 * @author Ale
 * @version 1.0.0
 */
public class MybatisPlusActiveTaskQuery extends AbstractMybatisPlusBaseQuery<FlowTask, ActiveTaskQuery> implements ActiveTaskQuery {

    /**
     * 映射字段与函数的映射关系
     */
    private final FlowTaskMapper taskMapper;

    public MybatisPlusActiveTaskQuery(FlowTaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    /**
     * 实例id
     */
    private String instanceId;

    /**
     * 实例名称模糊匹配
     */
    private String instanceNameLike;

    /**
     * 创建时间大于等于
     */
    private LocalDateTime createdAtGe;

    /**
     * 创建时间小于等于
     */
    private LocalDateTime createdAtLe;

    /**
     * 任务名称模糊匹配
     */
    private String nameLike;

    /**
     * 任务受理人
     */
    private String assigneeId;

    /**
     * 任务类型
     */
    private FlowTaskType type;

    @Override
    public ActiveTaskQuery instanceId(String instanceId) {
        this.instanceId = instanceId;
        return this;
    }

    @Override
    public ActiveTaskQuery instanceNameLike(String instanceName) {
        this.instanceNameLike = instanceName;
        return this;
    }

    @Override
    public ActiveTaskQuery createdAtGe(LocalDateTime createdAt) {
        this.createdAtGe = createdAt;
        return this;
    }

    @Override
    public ActiveTaskQuery createdAtLe(LocalDateTime createdAt) {
        this.createdAtLe = createdAt;
        return this;
    }

    @Override
    public ActiveTaskQuery nameLike(String name) {
        this.nameLike = name;
        return this;
    }

    @Override
    public ActiveTaskQuery assigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
        return this;
    }

    @Override
    public ActiveTaskQuery type(FlowTaskType type) {
        this.type = type;
        return this;
    }

    @Override
    protected void executeBuildWrapper(QueryWrapper<FlowTask> queryWrapper) {
        super.executeBuildWrapper(queryWrapper);
        queryWrapper.eq(StrUtil.isNotBlank(this.instanceId), StrUtil.toUnderlineCase(FlowTask.Fields.instanceId), this.instanceId)
            .ge(createdAtGe != null, StrUtil.toUnderlineCase(FlowEntity.Fields.createdAt), createdAtGe)
            .le(createdAtLe != null, StrUtil.toUnderlineCase(FlowEntity.Fields.createdAt), createdAtLe)
            .eq(StrUtil.isNotBlank(this.nameLike), StrUtil.toUnderlineCase(FlowTask.Fields.name), this.nameLike)
            .eq(StrUtil.isNotBlank(this.assigneeId), StrUtil.toUnderlineCase(FlowTask.Fields.assigneeId), this.assigneeId)
            .eq(this.type != null, StrUtil.toUnderlineCase(FlowTask.Fields.type), this.type);
        if (StrUtil.isNotBlank(this.instanceNameLike)) {
            queryWrapper.exists(
                "select 1 from flow_instance i where i.id = flow_task.instance_id = i.id and i.name like '{0}%'",
                this.instanceNameLike
            );
        }
    }

    @Override
    protected BaseMapper<FlowTask> provideMapper() {
        return this.taskMapper;
    }
}
