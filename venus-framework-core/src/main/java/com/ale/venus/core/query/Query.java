package com.ale.venus.core.query;

import java.lang.annotation.*;

/**
 * 查询注解
 *
 * @author Ale
 * @version 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    /**
     * 字段
     * 默认取注解字段名
     *
     * @return 字段
     */
    String column() default "";

    /**
     * 查询类型（默认等值查询）
     *
     * @return 查询类型
     */
    QueryType type() default QueryType.EQ;

    /**
     * 所需的参数
     *
     * @return 参数
     */
    QueryParameter[] parameters() default {};
}
