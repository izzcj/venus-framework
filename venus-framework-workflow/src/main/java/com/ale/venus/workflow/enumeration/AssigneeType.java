package com.ale.venus.workflow.enumeration;

import com.ale.venus.common.enumeration.BaseEnum;
import lombok.Getter;

/**
 * 受理类型
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public enum AssigneeType implements BaseEnum<String> {

    /**
     * 转办
     */
    TRANSFER,

    /**
     * 委派
     */
    DELEGATE,

    /**
     * 认领
     */
    CLAIM;

    AssigneeType() {
        this.init();
    }
}
