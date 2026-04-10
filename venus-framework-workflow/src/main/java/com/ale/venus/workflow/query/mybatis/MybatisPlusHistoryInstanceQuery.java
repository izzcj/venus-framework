package com.ale.venus.workflow.query.mybatis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.workflow.dao.mybatis.mapper.FlowHistoryInstanceMapper;
import com.ale.venus.workflow.entity.FlowEntity;
import com.ale.venus.workflow.entity.FlowHistoryInstance;
import com.ale.venus.workflow.entity.FlowInstance;
import com.ale.venus.workflow.enumeration.FlowInstanceState;
import com.ale.venus.workflow.model.node.CarbonCopyNode;
import com.ale.venus.workflow.query.HistoryInstanceQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 基于MybatisPlus的流程历史实例查询构建器
 *
 * @author Ale
 * @version 1.0.0
 */
public class MybatisPlusHistoryInstanceQuery extends AbstractMybatisPlusBaseQuery<FlowHistoryInstance, HistoryInstanceQuery> implements HistoryInstanceQuery {

    /**
     * 流程历史实例Mapper
     */
    private final FlowHistoryInstanceMapper historyInstanceMapper;


    public MybatisPlusHistoryInstanceQuery(FlowHistoryInstanceMapper historyInstanceMapper) {
        this.historyInstanceMapper = historyInstanceMapper;
    }

    /**
     * 流程发起人ID
     */
    private String starterId;

    /**
     * 受理人ID
     */
    private String assigneeId;

    /**
     * 是否为抄送
     */
    private boolean isCarbonCopy;

    /**
     * 创建时间大于等于
     */
    private LocalDateTime createdAtGe;

    /**
     * 创建时间小于等于
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
     * 业务ID集合
     */
    private Collection<String> businessIds;

    /**
     * 流程实例状态
     */
    private FlowInstanceState state;

    /**
     * 标题模糊查询
     */
    private String titleLike;

    @Override
    public HistoryInstanceQuery starterId(String starterId) {
        this.starterId = starterId;
        return this;
    }

    @Override
    public HistoryInstanceQuery assigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
        return this;
    }

    @Override
    public HistoryInstanceQuery isCarbonCopy() {
        this.isCarbonCopy = true;
        return this;
    }

    @Override
    public HistoryInstanceQuery createdAtGe(LocalDateTime createdAt) {
        this.createdAtGe = createdAt;
        return this;
    }

    @Override
    public HistoryInstanceQuery createdAtLe(LocalDateTime createdAt) {
        this.createdAtLe = createdAt;
        return this;
    }

    @Override
    public HistoryInstanceQuery businessType(String businessType) {
        this.businessType = businessType;
        return this;
    }

    @Override
    public HistoryInstanceQuery businessId(String businessId) {
        this.businessId = businessId;
        return this;
    }

    @Override
    public HistoryInstanceQuery businessIds(Collection<String> businessIds) {
        this.businessIds = businessIds;
        return this;
    }

    @Override
    public HistoryInstanceQuery state(FlowInstanceState state) {
        this.state = state;
        return this;
    }

    @Override
    public HistoryInstanceQuery titleLike(String title) {
        this.titleLike = title;
        return this;
    }

    /**
     * 构建wrapper
     */
    @Override
    protected void executeBuildWrapper(QueryWrapper<FlowHistoryInstance> queryWrapper) {
        super.executeBuildWrapper(queryWrapper);
        queryWrapper.eq(StrUtil.toUnderlineCase(FlowHistoryInstance.Fields.deleted), false)
            .eq(StrUtil.isNotBlank(this.starterId), StrUtil.toUnderlineCase(FlowEntity.Fields.createdBy), this.starterId)
            .ge(this.createdAtGe != null, StrUtil.toUnderlineCase(FlowEntity.Fields.createdAt), this.createdAtGe)
            .le(this.createdAtLe != null, StrUtil.toUnderlineCase(FlowEntity.Fields.createdAt), this.createdAtLe)
            .eq(StrUtil.isNotBlank(this.businessType), StrUtil.toUnderlineCase(FlowInstance.Fields.businessType), this.businessType)
            .eq(StrUtil.isNotBlank(this.businessId), StrUtil.toUnderlineCase(FlowInstance.Fields.businessId), this.businessId)
            .in(CollectionUtil.isNotEmpty(this.businessIds), StrUtil.toUnderlineCase(FlowInstance.Fields.businessId), this.businessIds)
            .like(StrUtil.isNotBlank(this.titleLike), StrUtil.toUnderlineCase(FlowInstance.Fields.title), this.titleLike);
        if (this.state != null) {
            queryWrapper.eq(StrUtil.toUnderlineCase(FlowHistoryInstance.Fields.state), this.state.getValue());
        }
        if (StrUtil.isNotBlank(this.assigneeId)) {
            if (this.isCarbonCopy) {
                queryWrapper.exists(
                    "select 1 from flow_history_task ht where ht.instance_id = flow_history_instance.id and ht.assignee_id = {0} and ht.type = {1}",
                    this.assigneeId,
                    CarbonCopyNode.NODE_TYPE
                );
            } else {
                queryWrapper.exists(
                    "select 1 from flow_history_task ht where ht.instance_id = flow_history_instance.id and ht.assignee_id = {0}",
                    this.assigneeId
                );
            }
        }
    }

    @Override
    protected BaseMapper<FlowHistoryInstance> provideMapper() {
        return this.historyInstanceMapper;
    }
}
