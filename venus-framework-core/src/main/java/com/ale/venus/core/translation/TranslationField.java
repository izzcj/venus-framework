package com.ale.venus.core.translation;

import com.ale.venus.common.constants.StringConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 翻译字段注解
 *
 * @author Ale
 * @version 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TranslationField {

    /**
     * 翻译类型，翻译什么种类的数据
     *
     * @return 类型
     */
    String type();

    /**
     * 翻译参数
     * 示例：key=value
     *
     * @return 翻译参数
     */
    String params() default StringConstants.EMPTY;

    /**
     * 存放翻译值字段的名字，默认为翻译字段名字+Name
     *
     * @return 字段名字
     */
    String field() default StringConstants.EMPTY;

}
