package com.ale.venus.workflow.query.mybatis;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.workflow.entity.FlowEntity;
import com.ale.venus.workflow.query.SortableQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 基于MybatisPlus的抽象可排序查询构建器
 *
 * @param <T> 查询结果类型
 * @author Ale
 * @version 1.0.0
 */
public abstract class AbstractMybatisPlusSortableQuery<T extends FlowEntity> extends AbstractMybatisPlusQuery<T> implements SortableQuery<T> {

    /**
     * 排序字段映射
     */
    protected Map<String, Boolean> sortFieldMapping;

    @Override
    public SortableQuery<T> orderBy(String sortField, boolean isDesc) {
        if (this.sortFieldMapping == null) {
            this.sortFieldMapping = Maps.newHashMap();
        }
        this.sortFieldMapping.put(sortField, isDesc);
        return this;
    }

    @Override
    public SortableQuery<T> orderByDesc(String sortField, String... sortFields) {
        if (this.sortFieldMapping == null) {
            this.sortFieldMapping = Maps.newHashMap();
        }
        this.sortFieldMapping.put(sortField, true);
        if (sortFields != null) {
            for (String field : sortFields) {
                this.sortFieldMapping.put(field, true);
            }
        }
        return this;
    }

    @Override
    public SortableQuery<T> orderByAsc(String sortField, String... sortFields) {
        if (this.sortFieldMapping == null) {
            this.sortFieldMapping = Maps.newHashMap();
        }
        this.sortFieldMapping.put(sortField, false);
        if (sortFields != null) {
            for (String field : sortFields) {
                this.sortFieldMapping.put(field, false);
            }
        }
        return this;
    }

    @Override
    protected QueryWrapper<T> buildQueryWrapper() {
        QueryWrapper<T> queryWrapper = Wrappers.query();
        this.executeBuildWrapper(queryWrapper);
        if (CollectionUtil.isNotEmpty(this.sortFieldMapping)) {
            this.sortFieldMapping.forEach((field, isDesc) -> {
                if (isDesc) {
                    queryWrapper.orderByDesc(field);
                } else {
                    queryWrapper.orderByAsc(field);
                }
            });
        }
        return queryWrapper;
    }

    /**
     * 构建查询条件
     *
     * @param queryWrapper 查询条件
     */
    protected abstract void executeBuildWrapper(QueryWrapper<T> queryWrapper);

}
