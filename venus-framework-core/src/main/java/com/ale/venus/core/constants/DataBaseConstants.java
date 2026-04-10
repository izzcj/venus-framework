package com.ale.venus.core.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 数据库常量
 *
 * @author Ale
 * @version 1.0.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataBaseConstants {

    /**
     * 正序排序
     */
    public static final String ORDER_ASC = "asc";

    /**
     * 倒序排序
     */
    public static final String ORDER_DESC = "desc";

    /**
     * 是否删除字段名称
     */
    public static final String DELETED_FIELD_NAME = "deleted";

    /**
     * 创建人字段名称（驼峰类型）
     */
    public static final String CREATE_BY_CAMEL_FIELD_NAME = "createBy";

    /**
     * 最后更新人字段名称（驼峰类型）
     */
    public static final String UPDATE_BY_CAMEL_FIELD_NAME = "updateBy";

    /**
     * 创建时间字段名称（驼峰类型）
     */
    public static final String CREATE_TIME_CAMEL_FIELD_NAME = "createTime";

    /**
     * 最后更新时间字段名称（驼峰类型）
     */
    public static final String UPDATE_TIME_CAMEL_FIELD_NAME = "updateTime";
}
