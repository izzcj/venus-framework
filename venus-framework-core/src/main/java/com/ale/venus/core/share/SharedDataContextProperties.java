package com.ale.venus.core.share;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 共享数据上下文配置
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "venus.shared-data-context")
public class SharedDataContextProperties {

    /**
     * 是否在生命周期中清除缓存
     */
    private boolean clearOnLifecycle;

}
