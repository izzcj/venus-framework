package com.ale.venus.core.query;

import com.ale.venus.common.enumeration.BaseEnum;

/**
 * 查询类型枚举
 *
 * @author Ale
 * @version 1.0.0
 */
public enum QueryType implements BaseEnum<String> {

    /**
     * 等于
     */
    EQ,

    /**
     * 不等于
     */
    NE,

    /**
     * 大于等于
     */
    GE,

    /**
     * 大于
     */
    GT,

    /**
     * 小于等于
     */
    LE,

    /**
     * 小于
     */
    LT,

    /**
     * 全模糊
     */
    LIKE_ANYWHERE,

    /**
     * 左模糊
     */
    LIKE_LEFT,

    /**
     * 右模糊
     */
    LIKE_RIGHT,

    /**
     * 为空
     */
    IS_NULL,

    /**
     * 不为空
     */
    IS_NOT_NULL,

    /**
     * in
     */
    IN,

    /**
     * not in
     */
    NOT_IN,

    /**
     * 排序
     */
    SORT;

    QueryType() {
        this.init();
    }
}
