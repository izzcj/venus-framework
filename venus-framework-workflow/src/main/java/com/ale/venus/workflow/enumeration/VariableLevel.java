package com.ale.venus.workflow.enumeration;

import com.ale.venus.common.enumeration.BaseEnum;
import lombok.Getter;

/**
 * 变量级别
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public enum VariableLevel implements BaseEnum<String> {

    /**
     * 实例级
     */
    INSTANCE,

    /**
     * 执行
     */
    EXECUTION,

    /**
     * 任务
     */
    TASK;

    VariableLevel() {
        this.init();
    }
}
