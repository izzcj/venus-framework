package com.ale.venus.workflow.enumeration;

import com.ale.venus.common.enumeration.BaseEnum;
import lombok.Getter;

/**
 * 流程任务状态
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public enum FlowTaskState implements BaseEnum<String> {

    /**
     * 激活
     */
    ACTIVE("激活"),

    /**
     * 挂起
     */
    SUSPEND("挂起"),

    /**
     * 完成
     */
    COMPLETE("完成"),

    /**
     * 驳回
     */
    REJECT("驳回"),

    /**
     * 撤销
     */
    REVOKE("撤销"),

    /**
     * 回退
     */
    ROLLBACK("回退"),

    /**
     * 超时
     */
    TIMEOUT("超时"),

    /**
     * 自动完成
     */
    AUTO_COMPLETE("自动完成"),

    /**
     * 自动驳回
     */
    AUTO_REJECT("自动驳回"),

    /**
     * 自动结束
     */
    AUTO_FINISH("自动结束");

    /**
     * 名称
     */
    private final String name;

    FlowTaskState(String name) {
        this.name = name;
        this.init(this.toString(), name);
    }
}
