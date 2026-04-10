package com.ale.venus.common.cache;

import lombok.EqualsAndHashCode;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Java原生Cache实现
 *
 * @param <K> 键类型
 * @param <V> 值类型
 * @author Ale
 * @version 1.0.0
 */
@EqualsAndHashCode
public class SimpleCacheManager<K, V> implements CacheManager<K, V> {

    /**
     * 缓存Map
     */
    private final ConcurrentReferenceHashMap<K, V> cacheMap;

    public SimpleCacheManager(int initialCapacity) {
        this.cacheMap = new ConcurrentReferenceHashMap<>(initialCapacity);
    }

    public SimpleCacheManager() {
        this(128);
    }

    @Override
    public void clear() {
        this.cacheMap.clear();
    }

    @Override
    public V set(K key, V value) {
        return this.cacheMap.put(key, value);
    }

    @Override
    public V get(K key) {
        return this.cacheMap.get(key);
    }

    @Override
    public V getOrDefault(K key, V defaultValue) {
        return this.cacheMap.getOrDefault(key, defaultValue);
    }

    @Override
    public V getIfAbsent(K key, Supplier<V> valueSupplier) {
        V value = this.get(key);
        if (value == null) {
            return valueSupplier.get();
        }

        return value;
    }

    @Override
    public V computeIfAbsent(K key, Function<K, V> valueSupplier) {
        return this.cacheMap.computeIfAbsent(
            key,
            valueSupplier
        );
    }

    @Override
    public boolean has(K key) {
        return this.cacheMap.containsKey(key);
    }

    @Override
    public V remove(K key) {
        return this.cacheMap.remove(key);
    }
}
