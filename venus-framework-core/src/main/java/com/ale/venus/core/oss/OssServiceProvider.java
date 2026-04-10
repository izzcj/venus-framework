package com.ale.venus.core.oss;

import com.ale.venus.common.enumeration.BaseEnum;

/**
 * Oss服务实现提供器枚举
 *
 * @author Ale
 * @version 1.0.0
 */
public enum OssServiceProvider implements BaseEnum<String> {

    /**
     * minio
     */
    MINIO,

    /**
     * 阿里云OSS
     */
    ALIYUN;

    OssServiceProvider() {
        init();
    }
}
