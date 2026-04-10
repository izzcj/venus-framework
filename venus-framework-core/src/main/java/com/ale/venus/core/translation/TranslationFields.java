package com.ale.venus.core.translation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 翻译字段集合
 *
 * @author Ale
 * @version 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TranslationFields {

    /**
     * 翻译字段集合
     *
     * @return 翻译字段集合
     */
    TranslationField[] value();
}
