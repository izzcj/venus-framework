package com.ale.venus.workflow.query;

import com.ale.venus.workflow.entity.FlowEntity;

/**
 * 可排序的查询
 *
 * @param <T> 查询结果类型
 * @author Ale
 * @version 1.0.0
 */
public interface SortableQuery<T extends FlowEntity> extends Query<T> {

    /**
     * 排序
     *
     * @param sortField 排序字段
     * @param isDesc    是否降序
     * @return this
     */
    SortableQuery<T> orderBy(String sortField, boolean isDesc);

    /**
     * 升序排序
     *
     * @param sortField  排序字段
     * @param sortFields 排序字段
     * @return this
     */
    SortableQuery<T> orderByAsc(String sortField, String... sortFields);

    /**
     * 降序排序
     *
     * @param sortField  排序字段
     * @param sortFields 排序字段
     * @return this
     */
    SortableQuery<T> orderByDesc(String sortField, String... sortFields);
}
