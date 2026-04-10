package com.ale.venus.workflow.cache;

import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.common.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 基于redis的流程引擎缓存
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class RedisFlowEngineCache implements FlowEngineCache {

    @Override
    public <T> T set(String key, T value) {
        return this.set(key, value, null);
    }

    @Override
    public <T> T set(String key, T value, Duration duration) {
        String cacheKey = this.buildCacheKey(key);
        T oldValue = CastUtils.cast(RedisUtils.get(cacheKey));

        RedisUtils.set(cacheKey, value, duration);

        return oldValue;
    }

    @Override
    public boolean has(String key) {
        return RedisUtils.has(this.buildCacheKey(key));
    }

    @Override
    public <T> T get(String key) {
        return CastUtils.cast(RedisUtils.get(this.buildCacheKey(key)));
    }

    @Override
    public <T> T getOrDefault(String key, T defaultValue) {
        T result = this.get(key);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

    @Override
    public <T> T getIfAbsent(String key, Supplier<T> supplier) {
        T value = this.get(key);
        if (value == null) {
            return supplier.get();
        }
        return value;
    }

    @Override
    public <T> T computeIfAbsent(String key, Function<String, T> mappingFunc) {
        return RedisUtils.compute(key, mappingFunc);
    }

    @Override
    public <T> T remove(String key) {
        T removeValue = this.get(key);
        RedisUtils.delete(this.buildCacheKey(key));
        return removeValue;
    }

    @Override
    public void clear() {
        RedisUtils.batchDelete(this.buildCacheKey());
    }

    @Override
    public boolean setExpiration(String key, Duration duration) {
        return RedisUtils.setExpiration(this.buildCacheKey(key), duration);
    }
}
