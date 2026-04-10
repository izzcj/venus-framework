package com.ale.venus.core.query.condition;

import com.ale.venus.core.query.QueryParameter;
import com.ale.venus.core.query.QueryType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * NOT IN
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
public class NotInCondition implements QueryCondition {

    @SuppressWarnings("rawtypes")
    @Override
    public void build(String fieldName, Object fieldValue, QueryWrapper<?> queryWrapper, QueryParameter[] parameters) {
        if (fieldValue instanceof Collection collection) {
            if (collection.isEmpty()) {
                return;
            }
            queryWrapper.notIn(
                fieldName,
                collection
            );
            return;
        }
        throw new IllegalArgumentException("NOT IN 查询字段必须为集合！");
    }

    @Override
    public QueryType provideQueryType() {
        return QueryType.NOT_IN;
    }
}
