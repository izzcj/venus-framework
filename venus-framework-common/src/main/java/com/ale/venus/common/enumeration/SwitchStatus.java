package com.ale.venus.common.enumeration;

import lombok.Getter;

/**
 * 状态
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public enum SwitchStatus implements BaseEnum<Integer> {

    /**
     * 禁用
     */
    DISABLE(0, "禁用"),

    /**
     * 启用
     */
    ENABLE(1, "启用");

    SwitchStatus(int value, String msg) {
        init(value, msg);
    }
}
