package com.ale.venus.core.query.condition;

import com.ale.venus.core.query.QueryParameter;
import com.ale.venus.core.query.QueryType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;

/**
 * 为空
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
public class IsNullCondition implements QueryCondition {

    @Override
    public void build(String fieldName, Object fieldValue, QueryWrapper<?> queryWrapper, QueryParameter[] parameters) {
        queryWrapper.isNull(fieldName);
    }

    @Override
    public QueryType provideQueryType() {
        return QueryType.IS_NULL;
    }
}
