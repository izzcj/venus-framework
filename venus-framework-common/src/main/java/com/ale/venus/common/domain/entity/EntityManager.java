package com.ale.venus.common.domain.entity;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 实体管理器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface EntityManager {

    /**
     * 是否包含实体类
     *
     * @param entityClass 目标实体类
     * @return bool
     */
    boolean containsEntity(Class<? extends BaseEntity> entityClass);

    /**
     * 获取所有实体类
     *
     * @return 实体类集合
     */
    Set<Class<? extends BaseEntity>> getEntityClasses();

    /**
     * 获取标有指定注解的实体类
     *
     * @param annotationClass 注解类
     * @return 符合条件的实体类集合
     */
    Set<Class<? extends BaseEntity>> getAnnotatedEntityClasses(Class<? extends Annotation> annotationClass);
}
