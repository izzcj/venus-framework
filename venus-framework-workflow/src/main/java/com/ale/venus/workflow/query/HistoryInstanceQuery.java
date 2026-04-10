package com.ale.venus.workflow.query;

import com.ale.venus.workflow.entity.FlowHistoryInstance;
import com.ale.venus.workflow.enumeration.FlowInstanceState;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * 历史流程实例查询构建器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface HistoryInstanceQuery extends BaseQuery<FlowHistoryInstance, HistoryInstanceQuery> {

    /**
     * 发起人
     *
     * @param starterId 发起人
     * @return this
     */
    HistoryInstanceQuery starterId(String starterId);

    /**
     * 受理人
     *
     * @param assigneeId 受理人
     * @return this
     */
    HistoryInstanceQuery assigneeId(String assigneeId);

    /**
     * 是否为抄送
     * 需要同时设置assigneeId条件才生效
     *
     * @return this
     */
    HistoryInstanceQuery isCarbonCopy();

    /**
     * 创建时间大于等于
     *
     * @param createdAt 创建时间
     * @return this
     */
    HistoryInstanceQuery createdAtGe(LocalDateTime createdAt);

    /**
     * 创建时间小于等于
     *
     * @param createdAt 创建时间
     * @return this
     */
    HistoryInstanceQuery createdAtLe(LocalDateTime createdAt);

    /**
     * 业务类型
     *
     * @param businessType 业务类型
     * @return this
     */
    HistoryInstanceQuery businessType(String businessType);

    /**
     * 业务ID
     *
     * @param businessId 业务ID
     * @return this
     */
    HistoryInstanceQuery businessId(String businessId);

    /**
     * 业务ID集合
     *
     * @param businessIds 业务ID集合
     * @return this
     */
    HistoryInstanceQuery businessIds(Collection<String> businessIds);

    /**
     * 流程实例状态
     *
     * @param state 状态
     * @return this
     */
    HistoryInstanceQuery state(FlowInstanceState state);

    /**
     * 标题模糊插叙
     *
     * @param title 标题
     * @return this
     */
    HistoryInstanceQuery titleLike(String title);

}
