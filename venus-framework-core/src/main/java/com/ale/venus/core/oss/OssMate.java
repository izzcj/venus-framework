package com.ale.venus.core.oss;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Oss元信息
 *
 * @author Ale
 * @version 1.0.0
 */
@Data
@Builder
public class OssMate {

    /**
     * ID
     */
    private String id;

    /**
     * 对象key
     */
    private String objectKey;

    /**
     * oss服务提供器
     */
    private OssServiceProvider provider;

    /**
     * 访问URL
     */
    private String url;

    /**
     * 引用计数
     */
    private Integer referenceCount;

    /**
     * 对象大小
     */
    private Long size;

    /**
     * 对象Mime类型
     */
    private String mimeType;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后访问时间
     */
    private LocalDateTime lastVisitTime;

}
