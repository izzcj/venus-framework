package com.ale.venus.common.support;

/**
 * 是否支持
 *
 * @param <T> 支持类型泛型
 * @author Ale
 * @version 1.0.0
 */
public interface Supportable<T> {

    /**
     * 是否支持某个数据
     *
     * @param t 数据
     * @return bool
     */
    boolean supports(T t);
}
