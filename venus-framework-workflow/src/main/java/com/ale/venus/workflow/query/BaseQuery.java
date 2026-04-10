package com.ale.venus.workflow.query;

import com.ale.venus.workflow.entity.FlowEntity;

import java.util.Collection;

/**
 * 查询基类接口
 *
 * @param <Q> 查询条件构建器类型
 * @param <T> 查询结果类型
 * @author Ale
 * @version 1.0.0
 */
public interface BaseQuery<T extends FlowEntity, Q extends BaseQuery<T, Q>> extends SortableQuery<T> {

    /**
     * id查询
     *
     * @param id id
     * @return this
     */
    Q id(String id);

    /**
     * id集合查询
     *
     * @param ids id集合
     * @return this
     */
    Q ids(Collection<String> ids);

    /**
     * 机构id查询
     *
     * @param tenantId 机构id
     * @return this
     */
    Q tenantId(String tenantId);

    /**
     * exists查询
     * 如果需要传参，sql中的参数占位符使用{index}格式
     *
     * @param sql    sql
     * @param params 参数
     * @return this
     */
    Q exists(String sql, Object... params);

}
