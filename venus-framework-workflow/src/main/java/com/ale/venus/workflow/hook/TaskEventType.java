package com.ale.venus.workflow.hook;

import com.ale.venus.common.enumeration.BaseEnum;

/**
 * 流程任务事件类型
 *
 * @author Ale
 * @version 1.0.0
 */
public enum TaskEventType implements BaseEnum<String> {

    /**
     * 创建之前
     */
    BEFORE_CREATE,

    /**
     * 创建之后
     */
    AFTER_CREATE,

    /**
     * 更新之前
     */
    BEFORE_UPDATE,

    /**
     * 更新之后
     */
    AFTER_UPDATE,

    /**
     * 完成之前
     */
    BEFORE_COMPLETE,

    /**
     * 完成之后
     */
    AFTER_COMPLETE,

    /**
     * 驳回之前
     */
    BEFORE_REJECT,

    /**
     * 驳回之后
     */
    AFTER_REJECT,

    /**
     * 回退之前
     */
    BEFORE_ROLLBACK,

    /**
     * 回退之后
     */
    AFTER_ROLLBACK,

    /**
     * 转办之前
     */
    BEFORE_TRANSFER,

    /**
     * 转办之后
     */
    AFTER_TRANSFER,

    /**
     * 委派之前
     */
    BEFORE_DELEGATE,

    /**
     * 委派之后
     */
    AFTER_DELEGATE,

    /**
     * 拿回之前
     */
    BEFORE_RECLAIM,

    /**
     * 拿回之后
     */
    AFTER_RECLAIM,

    /**
     * 添加任务处理人之前
     */
    BEFORE_ADD_ASSIGNEE,

    /**
     * 添加任务处理人之后
     */
    AFTER_ADD_ASSIGNEE,

    /**
     * 移除任务处理人之前
     */
    BEFORE_REMOVE_ASSIGNEE,

    /**
     * 移除任务处理人之后
     */
    AFTER_REMOVE_ASSIGNEE;

    TaskEventType() {
        this.init();
    }
}
