package com.ale.venus.core.query;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.domain.entity.BaseEntity;
import com.ale.venus.common.support.ReflectionField;
import com.ale.venus.common.utils.ReflectionUtils;
import com.ale.venus.core.query.condition.QueryCondition;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 查询条件解析器
 *
 * @author Ale
 * @version 1.0.0
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class QueryConditionResolver {

    /**
     * 查询条件映射
     */
    private static Map<QueryType, QueryCondition> queryConditionMap;

    public QueryConditionResolver(ObjectProvider<QueryCondition> queryConditions) {
        queryConditionMap = queryConditions.orderedStream()
            .collect(
                Collectors.toMap(
                    QueryCondition::provideQueryType,
                    queryCondition -> queryCondition
                )
            );
    }

    /**
     * 解析查询条件
     *
     * @param <E>   实体类型
     * @param query 查询条件
     * @return 查询条件
     */
    public static <E extends BaseEntity> QueryWrapper<E> resolve(BaseQuery query) {
        List<ReflectionField> queryFields = ReflectionUtils.getClassAnnotatedFields(query.getClass(), Query.class);
        QueryWrapper<E> queryWrapper = new QueryWrapper<>();
        for (ReflectionField queryField : queryFields) {
            Object fieldValue = queryField.getValue(query);
            if (fieldValue == null) {
                continue;
            }
            Query annotation = queryField.field().getAnnotation(Query.class);
            String fieldName;
            if (StrUtil.isNotBlank(annotation.column())) {
                fieldName = annotation.column();
            } else {
                fieldName = queryField.field().getName();
            }
            QueryCondition queryCondition = queryConditionMap.get(annotation.type());
            if (queryCondition == null) {
                continue;
            }
            queryCondition.build(StrUtil.toUnderlineCase(fieldName), fieldValue, queryWrapper, annotation.parameters());
        }
        return queryWrapper;
    }

    /**
     * 解析查询条件
     *
     * @param <E>   实体类型
     * @param query 查询条件
     * @return 查询条件
     */
    public static <E extends BaseEntity> LambdaQueryWrapper<E> resolveLambda(BaseQuery query) {
        QueryWrapper<E> queryWrapper = resolve(query);
        return queryWrapper.lambda();
    }
}
