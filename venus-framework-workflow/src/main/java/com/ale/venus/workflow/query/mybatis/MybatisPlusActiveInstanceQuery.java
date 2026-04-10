package com.ale.venus.workflow.query.mybatis;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.workflow.dao.mybatis.mapper.FlowInstanceMapper;
import com.ale.venus.workflow.entity.FlowEntity;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.query.ActiveInstanceQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;

/**
 * 基于MybatisPlus的流程实例查询构建器
 *
 * @author Ale
 * @version 1.0.0
 */
public class MybatisPlusActiveInstanceQuery extends AbstractMybatisPlusBaseQuery<FlowInstance, ActiveInstanceQuery> implements ActiveInstanceQuery {

    /**
     * 流程实例Mapper
     */
    private final FlowInstanceMapper instanceMapper;

    public MybatisPlusActiveInstanceQuery(FlowInstanceMapper instanceMapper) {
        this.instanceMapper = instanceMapper;
    }

    /**
     * 流程发起人ID
     */
    private String starterId;

    /**
     * 创建时间开始
     */
    private LocalDateTime createdAtGe;

    /**
     * 创建时间结束
     */
    private LocalDateTime createdAtLe;

    /**
     * 业务类型
     */
    private String businessType;

    /**
     * 业务ID
     */
    private String businessId;

    /**
     * 标题模糊匹配
     */
    private String titleLike;

    @Override
    public ActiveInstanceQuery starterId(String starterId) {
        this.starterId = starterId;
        return this;
    }

    @Override
    public ActiveInstanceQuery createdAtGe(LocalDateTime createdAt) {
        this.createdAtGe = createdAt;
        return this;
    }

    @Override
    public ActiveInstanceQuery createdAtLe(LocalDateTime createdAt) {
        this.createdAtLe = createdAt;
        return this;
    }

    @Override
    public ActiveInstanceQuery businessType(String businessType) {
        this.businessType = businessType;
        return this;
    }

    @Override
    public ActiveInstanceQuery businessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    @Override
    public ActiveInstanceQuery titleLike(String title) {
        this.titleLike = title;
        return this;
    }

    @Override
    protected void executeBuildWrapper(QueryWrapper<FlowInstance> queryWrapper) {
        super.executeBuildWrapper(queryWrapper);
        queryWrapper.eq(StrUtil.isNotBlank(this.starterId), StrUtil.toUnderlineCase(FlowEntity.Fields.createdBy), this.starterId)
            .ge(this.createdAtGe != null, StrUtil.toUnderlineCase(FlowEntity.Fields.createdAt), this.createdAtGe)
            .le(this.createdAtLe != null, StrUtil.toUnderlineCase(FlowEntity.Fields.createdAt), this.createdAtLe)
            .eq(StrUtil.isNotBlank(this.businessType), StrUtil.toUnderlineCase(FlowInstance.Fields.businessType), this.businessType)
            .eq(StrUtil.isNotBlank(this.businessId), StrUtil.toUnderlineCase(FlowInstance.Fields.businessId), this.businessId)
            .like(StrUtil.isNotBlank(this.titleLike), StrUtil.toUnderlineCase(FlowInstance.Fields.title), this.titleLike);
    }

    @Override
    protected BaseMapper<FlowInstance> provideMapper() {
        return this.instanceMapper;
    }
}
