package com.ale.venus.workflow.parser;

import com.ale.venus.workflow.model.Condition;

/**
 * 条件解析器
 * 主要作用为解析value
 *
 * @author Ale
 * @version 1.0.0
 */
public interface ConditionParser {

    /**
     * 解析条件
     *
     * @param condition 条件
     */
    void parser(Condition condition);

}
