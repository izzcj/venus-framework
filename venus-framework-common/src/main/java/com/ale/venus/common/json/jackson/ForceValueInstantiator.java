package com.ale.venus.common.json.jackson;

import com.ale.venus.common.cache.CacheManager;
import com.ale.venus.common.utils.ReflectionUtils;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import lombok.RequiredArgsConstructor;

/**
 * 强制实例化
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor
public class ForceValueInstantiator extends ValueInstantiator {

    /**
     * 实例化器实例缓存
     */
    private static final CacheManager<Class<?>, ForceValueInstantiator> INSTANCE_CACHE = CacheManager.newCache(ForceValueInstantiator.class);

    /**
     * 需要实例化的类
     */
    private final Class<?> clazz;

    /**
     * 获取实例
     *
     * @param clazz Bean类
     * @return 实例化器实例
     */
    public static ForceValueInstantiator getInstance(Class<?> clazz) {
        return INSTANCE_CACHE.computeIfAbsent(clazz, ForceValueInstantiator::new);
    }

    @Override
    public boolean canCreateUsingDefault() {
        return true;
    }

    @Override
    public Object createUsingDefault(DeserializationContext context) {
        return ReflectionUtils.instantiate(this.clazz);
    }
}
