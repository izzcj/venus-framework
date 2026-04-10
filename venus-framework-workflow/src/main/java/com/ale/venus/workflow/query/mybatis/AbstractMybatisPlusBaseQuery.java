package com.ale.venus.workflow.query.mybatis;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.workflow.entity.FlowEntity;
import com.ale.venus.workflow.query.BaseQuery;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;

/**
 * 基于MybatisPlus的抽象基础查询构建器
 *
 * @param <Q> 查询条件构建器类型
 * @param <T> 查询结果类型
 * @author Ale
 * @version 1.0.0
 */
public abstract class AbstractMybatisPlusBaseQuery<T extends FlowEntity, Q extends BaseQuery<T, Q>> extends AbstractMybatisPlusSortableQuery<T> implements BaseQuery<T, Q> {

    /**
     * 自引用
     */
    private final Q self = CastUtils.cast(this);

    /**
     * id
     */
    protected String id;

    /**
     * id集合
     */
    protected Collection<String> ids;

    /**
     * 机构ID
     */
    protected String tenantId;

    /**
     * exists查询映射
     */
    protected Map<String, Object[]> existsSqlMapping;

    @Override
    public Q id(String id) {
        this.id = id;
        return this.self;
    }

    @Override
    public Q ids(Collection<String> ids) {
        this.ids = ids;
        return this.self;
    }

    @Override
    public Q tenantId(String tenantId) {
        this.tenantId = tenantId;
        return this.self;
    }

    @Override
    public Q exists(String sql, Object... params) {
        if (this.existsSqlMapping == null) {
            this.existsSqlMapping = Maps.newHashMap();
        }
        this.existsSqlMapping.put(sql, params);
        return this.self;
    }

    @Override
    protected void executeBuildWrapper(QueryWrapper<T> queryWrapper) {
        queryWrapper.eq(StrUtil.isNotBlank(this.id), StrUtil.toUnderlineCase(FlowEntity.Fields.id), this.id)
            .in(CollectionUtil.isNotEmpty(this.ids), StrUtil.toUnderlineCase(FlowEntity.Fields.id), this.ids)
            .eq(StrUtil.isNotBlank(this.tenantId), StrUtil.toUnderlineCase(FlowEntity.Fields.tenantId), this.tenantId);
        if (CollectionUtil.isNotEmpty(this.existsSqlMapping)) {
            this.existsSqlMapping.forEach((sql, params) -> {
                if (params != null && params.length > 0) {
                    queryWrapper.exists(sql, params);
                    return;
                }
                queryWrapper.exists(sql);
            });
        }
    }
}
