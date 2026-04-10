package com.ale.venus.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.GenericTypeResolver;

/**
 * 泛型工具类
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenericTypeUtils {

    /**
     * 获取泛型参数
     *
     * @param clazz      类
     * @param genericIfc 泛型接口
     * @return 泛型参数
     */
    public static Class<?> resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        return resolveTypeArguments(clazz, genericIfc, 0);
    }

    /**
     * 获取泛型参数
     *
     * @param clazz      类
     * @param genericIfc 泛型接口
     * @param index      泛型参数索引
     * @return 泛型参数
     */
    public static Class<?> resolveTypeArguments(Class<?> clazz, Class<?> genericIfc, int index) {
        Class<?>[] typeArguments = resolveAllTypeArguments(clazz, genericIfc);
        if (typeArguments == null || typeArguments.length <= index) {
            throw new IllegalArgumentException("Could not find type argument at index " + index + " for generic interface [" + genericIfc.getName() + "]");
        }
        return typeArguments[index];
    }

    /**
     * 获取泛型参数
     *
     * @param clazz      类
     * @param genericIfc 泛型接口
     * @return 泛型参数
     */
    public static Class<?>[] resolveAllTypeArguments(Class<?> clazz, Class<?> genericIfc) {
        return GenericTypeResolver.resolveTypeArguments(clazz, genericIfc);
    }
}
