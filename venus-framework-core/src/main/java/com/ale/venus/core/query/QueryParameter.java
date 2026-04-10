package com.ale.venus.core.query;

import java.lang.annotation.*;

/**
 * 查询参数注解
 *
 * @author Ale
 * @version 1.0.0
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParameter {

    /**
     * 参数名称
     *
     * @return 名称
     */
    String name();

    /**
     * 参数值
     *
     * @return 值
     */
    String value();
}
