package com.ale.venus.core.service.hook;

import com.ale.venus.common.domain.entity.BaseEntity;
import com.ale.venus.common.support.TriConsumer;

/**
 * service钩子调用器持有器
 *
 * @param <E> 实体类型
 * @author Ale
 * @version 1.0.0
 */
public interface ServiceHookInvokerHolder<E extends BaseEntity> extends HookInvokerHolder<TriConsumer<GlobalServiceHook, ?, HookContext>, TriConsumer<LocalServiceHook<E>, ?, HookContext>> {
}
