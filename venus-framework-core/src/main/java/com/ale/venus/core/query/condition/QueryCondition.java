package com.ale.venus.core.query.condition;

import com.ale.venus.core.query.QueryParameter;
import com.ale.venus.core.query.QueryType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 查询策略
 *
 * @author Ale
 * @version 1.0.0
 */
public interface QueryCondition {

    /**
     * 构建查询条件
     *
     * @param fieldName    字段名
     * @param fieldValue   字段值
     * @param queryWrapper 查询条件
     * @param parameters   查询参数
     */
    void build(String fieldName, Object fieldValue, QueryWrapper<?> queryWrapper, QueryParameter[] parameters);

    /**
     * 提供查询类型
     *
     * @return 查询类型
     */
    QueryType provideQueryType();
}
