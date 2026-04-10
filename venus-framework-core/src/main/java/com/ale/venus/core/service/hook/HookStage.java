package com.ale.venus.core.service.hook;

import com.ale.venus.common.enumeration.BaseEnum;
import lombok.Getter;

/**
 * 钩子阶段
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public enum HookStage implements BaseEnum<String> {

    /**
     * 查询之前
     */
    BEFORE_QUERY("beforeQuery"),

    /**
     * 查询之后
     */
    AFTER_QUERY("afterQuery"),

    /**
     * 查询列表之后
     */
    AFTER_QUERY_LIST("afterQueryList"),

    /**
     * 创建之前
     */
    BEFORE_CREATE("beforeCreate"),

    /**
     * 创建之后
     */
    AFTER_CREATE("afterCreate"),

    /**
     * 批量创建之前
     */
    BEFORE_BATCH_CREATE("beforeBatchCreate"),

    /**
     * 批量创建之后
     */
    AFTER_BATCH_CREATE("afterBatchCreate"),

    /**
     * 修改之前
     */
    BEFORE_MODIFY("beforeModify"),

    /**
     * 修改之后
     */
    AFTER_MODIFY("afterModify"),

    /**
     * 批量修改之前
     */
    BEFORE_BATCH_MODIFY("beforeBatchModify"),

    /**
     * 匹修改后
     */
    AFTER_BATCH_MODIFY("afterBatchModify"),

    /**
     * 保存之前
     */
    BEFORE_SAVE("beforeSave"),

    /**
     * 保存之后
     */
    AFTER_SAVE("afterSave"),

    /**
     * 批量保存之前
     */
    BEFORE_BATCH_SAVE("beforeBatchSave"),

    /**
     * 批量保存之后
     */
    AFTER_BATCH_SAVE("afterBatchSave"),

    /**
     * 删除之前
     */
    BEFORE_DELETE("beforeDelete"),

    /**
     * 删除之后
     */
    AFTER_DELETE("afterDelete"),

    /**
     * 批量删除之前
     */
    BEFORE_BATCH_DELETE("beforeBatchDelete"),

    /**
     * 批量删除之后
     */
    AFTER_BATCH_DELETE("afterBatchDelete"),

    /**
     * 清理钩子上下文之前
     */
    BEFORE_CLEAR_HOOK_CONTEXT("beforeClearHookContext");

    /**
     * 方法名称
     */
    private final String methodName;

    HookStage(String methodName) {
        this.methodName = methodName;
        this.init(methodName);
    }

}
