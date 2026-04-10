package com.ale.venus.workflow.cache;

import com.ale.venus.common.data.DataRepository;
import com.ale.venus.common.utils.CacheUtils;

import java.time.Duration;

/**
 * 流程引擎缓存
 *
 * @author Ale
 * @version 1.0.0
 */
public interface FlowEngineCache extends DataRepository {

    /**
     * 流程引擎缓存KEY前缀
     */
    String FLOW_ENGINE_CACHE_KEY_PREFIX = CacheUtils.buildCacheKeyWithPrefix("flowEngine");

    /**
     * 设置缓存
     *
     * @param key      缓存KEY
     * @param value    缓存值
     * @param duration 缓存时长
     * @param <T>   缓存值类型
     * @return 缓存值
     */
    <T> T set(String key, T value, Duration duration);

    /**
     * 设置缓存的过期时间
     *
     * @param key      缓存KEY
     * @param duration 缓存时长
     * @return 是否设置成功
     */
    boolean setExpiration(String key, Duration duration);

    /**
     * 构建流程引擎缓存KEY
     *
     * @return 缓存KEY
     */
    default String buildCacheKey() {
        return CacheUtils.buildCacheKey(FLOW_ENGINE_CACHE_KEY_PREFIX, "instanceModel");
    }

    /**
     * 构建流程引擎缓存KEY
     *
     * @param key key
     * @return 缓存KEY
     */
    default String buildCacheKey(String key) {
        return CacheUtils.buildCacheKey(FLOW_ENGINE_CACHE_KEY_PREFIX, "instanceModel", key);
    }

}
