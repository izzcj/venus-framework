package com.ale.venus.security.context;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.exception.AuthenticationNotExistException;
import com.ale.venus.common.exception.ExceptionCode;
import com.ale.venus.common.security.ExpirationAwareToken;
import com.ale.venus.common.utils.CacheUtils;
import com.ale.venus.common.utils.CastUtils;
import com.ale.venus.common.utils.RedisUtils;
import com.ale.venus.security.exception.VenusSecurityException;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Redis会话上下文实现
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class RedisSessionContext implements SessionContext {

    /**
     * 会话上下文存储键前缀
     */
    private static final String SESSION_CONTEXT_KEY_PREFIX = CacheUtils.buildCacheKeyWithPrefix("sessionContext");
    /**
     * 值缓存器
     */
    private final LoadingCache<ValueCacheKey, Object> valueCache;
    /**
     * 刷新缓存
     */
    private final LoadingCache<RefreshCacheKey, Boolean> refreshCache;
    /**
     * Hash操作
     */
    private final HashOperations<String, String, Object> hashOperations;
    /**
     * Session刷新后的处理器
     */
    private final ObjectProvider<SessionTouchedHandler> sessionTouchedHandlers;

    /**
     * 值缓存Key
     *
     * @param hashKey Hash键名
     * @param key 键名
     */
    private record ValueCacheKey(String hashKey, String key) {
    }

    /**
     * 刷新缓存Key
     *
     * @param hashKey Hash键名
     * @param expiresIn 过期日期
     */
    private record RefreshCacheKey(String hashKey, Date expiresIn) {

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RefreshCacheKey that = (RefreshCacheKey) o;
            return this.hashKey.equals(that.hashKey);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.hashKey);
        }
    }

    public RedisSessionContext(RedisTemplate<String, Object> redisTemplate, TaskExecutor taskExecutor, ObjectProvider<SessionTouchedHandler> sessionTouchedHandlers) {
        this.hashOperations = redisTemplate.opsForHash();
        this.sessionTouchedHandlers = sessionTouchedHandlers;

        this.valueCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(30))
            .executor(taskExecutor)
            .build(valueCacheKey -> this.hashOperations.get(
                valueCacheKey.hashKey(),
                valueCacheKey.key()
            ));
        this.refreshCache = Caffeine.newBuilder()
            .executor(taskExecutor)
            .expireAfterWrite(Duration.ofMinutes(10))
            .build(refreshCacheKey -> RedisUtils.setExpirationAt(
                refreshCacheKey.hashKey(),
                refreshCacheKey.expiresIn()
            ));
    }

    /**
     * 构建HashKey
     *
     * @return HashKey
     */
    private String buildHashKey() {
        return this.buildHashKey(null);
    }

    /**
     * 构建HashKey
     *
     * @param tokenId Token唯一编号
     * @return HashKey
     */
    private String buildHashKey(String tokenId) {
        if (StrUtil.isNotBlank(tokenId)) {
            return CacheUtils.buildCacheKey(
                SESSION_CONTEXT_KEY_PREFIX,
                tokenId
            );
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            log.warn("认证信息不存在，可能原因是调用此代码的线程为一个新的线程或者当前请求无需认证的线程");
            throw new AuthenticationNotExistException(ExceptionCode.AUTHENTICATION_NOT_EXIST);
        }

        if (authentication instanceof ExpirationAwareToken token) {
            return CacheUtils.buildCacheKey(
                SESSION_CONTEXT_KEY_PREFIX,
                token.getTokenId()
            );
        }

        throw new VenusSecurityException("认证信息[{}]未实现接口[{}]", authentication.getClass().getSimpleName(), "ExpirationAwareToken");
    }

    @Override
    public <T> T set(String key, T value) {
        String hashKey = this.buildHashKey();
        T oldValue = CastUtils.cast(this.hashOperations.get(hashKey, key));
        this.hashOperations.put(
            hashKey,
            key,
            value
        );

        // 刷新一下session的过期时间
        this.refreshExpiration(null);

        return oldValue;
    }

    @Override
    public boolean has(String key) {
        return this.valueCache.get(
            new ValueCacheKey(
                this.buildHashKey(),
                key
            )
        ) != null;
    }

    @Override
    public <T> T get(String key) {
        return CastUtils.cast(
            this.valueCache.get(
                new ValueCacheKey(
                    this.buildHashKey(),
                    key
                )
            )
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
        String hashKey = this.buildHashKey();
        T value = CastUtils.cast(
            this.get(key)
        );

        this.hashOperations.delete(hashKey, key);
        this.valueCache.invalidate(
            new ValueCacheKey(hashKey, key)
        );
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

    @Override
    public <T> T get(String key, String tokenId) {
        return CastUtils.cast(
            this.valueCache.get(
                new ValueCacheKey(
                    this.buildHashKey(tokenId),
                    key
                )
            )
        );
    }

    @Override
    public <T> T set(String key, T value, String tokenId) {
        String hashKey = this.buildHashKey(tokenId);
        T oldValue = CastUtils.cast(this.hashOperations.get(hashKey, key));
        this.hashOperations.put(
            hashKey,
            key,
            value
        );

        return oldValue;
    }

    @Override
    public void refreshExpiration(ExpirationAwareToken expirationAwareToken) {
        if (expirationAwareToken != null) {
            var result = this.refreshCache.get(
                new RefreshCacheKey(
                    this.buildHashKey(expirationAwareToken.getTokenId()),
                    expirationAwareToken.getExpiresIn()
                )
            );

            if (BooleanUtil.isTrue(result)) {
                for (SessionTouchedHandler sessionTouchedHandler : this.sessionTouchedHandlers) {
                    sessionTouchedHandler.onTouched((Authentication) expirationAwareToken);
                }
            }
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof ExpirationAwareToken token) {
            var result = this.refreshCache.get(
                new RefreshCacheKey(
                    this.buildHashKey(token.getTokenId()),
                    token.getExpiresIn()
                )
            );

            if (BooleanUtil.isTrue(result)) {
                for (SessionTouchedHandler sessionTouchedHandler : this.sessionTouchedHandlers) {
                    sessionTouchedHandler.onTouched(
                        authentication
                    );
                }
            }
        }
    }

    @Override
    public void expiresSession(ExpirationAwareToken expirationAwareToken) {
        this.expiresSession(expirationAwareToken.getTokenId());
    }

    @Override
    public void expiresSession(String tokenId) {
        if (StrUtil.isNotBlank(tokenId)) {
            var hashKey = this.buildHashKey(tokenId);
            RedisUtils.delete(hashKey);
            this.invalidateCache(hashKey);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof ExpirationAwareToken token) {
            var hashKey = this.buildHashKey(token.getTokenId());
            RedisUtils.delete(hashKey);
            this.invalidateCache(hashKey);
        }
    }

    /**
     * 失效指定Hash键下的所有缓存
     *
     * @param hashKey Hash键名
     */
    private void invalidateCache(String hashKey) {
        for (ValueCacheKey valueCacheKey : this.valueCache.asMap().keySet()) {
            if (StrUtil.equals(valueCacheKey.hashKey(), hashKey)) {
                this.valueCache.invalidate(valueCacheKey);
            }
        }
        for (RefreshCacheKey refreshCacheKey : this.refreshCache.asMap().keySet()) {
            if (StrUtil.equals(refreshCacheKey.hashKey(), hashKey)) {
                this.refreshCache.invalidate(refreshCacheKey);
            }
        }
    }
}
