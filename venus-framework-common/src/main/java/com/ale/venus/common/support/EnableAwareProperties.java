package com.ale.venus.common.support;

import lombok.Data;

/**
 * 开关配置属性
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
public abstract class EnableAwareProperties {

    /**
     * 是否开启
     */
    private boolean enabled;
}
