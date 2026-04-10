package com.ale.venus.common.support;


import com.ale.venus.common.utils.CastUtils;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

/**
 * 反射字段
 *
 * @param field     字段反射对象
 * @param varHandle 字段处理句柄
 * @author Ale
 * @version 1.0.0
 */
public record ReflectionField(Field field, VarHandle varHandle) {

    /**
     * 获取属性的值
     *
     * @param instance 实例对象
     * @param <T>      值类型
     * @return 值
     */
    public <T> T getValue(Object instance) {
        return CastUtils.cast(
            this.varHandle.get(instance)
        );
    }

    /**
     * 设置属性的值
     *
     * @param instance 实例对象
     * @param value    值
     */
    public void setValue(Object instance, Object value) {
        this.varHandle.set(instance, value);
    }

    /**
     * 获取并设置属性的值
     *
     * @param instance 实例对象
     * @param value    新值
     * @param <T>      值类型
     * @return 旧值
     */
    public <T> T getAndSetValue(Object instance, T value) {
        return CastUtils.cast(
            this.varHandle.getAndSet(instance, value)
        );
    }
}
