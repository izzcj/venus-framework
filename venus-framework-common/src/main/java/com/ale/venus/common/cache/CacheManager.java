package com.ale.venus.common.cache;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.ConcurrentHashSet;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 缓存管理器
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Ale
 * @version 1.0.0
 */
public interface CacheManager<K, V> {

    /**
     * 缓存引用
     */
    Map<String, Set<CacheManager<?, ?>>> CACHE_REF_MAP = new ConcurrentHashMap<>(64);

    /**
     * 清除指定组的缓存
     *
     * @param group 缓存组
     */
    static void clearCache(String group) {
        Set<CacheManager<?, ?>> cacheManagers = CACHE_REF_MAP.get(group);
        if (CollectionUtil.isNotEmpty(cacheManagers)) {
            cacheManagers.forEach(CacheManager::clear);
        }
    }

    /**
     * 清除指定类下的缓存
     *
     * @param clazz 类
     */
    static void clearCache(Class<?> clazz) {
        clearCache(clazz.getName());
    }

    /**
     * 清除所有缓存
     */
    static void clearCache() {
        CACHE_REF_MAP.values().forEach(set -> set.forEach(CacheManager::clear));
    }

    /**
     * 创建一个新缓存
     *
     * @param group 缓存组
     * @param <K> 缓存键类型
     * @param <V> 缓存值类型
     * @return 缓存管理器对象
     */
    static <K, V> CacheManager<K, V> newCache(String group) {
        return newCache(group, 16);
    }

    /**
     * 创建一个新缓存
     *
     * @param group 缓存组
     * @param initialCapacity 缓存初始容量
     * @param <K> 缓存键类型
     * @param <V> 缓存值类型
     * @return 缓存管理器对象
     */
    static <K, V> CacheManager<K, V> newCache(String group, int initialCapacity) {
        SimpleCacheManager<K, V> cacheManager = new SimpleCacheManager<>();
        Set<CacheManager<?, ?>> cacheSet = CACHE_REF_MAP.computeIfAbsent(group, key -> new ConcurrentHashSet<>(initialCapacity));
        cacheSet.add(cacheManager);

        return cacheManager;
    }

    /**
     * 创建一个新缓存
     *
     * @param clazz 缓存所属类
     * @param <K> 缓存键类型
     * @param <V> 缓存值类型
     * @return 缓存管理器对象
     */
    static <K, V> CacheManager<K, V> newCache(Class<?> clazz) {
        return newCache(clazz.getName());
    }

    /**
     * 创建一个新缓存
     *
     * @param clazz 缓存所属类
     * @param initialCapacity 缓存初始容量
     * @param <K> 缓存键类型
     * @param <V> 缓存值类型
     * @return 缓存管理器对象
     */
    static <K, V> CacheManager<K, V> newCache(Class<?> clazz, int initialCapacity) {
        return newCache(clazz.getName(), initialCapacity);
    }

    /**
     * 清除缓存
     */
    void clear();

    /**
     * 设置缓存
     *
     * @param key 键
     * @param value 值
     * @return 如果key存在，则会返回被覆盖的旧值
     */
    V set(K key, V value);

    /**
     * 获取缓存
     *
     * @param key 键
     * @return 值
     */
    V get(K key);

    /**
     * 获取缓存
     *
     * @param key 键
     * @param defaultValue 默认值
     * @return 值
     */
    V getOrDefault(K key, V defaultValue);

    /**
     * 获取缓存
     *
     * @param key 键
     * @param valueSupplier 值提供函数
     * @return 值
     */
    V getIfAbsent(K key, Supplier<V> valueSupplier);

    /**
     * 获取缓存
     *
     * @param key 键
     * @param valueSupplier 值提供函数
     * @return 值
     */
    V computeIfAbsent(K key, Function<K, V> valueSupplier);

    /**
     * 判断缓存是否存在
     *
     * @param key 键
     * @return bool
     */
    boolean has(K key);

    /**
     * 删除缓存
     *
     * @param key 键
     * @return 删除键对应的值，如果存在的话
     */
    V remove(K key);
}
