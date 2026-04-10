package com.ale.venus.workflow.parser;

import cn.hutool.core.text.StrPool;
import com.ale.venus.workflow.model.Condition;

import java.util.Collection;
import java.util.StringJoiner;

/**
 * 默认条件解析器
 *
 * @author Ale
 * @version 1.0.0
 */
public class DefaultConditionParser implements ConditionParser {

    @Override
    public void parser(Condition condition) {
        condition.setValue(this.convertValue(condition.getValue()));
    }

    /**
     * 转换值
     *
     * @param value 值
     * @return 转换后的值
     */
    private Object convertValue(Object value) {
        if (value instanceof Collection<?>) {
            StringJoiner joiner = new StringJoiner(",");
            for (Object item : (Collection<?>) value) {
                joiner.add(String.valueOf(this.convertValue(item)));
            }
            return StrPool.C_DELIM_START + joiner.toString() + StrPool.C_DELIM_END;
        }
        return value;
    }
}
