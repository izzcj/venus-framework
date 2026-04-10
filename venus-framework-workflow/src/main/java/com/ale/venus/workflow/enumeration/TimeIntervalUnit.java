package com.ale.venus.workflow.enumeration;

import com.ale.venus.common.enumeration.BaseEnum;
import lombok.Getter;

/**
 * 时间间隔单位
 *
 * @author Ale
 * @version 1.0.0
 */
@Getter
public enum TimeIntervalUnit implements BaseEnum<String> {

    /**
     * 分钟
     */
    MINUTE,

    /**
     * 小时
     */
    HOUR,

    /**
     * 天
     */
    DAY;

    TimeIntervalUnit() {
        this.init();
    }
}
