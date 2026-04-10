package com.ale.venus.common.data;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 数据仓库
 *
 * @author Ale
 * @version 1.0.0
 */
public interface DataRepository {

    /**
     * 设置数据
     *
     * @param <T> 期望值类型
     * @param key   键
     * @param value 值
     * @return 如果对应key的值已经存在，则返回旧的值
     */
    <T> T set(String key, T value);

    /**
     * 判断是否包含指定键数据
     *
     * @param key 键
     * @return bool
     */
    boolean has(String key);

    /**
     * 获取数据
     *
     * @param key 键
     * @param <T> 值泛型
     * @return 值
     */
    <T> T get(String key);

    /**
     * 获取数据
     *
     * @param key          键
     * @param defaultValue 默认值
     * @param <T>          值泛型
     * @return 值
     */
    <T> T getOrDefault(String key, T defaultValue);

    /**
     * 获取数据，如果不存在则指定并设置进context中
     *
     * @param key      键
     * @param supplier 值提供器
     * @param <T>      值泛型
     * @return 值
     */
    <T> T getIfAbsent(String key, Supplier<T> supplier);

    /**
     * 获取数据，如果不存在则指定并设置进context中
     *
     * @param key         键
     * @param mappingFunc 值映射函数
     * @param <T>         值泛型
     * @return 值
     */
    <T> T computeIfAbsent(String key, Function<String, T> mappingFunc);

    /**
     * 删除数据
     *
     * @param key 键
     * @return 值
     * @param <T> 值泛型
     */
    <T> T remove(String key);

    /**
     * 清除所有数据
     */
    void clear();
}
