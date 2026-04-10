package com.ale.venus.workflow.support;

import java.util.Map;

/**
 * 变量访问器
 * 最后变量以map形式使用
 *
 * @param <T> 变量类型
 * @author Ale
 * @version 1.0.0
 */
public interface VariableAccessor<T> {

    /**
     * 获取变量
     *
     * @return 变量
     */
    T getVariable();

    /**
     * 设置变量
     *
     * @param variable 变量
     */
    void setVariable(T variable);

    /**
     * 获取所有变量
     *
     * @return 所有变量
     */
    Map<String, Object> getAllVariable();

    /**
     * 通过key获取变量
     *
     * @param variableKey 变量key
     * @return 变量
     */
    Object getVariableByKey(String variableKey);

    /**
     * 判断变量是否存在
     *
     * @param variableKey 变量key
     * @return 是否存在
     */
    boolean hasVariable(String variableKey);

    /**
     * 判断变量值是否存在
     *
     * @param variableKey   变量key
     * @param variableValue 变量值
     * @return 存在
     */
    boolean hasVariableValue(String variableKey, Object variableValue);

    /**
     * 添加变量
     *
     * @param variable 添加的变量
     */
    void addVariable(Map<String, Object> variable);

    /**
     * 添加变量
     *
     * @param variableKey   变量key
     * @param variableValue 变量value
     */
    void addVariable(String variableKey, Object variableValue);

    /**
     * 删除变量
     *
     * @param variableKey 删除的变量key
     */
    void removeVariable(String variableKey);
}
