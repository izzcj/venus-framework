package com.ale.venus.workflow.enumeration;

import com.ale.venus.common.enumeration.BaseEnum;
import lombok.Getter;

/**
 * 流程定义状态
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public enum FlowDefinitionState implements BaseEnum<String> {

    /**
     * 激活
     */
    ACTIVE,

    /**
     * 挂起
     */
    SUSPENDED;

    FlowDefinitionState() {
        this.init();
    }
}
