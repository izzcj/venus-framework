package com.ale.venus.workflow.enumeration;

import com.ale.venus.common.enumeration.BaseEnum;
import lombok.Getter;

/**
 * 流程实例状态
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public enum FlowInstanceState implements BaseEnum<String> {

    /**
     * 激活
     */
    ACTIVE("激活"),

    /**
     * 挂起
     */
    SUSPEND("挂起"),

    /**
     * 通过
     */
    COMPLETE("通过"),

    /**
     * 驳回
     */
    REJECT("驳回"),

    /**
     * 回退
     */
    ROLLBACK("回退"),

    /**
     * 撤销
     */
    REVOKE("撤销"),

    /**
     * 超时结束
     */
    TIMEOUT("超时"),

    /**
     * 自动通过
     */
    AUTO_COMPLETE("自动通过"),

    /**
     * 自动拒绝
     */
    AUTO_REJECT("自动驳回");

    /**
     * 名称
     */
    private final String name;

    FlowInstanceState(String name) {
        this.name = name;
        this.init(this.toString(), name);
    }
}
