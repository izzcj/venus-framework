package com.ale.venus.workflow.enumeration;

import com.ale.venus.common.enumeration.BaseEnum;
import lombok.Getter;

/**
 * 无审批人时处理方式
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public enum NoAssigneeStrategy implements BaseEnum<String> {

    /**
     * 自动通过
     */
    AUTO_COMPLETE,

    /**
     * 自动驳回
     */
    AUTO_REJECT;

    NoAssigneeStrategy() {
        this.init();
    }
}
