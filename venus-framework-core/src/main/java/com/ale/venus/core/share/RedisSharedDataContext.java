package com.ale.venus.core.share;

import com.ale.venus.common.utils.CacheUtils;
import com.ale.venus.common.utils.CastUtils;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Redis共享数据上下文
 *
 * @author Ale
 * @version 1.0.0
 */
public class RedisSharedDataContext implements SharedDataContext {

    /**
     * 共享上下文存储键前缀
     */
    static final String SHARED_OBJECT_CONTEXT_KEY_PREFIX = CacheUtils.buildCacheKeyWithPrefix("sharedDataContext");

    /**
     * 值缓存器
     */
    private final LoadingCache<String, Object> valueCache;

    /**
     * Hash操作
     */
    private final HashOperations<String, String, Object> hashOperations;

    /**
     * 共享上下文键
     */
    private final String sharedContextKey;

    public RedisSharedDataContext(RedisTemplate<String, Object> redisTemplate, String sharedContextKey) {
        this.hashOperations = redisTemplate.opsForHash();
        this.sharedContextKey = sharedContextKey;

        this.valueCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(30))
            .build(key -> this.hashOperations.get(
                this.buildHashKey(),
                key
            ));
    }

    /**
     * 构建HashKey
     *
     * @return HashKey
     */
    private String buildHashKey() {
        return CacheUtils.buildCacheKey(
            SHARED_OBJECT_CONTEXT_KEY_PREFIX,
            this.sharedContextKey
        );
    }

    @Override
    public <T> T set(String key, T value) {
        T oldValue = CastUtils.cast(this.get(key));
        String hashKey = this.buildHashKey();
        this.hashOperations.put(
            hashKey,
            key,
            value
        );

        return oldValue;
    }

    @Override
    public boolean has(String key) {
        return this.valueCache.get(key) != null;
    }

    @Override
    public <T> T get(String key) {
        return CastUtils.cast(
            this.valueCache.get(key)
        );
    }

    @Override
    public <T> T getOrDefault(String key, T defaultValue) {
        T value = this.get(key);
        if (value == null) {
            return defaultValue;
        }

        return value;
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
        T value = CastUtils.cast(
            this.get(key)
        );

        if (value == null) {
            String hashKey = this.buildHashKey();
            value = mappingFunc.apply(key);
            this.hashOperations.put(hashKey, key, value);
        }

        return value;
    }

    @Override
    public <T> T remove(String key) {
        T value = CastUtils.cast(
            this.get(key)
        );

        String hashKey = this.buildHashKey();
        this.hashOperations.delete(hashKey, key);
        this.valueCache.invalidate(key);
        return value;
    }

    @Override
    public void clear() {
        String hashKey = this.buildHashKey();
        this.hashOperations.delete(
            hashKey,
            this.hashOperations.keys(hashKey)
                .toArray()
        );

        this.valueCache.invalidateAll();
    }
}
