package com.ale.venus.common.support;

/**
 * 可提供的
 *
 * @param <T> 提供类型
 * @author Ale
 * @version 1.0.0
 */
public interface Providable<T> {

    /**
     * 提供
     *
     * @return 提供类型
     */
    T provide();
}
