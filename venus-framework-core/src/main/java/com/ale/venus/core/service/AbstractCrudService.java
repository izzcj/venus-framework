package com.ale.venus.core.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import com.ale.venus.common.domain.entity.BaseEntity;
import com.ale.venus.common.exception.ServiceException;
import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.common.utils.GenericTypeUtils;
import com.ale.venus.core.constants.HookConstants;
import com.ale.venus.core.pojo.BaseBO;
import com.ale.venus.core.query.BaseQuery;
import com.ale.venus.core.query.QueryConditionResolver;
import com.ale.venus.core.service.hook.HookContext;
import com.ale.venus.core.service.hook.HookStage;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Sets;
import lombok.Getter;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 抽象CRUD服务
 *
 * @param <Mapper> Mapper
 * @param <E>      实体类型
 * @param <B>      实体BO类型
 * @param <Q>      查询条件类型
 * @author Ale
 * @version 1.0.0
 */
public abstract class AbstractCrudService<Mapper extends BaseMapper<E>, E extends BaseEntity, B extends BaseBO, Q extends BaseQuery> extends AbstractMybatisPlusCrudServiceImpl<Mapper, E> implements ICrudService<E, B, Q> {

    /**
     * 实体类型
     */
    @Getter
    private final Class<E> entityClass = CastUtils.cast(GenericTypeUtils.resolveTypeArguments(this.getClass(), AbstractCrudService.class, 1));

    /**
     * BO类型
     */
    private final Class<B> boClass = CastUtils.cast(GenericTypeUtils.resolveTypeArguments(this.getClass(), AbstractCrudService.class, 2));

    @Override
    public B queryById(Long id) {
        HookContext hookContext = HookContext.newContext();
        E entity;
        try {
            entity = this.getById(id);
            if (entity == null) {
                throw new ServiceException("查询实体失败！实体[{}]不存在！", id);
            }
            this.executeServiceHooks(entity, HookStage.AFTER_QUERY, hookContext);
            this.executeServiceHooks(entity, HookStage.BEFORE_CLEAR_HOOK_CONTEXT, hookContext);
        } finally {
            hookContext.clear();
        }
        return BeanUtil.copyProperties(entity, this.boClass);
    }

    @Override
    public B queryOne(Q query) {
        return this.queryOne(query, HookContext.newContext());
    }

    @Override
    public B queryOne(Q query, HookContext context) {
        LambdaQueryWrapper<E> queryWrapper = QueryConditionResolver.resolveLambda(query);
        HookContext hookContext = Objects.requireNonNullElse(context, this.createHookContext());
        E one;
        try {
            this.executeServiceHooks(queryWrapper, HookStage.BEFORE_QUERY, hookContext);
            if (BooleanUtil.isTrue(hookContext.isTermination())) {
                return null;
            }
            one = this.getOne(queryWrapper);
            this.executeServiceHooks(one, HookStage.AFTER_QUERY, hookContext);
            this.executeServiceHooks(one, HookStage.BEFORE_CLEAR_HOOK_CONTEXT, hookContext);
        } finally {
            if (context == null) {
                hookContext.clear();
            }
        }
        return BeanUtil.copyProperties(one, this.boClass);
    }

    @Override
    public List<B> queryList(Q query) {
        return this.queryList(query, HookContext.newContext());
    }

    @Override
    public List<B> queryList(Q query, HookContext context) {
        LambdaQueryWrapper<E> queryWrapper = QueryConditionResolver.resolveLambda(query);
        HookContext hookContext = Objects.requireNonNullElse(context, this.createHookContext());
        List<E> entityList;
        try {
            this.executeServiceHooks(queryWrapper, HookStage.BEFORE_QUERY, hookContext);
            if (BooleanUtil.isTrue(hookContext.isTermination())) {
                return Collections.emptyList();
            }
            entityList = this.baseMapper.selectList(queryWrapper);
            this.executeServiceHooks(entityList, HookStage.AFTER_QUERY_LIST, hookContext);
            this.executeServiceHooks(entityList, HookStage.BEFORE_CLEAR_HOOK_CONTEXT, hookContext);
        } finally {
            if (context == null) {
                hookContext.clear();
            }
        }
        return BeanUtil.copyToList(entityList, this.boClass);
    }

    @Override
    public IPage<B> queryPage(Pageable pageable, Q query) {
        return this.queryPage(pageable, query, HookContext.newContext());
    }

    @Override
    public IPage<B> queryPage(Pageable pageable, Q query, HookContext context) {
        LambdaQueryWrapper<E> queryWrapper = QueryConditionResolver.resolveLambda(query);
        HookContext hookContext = Objects.requireNonNullElse(context, this.createHookContext());
        IPage<E> page;
        try {
            this.executeServiceHooks(queryWrapper, HookStage.BEFORE_QUERY, hookContext);
            if (BooleanUtil.isTrue(hookContext.isTermination())) {
                return new Page<>(pageable.getPageNumber(), pageable.getPageSize());
            }
            page = this.baseMapper.selectPage(new Page<>(pageable.getPageNumber(), pageable.getPageSize()), queryWrapper);
            this.executeServiceHooks(page.getRecords(), HookStage.AFTER_QUERY_LIST, hookContext);
            this.executeServiceHooks(page.getRecords(), HookStage.BEFORE_CLEAR_HOOK_CONTEXT, hookContext);
        } finally {
            if (context == null) {
                hookContext.clear();
            }
        }
        IPage<B> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(BeanUtil.copyToList(page.getRecords(), this.boClass));
        return result;
    }

    @Override
    public <T> IPage<T> executeQueryPage(Pageable pageable, Wrapper<E> queryWrapper, Class<T> clazz) {
        IPage<E> page = this.baseMapper.selectPage(new Page<>(pageable.getPageNumber(), pageable.getPageSize()), queryWrapper);
        IPage<T> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(BeanUtil.copyToList(page.getRecords(), clazz));
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(B entityBO) {
        this.create(entityBO, HookContext.newContext());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void create(B entityBO, HookContext context) {
        HookContext hookContext = Objects.requireNonNullElse(context, this.createHookContext());
        hookContext.set(HookConstants.ENTITY_BO_KEY, entityBO);
        E entity = BeanUtil.copyProperties(entityBO, this.entityClass);
        try {
            this.executeServiceHooks(entity, HookStage.BEFORE_CREATE, hookContext);
            this.executeServiceHooks(entity, HookStage.BEFORE_SAVE, hookContext);
            if (BooleanUtil.isTrue(hookContext.isTermination())) {
                return;
            }
            this.baseMapper.insert(entity);
            this.executeServiceHooks(entity, HookStage.AFTER_CREATE, hookContext);
            this.executeServiceHooks(entity, HookStage.AFTER_SAVE, hookContext);
            this.executeServiceHooks(entity, HookStage.BEFORE_CLEAR_HOOK_CONTEXT, hookContext);
        } finally {
            if (context == null) {
                hookContext.clear();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchCreate(List<B> entityBOList) {
        this.batchCreate(entityBOList, HookContext.newContext());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchCreate(List<B> entityBOList, HookContext context) {
        HookContext hookContext = Objects.requireNonNullElse(context, this.createHookContext());
        hookContext.set(HookConstants.ENTITY_BO_LIST_KEY, entityBOList);
        List<E> entityList = BeanUtil.copyToList(entityBOList, this.entityClass);
        try {
            this.executeServiceHooks(entityList, HookStage.BEFORE_BATCH_CREATE, hookContext);
            this.executeServiceHooks(entityList, HookStage.BEFORE_BATCH_SAVE, hookContext);
            if (BooleanUtil.isTrue(hookContext.isTermination())) {
                return;
            }
            this.baseMapper.insert(entityList);
            this.executeServiceHooks(entityList, HookStage.AFTER_BATCH_CREATE, hookContext);
            this.executeServiceHooks(entityList, HookStage.AFTER_BATCH_SAVE, hookContext);
            this.executeServiceHooks(entityList, HookStage.BEFORE_CLEAR_HOOK_CONTEXT, hookContext);
        } finally {
            if (context == null) {
                hookContext.clear();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void modify(B entityBO) {
        this.modify(entityBO, HookContext.newContext());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void modify(B entityBO, HookContext context) {
        HookContext hookContext = Objects.requireNonNullElse(context, this.createHookContext());
        E entity = BeanUtil.copyProperties(entityBO, this.entityClass);

        E oldEntity = this.getById(entity.getId());
        if (oldEntity == null) {
            throw new ServiceException("修改实体失败！实体不存在：{}", entity.getId());
        }
        hookContext.set(HookConstants.ENTITY_BO_KEY, entityBO);
        hookContext.set(HookConstants.OLD_ENTITY_KEY, oldEntity);

        try {
            this.executeServiceHooks(entity, HookStage.BEFORE_MODIFY, hookContext);
            this.executeServiceHooks(entity, HookStage.BEFORE_SAVE, hookContext);
            if (BooleanUtil.isTrue(hookContext.isTermination())) {
                return;
            }
            this.baseMapper.updateById(entity);
            this.executeServiceHooks(entity, HookStage.AFTER_MODIFY, hookContext);
            this.executeServiceHooks(entity, HookStage.AFTER_SAVE, hookContext);
            this.executeServiceHooks(entity, HookStage.BEFORE_CLEAR_HOOK_CONTEXT, hookContext);
        } finally {
            if (context == null) {
                hookContext.clear();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchModify(List<B> entityBOList) {
        this.batchModify(entityBOList, HookContext.newContext());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchModify(List<B> entityBOList, HookContext context) {
        HookContext hookContext = Objects.requireNonNullElse(context, this.createHookContext());
        Map<Long, B> entityBOMapping = entityBOList.stream()
            .collect(Collectors.toMap(B::getId, Function.identity()));
        hookContext.set(HookConstants.ENTITY_BO_MAP_KEY, entityBOMapping);
        List<E> entityList = BeanUtil.copyToList(entityBOList, this.entityClass);

        List<Long> ids = entityList.stream()
            .map(E::getId)
            .toList();

        Map<Long, E> oldEntityMapping = this.listByIds(ids)
            .stream()
            .collect(Collectors.toMap(E::getId, Function.identity()));
        for (Long id : ids) {
            if (!oldEntityMapping.containsKey(id)) {
                throw new ServiceException("修改实体失败！实体不存在：{}", id);
            }
        }
        hookContext.set(HookConstants.OLD_ENTITY_MAP_KEY, oldEntityMapping);

        try {
            this.executeServiceHooks(entityList, HookStage.BEFORE_BATCH_MODIFY, hookContext);
            this.executeServiceHooks(entityList, HookStage.BEFORE_BATCH_SAVE, hookContext);
            if (BooleanUtil.isTrue(hookContext.isTermination())) {
                return;
            }
            this.baseMapper.updateById(entityList);
            this.executeServiceHooks(entityList, HookStage.AFTER_BATCH_MODIFY, hookContext);
            this.executeServiceHooks(entityList, HookStage.AFTER_BATCH_SAVE, hookContext);
            this.executeServiceHooks(entityList, HookStage.BEFORE_CLEAR_HOOK_CONTEXT, hookContext);
        } finally {
            if (context == null) {
                hookContext.clear();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long id) {
        this.delete(id, HookContext.newContext());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(Long id, HookContext context) {
        HookContext hookContext = Objects.requireNonNullElse(context, this.createHookContext());
        E entity = this.getById(id);
        if (entity == null) {
            throw new ServiceException("删除实体失败！实体不存在：{}", id);
        }
        try {
            this.executeServiceHooks(entity, HookStage.BEFORE_DELETE, hookContext);
            if (BooleanUtil.isTrue(hookContext.isTermination())) {
                return;
            }
            this.baseMapper.deleteById(id);
            this.executeServiceHooks(entity, HookStage.AFTER_DELETE, hookContext);
            this.executeServiceHooks(entity, HookStage.BEFORE_CLEAR_HOOK_CONTEXT, hookContext);
        } finally {
            if (context == null) {
                hookContext.clear();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchDelete(List<Long> ids) {
        this.batchDelete(ids, HookContext.newContext());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void batchDelete(List<Long> ids, HookContext context) {
        HookContext hookContext = Objects.requireNonNullElse(context, this.createHookContext());
        Set<Long> idSet = Sets.newHashSet(ids);
        List<E> entityList = this.listByIds(ids)
            .stream()
            .toList();
        for (Long id : ids) {
            if (!idSet.contains(id)) {
                throw new ServiceException("删除实体失败！实体不存在：{}", id);
            }
        }
        try {
            this.executeServiceHooks(entityList, HookStage.BEFORE_BATCH_DELETE, hookContext);
            if (BooleanUtil.isTrue(hookContext.isTermination())) {
                return;
            }
            this.baseMapper.deleteByIds(ids);
            this.executeServiceHooks(entityList, HookStage.AFTER_BATCH_DELETE, hookContext);
            this.executeServiceHooks(entityList, HookStage.BEFORE_CLEAR_HOOK_CONTEXT, hookContext);
        } finally {
            if (context == null) {
                hookContext.clear();
            }
        }
    }
}
