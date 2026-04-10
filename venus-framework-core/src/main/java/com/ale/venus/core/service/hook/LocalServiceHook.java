package com.ale.venus.core.service.hook;

import com.ale.venus.common.domain.entity.BaseEntity;

/**
 * 局部service钩子
 *
 * @param <E> 实体类型
 * @author Ale
 * @version 1.0.0
 */
public interface LocalServiceHook<E extends BaseEntity> extends ServiceHook<E> {
}
