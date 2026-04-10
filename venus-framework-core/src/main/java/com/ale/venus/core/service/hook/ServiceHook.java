package com.ale.venus.core.service.hook;

import com.ale.venus.common.domain.entity.BaseEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

/**
 * service钩子
 *
 * @param <E> 实体类型
 * @author Ale
 * @version 1.0.0
 */
public interface ServiceHook<E extends BaseEntity> {

    /**
     * 查询前置处理
     *
     * @param queryWrapper 查询参数
     * @param context      上下文
     */
    default void beforeQuery(LambdaQueryWrapper<E> queryWrapper, HookContext context) {
    }

    /**
     * 查询后置处理
     *
     * @param entity  查询结果
     * @param context 上下文
     */
    default void afterQuery(E entity, HookContext context) {
    }

    /**
     * 查询列表后置处理
     *
     * @param entities 查询结果列表
     * @param context  上下文
     */
    default void afterQueryList(List<E> entities, HookContext context) {
    }

    /**
     * 创建前置处理
     *
     * @param entity  实体
     * @param context 上下文
     */
    default void beforeCreate(E entity, HookContext context) {
    }

    /**
     * 创建后置处理
     *
     * @param entity  实体
     * @param context 上下文
     */
    default void afterCreate(E entity, HookContext context) {
    }

    /**
     * 批量创建前置处理
     *
     * @param entityList 实体列表
     * @param context    上下文
     */
    default void beforeBatchCreate(List<E> entityList, HookContext context) {
    }

    /**
     * 批量创建后置处理
     *
     * @param entityList 实体列表
     * @param context    上下文
     */
    default void afterBatchCreate(List<E> entityList, HookContext context) {
    }

    /**
     * 修改前置处理
     *
     * @param entity  实体
     * @param context 上下文
     */
    default void beforeModify(E entity, HookContext context) {
    }

    /**
     * 修改后置处理
     *
     * @param entity  实体
     * @param context 上下文
     */
    default void afterModify(E entity, HookContext context) {
    }

    /**
     * 批量修改前置处理
     *
     * @param entityList 实体列表
     * @param context    上下文
     */
    default void beforeBatchModify(List<E> entityList, HookContext context) {
    }

    /**
     * 批量修改后置处理
     *
     * @param entityList 实体列表
     * @param context    上下文
     */
    default void afterBatchModify(List<E> entityList, HookContext context) {
    }

    /**
     * 保存前置处理
     *
     * @param entity  实体
     * @param context 上下文
     */
    default void beforeSave(E entity, HookContext context) {
    }

    /**
     * 保存后置处理
     *
     * @param entity  实体
     * @param context 上下文
     */
    default void afterSave(E entity, HookContext context) {
    }

    /**
     * 批量保存前置处理
     *
     * @param entityList 实体列表
     * @param context    上下文
     */
    default void beforeBatchSave(List<E> entityList, HookContext context) {
    }

    /**
     * 批量保存后置处理
     *
     * @param entityList 实体列表
     * @param context    上下文
     */
    default void afterBatchSave(List<E> entityList, HookContext context) {
    }

    /**
     * 删除前置处理
     *
     * @param entity  删除的实体
     * @param context 上下文
     */
    default void beforeDelete(E entity, HookContext context) {
    }

    /**
     * 删除后置处理
     *
     * @param entity  删除的实体
     * @param context 上下文
     */
    default void afterDelete(E entity, HookContext context) {
    }

    /**
     * 批量删除前置处理
     *
     * @param entityList 删除的实体集合
     * @param context    上下文
     */
    default void beforeBatchDelete(List<E> entityList, HookContext context) {
    }

    /**
     * 批量删除后置处理
     *
     * @param entityList 删除的实体集合
     * @param context    上下文
     */
    default void afterBatchDelete(List<E> entityList, HookContext context) {
    }

    /**
     * 清理钩子上下文前置处理
     *
     * @param context 上下文
     */
    default void beforeClearHookContext(HookContext context) {
    }

}
