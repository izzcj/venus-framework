package com.ale.venus.workflow.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于json的变量访问器
 *
 * @author Ale
 * @version 1.0.0
 */
public interface JsonVariableAccessor extends VariableAccessor<String> {

    @Override
    @SuppressWarnings("unchecked")
    default Map<String, Object> getAllVariable() {
        if (StrUtil.isBlank(this.getVariable())) {
            return new HashMap<>();
        }
        return JSON.parseObject(this.getVariable(), Map.class);
    }

    @Override
    default Object getVariableByKey(String variableKey) {
        Map<String, Object> allVariable = this.getAllVariable();
        return allVariable.get(variableKey);
    }

    @Override
    default boolean hasVariable(String variableKey) {
        return this.getVariableByKey(variableKey) != null;
    }

    @Override
    default boolean hasVariableValue(String variableKey, Object variableValue) {
        Object value = this.getVariableByKey(variableKey);
        if (value == null) {
            return false;
        }
        return value.equals(variableValue);
    }

    @Override
    default void addVariable(Map<String, Object> variable) {
        if (CollectionUtil.isNotEmpty(variable)) {
            Map<String, Object> allVariable = this.getAllVariable();
            allVariable.putAll(variable);
            this.setVariable(JSON.toJSONString(allVariable));
        }
    }

    @Override
    default void addVariable(String variableKey, Object variableValue) {
        if (StrUtil.isNotBlank(variableKey) && variableValue != null) {
            this.addVariable(MapUtil.of(variableKey, variableValue));
        }
    }

    @Override
    default void removeVariable(String variableKey) {
        Map<String, Object> allVariable = this.getAllVariable();
        allVariable.remove(variableKey);
        this.setVariable(JSON.toJSONString(allVariable));
    }
}
