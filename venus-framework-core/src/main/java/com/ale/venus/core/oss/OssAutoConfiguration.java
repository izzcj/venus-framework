package com.ale.venus.core.oss;

import com.ale.venus.core.oss.aliyun.AliyunOssService;
import com.ale.venus.core.oss.aliyun.AliyunOssProperties;
import com.ale.venus.core.oss.minio.MinioOssService;
import com.ale.venus.core.oss.minio.MinioProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

/**
 * OSS对象存储自动配置
 *
 * @author Ale
 * @version 1.0.0
 */
@AutoConfiguration
@ComponentScan(basePackageClasses = ComponentScanMark.class)
@Import(OssSupport.class)
@EnableConfigurationProperties({OssProperties.class, MinioProperties.class, AliyunOssProperties.class})
public class OssAutoConfiguration {

    /**
     * OSS对象存储服务临时对象文件移除端点处理器Bean
     *
     * @param ossServices    OSS服务实现
     * @param ossMateService OSS元信息服务
     * @return 端点处理器Bean
     */
    @Bean
    public OssServiceTempObjectRemoveEndpoint ossServiceTempObjectRemovalEndpoint(ObjectProvider<OssService> ossServices, OssMateService ossMateService) {
        return new OssServiceTempObjectRemoveEndpoint(ossServices, ossMateService);
    }

    /**
     * OSS对象存储服务上传端点处理器Bean
     *
     * @param ossServices    OSS服务实现
     * @param ossMateService OSS元信息服务
     * @return 端点处理器Bean
     */
    @Bean
    public OssServiceUploadEndpoint ossServiceUploadEndpoint(ObjectProvider<OssService> ossServices, OssMateService ossMateService) {
        return new OssServiceUploadEndpoint(ossServices, ossMateService);
    }

    /**
     * OSS对象存储服务下载端点处理器Bean
     *
     * @param ossServices    OSS服务实现
     * @param ossMateService OSS元信息服务
     * @return 端点处理器Bean
     */
    @Bean
    public OssServiceDownloadEndpoint ossServiceDownloadEndpoint(ObjectProvider<OssService> ossServices, OssMateService ossMateService) {
        return new OssServiceDownloadEndpoint(ossServices, ossMateService);
    }

    /**
     * OSS对象存储服务获取访问URL端点处理器Bean
     *
     * @param ossMateService OSS元信息服务
     * @return 端点处理器Bean
     */
    @Bean
    public OssServiceVisitUrlEndpoint ossServiceVisitUrlEndpoint(OssMateService ossMateService) {
        return new OssServiceVisitUrlEndpoint(ossMateService);
    }

    /**
     * Minio对象存储服务Bean
     *
     * @param minioProperties 配置属性
     * @return Minio服务Bean
     * @throws Exception 异常
     */
    @Bean
    @ConditionalOnProperty(prefix = "venus.oss.minio", name = "enabled")
    public MinioOssService minioOssService(MinioProperties minioProperties) throws Exception {
        return new MinioOssService(minioProperties);
    }

    /**
     * 阿里云OSS对象存储服务Bean
     *
     * @param aliyunOssProperties 配置属性
     * @return 阿里云OSS服务Bean
     */
    @Bean
    @ConditionalOnProperty(prefix = "venus.oss.aliyun", name = "enabled")
    public AliyunOssService aliyunOssService(AliyunOssProperties aliyunOssProperties) {
        return new AliyunOssService(aliyunOssProperties);
    }
}
