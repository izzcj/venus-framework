package com.ale.venus.core.query.condition;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.core.constants.DataBaseConstants;
import com.ale.venus.core.query.QueryParameter;
import com.ale.venus.core.query.QueryType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 排序
 * 支持多字段排序
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
public class SortCondition implements QueryCondition {

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void build(String fieldName, Object fieldValue, QueryWrapper<?> queryWrapper, QueryParameter[] parameters) {
        String order = DataBaseConstants.ORDER_ASC;
        for (QueryParameter parameter : parameters) {
            if ("order".equals(parameter.name())) {
                order = parameter.value();
            }
        }
        List<String> sortFields = new ArrayList<>();
        if (fieldValue instanceof String stringValue) {
            if (stringValue.contains(StringPool.COMMA)) {
                String[] split = stringValue.split(StringPool.COMMA);
                sortFields.addAll(
                    Arrays.stream(split)
                        .map(StrUtil::toUnderlineCase)
                        .toList()
                );
            } else {
                sortFields.add(StrUtil.toUnderlineCase(stringValue));
            }
        } else if (fieldValue instanceof Collection collection) {
            sortFields.addAll(
                collection
                    .stream()
                    .map(item -> StrUtil.toUnderlineCase(item.toString()))
                    .toList()
            );
        } else {
            throw new IllegalArgumentException("排序字段必须为String或Collection");
        }
        queryWrapper.orderBy(
            CollectionUtil.isNotEmpty(sortFields),
            DataBaseConstants.ORDER_ASC.equals(order),
            sortFields
        );
    }

    @Override
    public QueryType provideQueryType() {
        return QueryType.SORT;
    }
}
