package com.ale.venus.core.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Oss配置
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@ConfigurationProperties(prefix = "venus.oss")
public class OssProperties {

    /**
     * 默认提供器
     */
    private OssServiceProvider defaultProvider = OssServiceProvider.MINIO;

}
