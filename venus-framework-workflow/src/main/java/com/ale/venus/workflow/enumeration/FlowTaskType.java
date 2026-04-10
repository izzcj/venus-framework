package com.ale.venus.workflow.enumeration;

import com.ale.venus.common.enumeration.BaseEnum;
import lombok.Getter;

/**
 * 流程任务类型
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public enum FlowTaskType implements BaseEnum<String> {

    /**
     * 开始
     */
    START("start"),

    /**
     * 外部流程
     */
    EXTERNAL("external"),

    /**
     * 互斥分支
     */
    EXCLUSIVE("exclusive"),

    /**
     * 并行分支
     */
    PARALLEL("parallel"),

    /**
     * 条件
     */
    CONDITION("condition"),

    /**
     * 审批
     */
    APPROVAL("approval"),

    /**
     * 抄送
     */
    CARBON_COPY("carbonCopy"),

    /**
     * 触发器
     */
    TRIGGER("trigger"),

    /**
     * 结束
     */
    END("end");

    FlowTaskType(String value) {
        this.init(value);
    }
}
