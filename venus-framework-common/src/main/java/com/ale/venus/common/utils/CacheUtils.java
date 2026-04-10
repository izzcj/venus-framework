package com.ale.venus.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.ale.venus.common.constants.StringConstants;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 缓存工具类
 * 使用caffeine本地缓存
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CacheUtils {

    /**
     * 缓存Key分隔符
     */
    public static final String CACHE_SEPARATOR = StringConstants.COLON;

    /**
     * 缓存前缀
     */
    public static final String CACHE_PREFIX = "venus";

    /**
     * 构建一个新缓存
     *
     * @param initialCapacity 初始大小
     * @param <K> 缓存键泛型
     * @param <V> 缓存值泛型
     * @return 缓存对象
     */
    public static <K, V> Cache<K, V> newCache(int initialCapacity) {
        return Caffeine.newBuilder()
            .initialCapacity(initialCapacity)
            .build();
    }

    /**
     * 构建一个新缓存
     *
     * @param <K> 缓存键泛型
     * @param <V> 缓存值泛型
     * @return 缓存对象
     */
    public static <K, V> Cache<K, V> newCache() {
        return newCache(1024);
    }

    /**
     * 构建一个缓存KEY
     *
     * @param fragments key片段字符串
     * @return key
     */
    public static String buildCacheKeyWithPrefix(String... fragments) {
        List<String> tokens = Lists.newArrayListWithExpectedSize(fragments.length + 1);
        tokens.add(CACHE_PREFIX);
        tokens.addAll(Arrays.asList(fragments));
        return CollectionUtil.join(tokens, CACHE_SEPARATOR);
    }

    /**
     * 构建一个缓存Key不带前缀
     *
     * @param fragments key片段字符串
     * @return key
     */
    public static String buildCacheKey(String... fragments) {
        return CollectionUtil.join(Arrays.asList(fragments), CACHE_SEPARATOR);
    }
}
