package com.ale.venus.common.domain.entity;

/**
 * 实体初始化器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface EntityInitializer {

    /**
     * 初始化实体
     *
     * @param entityClass 实体类
     */
    void initialize(Class<? extends BaseEntity> entityClass);
}
