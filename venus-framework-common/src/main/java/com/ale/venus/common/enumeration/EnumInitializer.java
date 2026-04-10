package com.ale.venus.common.enumeration;

/**
 * 枚举初始化器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface EnumInitializer {

    /**
     * 初始化枚举
     *
     * @param enumClass 枚举类
     */
    void initialize(Class<? extends BaseEnum<?>> enumClass);

}
