package com.ale.venus.workflow.query;

import com.ale.venus.workflow.entity.FlowInstance;

import java.time.LocalDateTime;

/**
 * 流程实例查询构建器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface ActiveInstanceQuery extends BaseQuery<FlowInstance, ActiveInstanceQuery> {

    /**
     * 发起人
     *
     * @param starterId 发起人
     * @return this
     */
    ActiveInstanceQuery starterId(String starterId);

    /**
     * 创建时间大于等于
     *
     * @param createdAt 创建时间
     * @return this
     */
    ActiveInstanceQuery createdAtGe(LocalDateTime createdAt);

    /**
     * 创建时间小于等于
     *
     * @param createdAt 创建时间
     * @return this
     */
    ActiveInstanceQuery createdAtLe(LocalDateTime createdAt);

    /**
     * 业务类型
     *
     * @param businessType 业务类型
     * @return this
     */
    ActiveInstanceQuery businessType(String businessType);

    /**
     * 业务ID
     *
     * @param businessId 业务ID
     * @return this
     */
    ActiveInstanceQuery businessId(String businessId);

    /**
     * 标题模糊匹配
     *
     * @param title 标题
     * @return this
     */
    ActiveInstanceQuery titleLike(String title);

}
