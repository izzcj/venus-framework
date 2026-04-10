package com.ale.venus.core.service;

import com.ale.venus.common.domain.entity.BaseEntity;
import com.ale.venus.core.pojo.BaseBO;
import com.ale.venus.core.query.BaseQuery;
import com.ale.venus.core.service.hook.HookContext;
import com.ale.venus.core.service.hook.LocalServiceHook;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * CRUD服务接口
 *
 * @param <E> 实体类型
 * @param <B> 实体BO类型
 * @param <Q> 查询条件类型
 * @author Ale
 * @version 1.0.0
 */
public interface ICrudService<E extends BaseEntity, B extends BaseBO, Q extends BaseQuery> extends IService<E>, LocalServiceHook<E> {

    /**
     * 根据ID查询单个实体
     *
     * @param id ID
     * @return 实体
     */
    B queryById(Long id);

    /**
     * 根据查询条件查询单个实体
     *
     * @param query 查询条件
     * @return 实体
     */
    B queryOne(Q query);

    /**
     * 根据查询条件查询单个实体
     *
     * @param query   查询条件
     * @param context 上下文
     * @return 实体
     */
    B queryOne(Q query, HookContext context);

    /**
     * 根据查询条件查询实体列表
     *
     * @param query 查询条件
     * @return 实体列表
     */
    List<B> queryList(Q query);

    /**
     * 根据查询条件查询实体列表
     *
     * @param query   查询条件
     * @param context 上下文
     * @return 实体列表
     */
    List<B> queryList(Q query, HookContext context);

    /**
     * 根据查询条件查询分页实体
     *
     * @param pageable 分页信息
     * @param query    查询条件
     * @return 分页实体
     */
    IPage<B> queryPage(Pageable pageable, Q query);

    /**
     * 根据查询条件查询分页实体
     *
     * @param pageable 分页信息
     * @param query    查询条件
     * @param context  上下文
     * @return 分页实体
     */
    IPage<B> queryPage(Pageable pageable, Q query, HookContext context);

    /**
     * 执行分页查询
     *
     * @param <T>          返回类型
     * @param pageable     分页信息
     * @param queryWrapper 查询条件
     * @param clazz        实体类型
     * @return 分页实体
     */
    <T> IPage<T> executeQueryPage(Pageable pageable, Wrapper<E> queryWrapper, Class<T> clazz);

    /**
     * 创建实体
     *
     * @param entityBO 实体BO
     */
    void create(B entityBO);

    /**
     * 创建实体
     *
     * @param entityBO 实体
     * @param context  上下文
     */
    void create(B entityBO, HookContext context);

    /**
     * 批量创建实体
     *
     * @param entityBOList 实体BO列表
     */
    void batchCreate(List<B> entityBOList);

    /**
     * 批量创建实体
     *
     * @param entityBOList 实体列表
     * @param context      上下文
     */
    void batchCreate(List<B> entityBOList, HookContext context);

    /**
     * 修改实体
     *
     * @param entityBO 实体BO
     */
    void modify(B entityBO);

    /**
     * 修改实体
     *
     * @param entityBO 实体BO
     * @param context  上下文
     */
    void modify(B entityBO, HookContext context);

    /**
     * 批量修改实体
     *
     * @param entityBOList 实体BO列表
     */
    void batchModify(List<B> entityBOList);

    /**
     * 批量修改实体
     *
     * @param entityBOList 实体BO列表
     * @param context      上下文
     */
    void batchModify(List<B> entityBOList, HookContext context);

    /**
     * 根据id删除实体
     *
     * @param id 实体id
     */
    void delete(Long id);

    /**
     * 根据id删除实体
     *
     * @param id     实体id
     * @param context 上下文
     */
    void delete(Long id, HookContext context);

    /**
     * 批量删除实体
     *
     * @param ids 实体id列表
     */
    void batchDelete(List<Long> ids);

    /**
     * 批量删除实体
     *
     * @param ids    实体id列表
     * @param context 上下文
     */
    void batchDelete(List<Long> ids, HookContext context);
}
