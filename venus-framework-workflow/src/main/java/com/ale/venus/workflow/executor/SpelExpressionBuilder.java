package com.ale.venus.workflow.executor;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.workflow.model.Condition;

import java.util.Objects;

/**
 * Spring el表达式构建器
 *
 * @author Ale
 * @version 1.0.0
 */
public class SpelExpressionBuilder implements ExpressionBuilder {

    @Override
    public String build(Condition condition) {
        String field = condition.getField();
        String operator = condition.getOperator();
        Object value = condition.getValue();
        if (StrUtil.isBlank(field) || Objects.isNull(value) || StrUtil.isBlank(operator)) {
            return null;
        }
        if ("in".equals(operator)) {
            return String.format("%s.contains(#%s)", value, field);
        }
        if ("not_in".equals(operator)) {
            return String.format("not %s.contains(#%s)", value, field);
        }
        if (value instanceof String) {
            return String.format("#%s %s '%s'", field, operator, value);
        }
        return String.format("#%s %s %s", field, operator, value);
    }
}
