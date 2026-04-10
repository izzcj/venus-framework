package com.ale.venus.core.oss.minio;

import com.ale.venus.common.support.EnableAwareProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * minio配置
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = "venus.oss.minio")
public class MinioProperties extends EnableAwareProperties {

    /**
     * 端点URL
     */
    private String endpoint = "127.0.0.1";

    /**
     * 域名
     * 线上环境使用
     */
    private String domain;

    /**
     * 端口号
     */
    private Integer port = 9000;

    /**
     * 访问Key
     */
    private String accessKey;

    /**
     * 密钥Key
     */
    private String secretKey;

    /**
     * 存储桶
     */
    private String bucket;
}
