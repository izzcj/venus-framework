package com.ale.venus.core.oss.aliyun;

import cn.hutool.core.util.StrUtil;
import com.ale.venus.core.exception.OssException;
import com.ale.venus.core.oss.AbstractOssService;
import com.ale.venus.core.oss.OssService;
import com.ale.venus.core.oss.OssServiceProvider;
import com.aliyun.oss.ClientBuilderConfiguration;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.common.comm.SignVersion;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

/**
 * 阿里云对象存储服务实现
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class AliyunOssService extends AbstractOssService {

    /**
     * 阿里云OSS配置属性
     */
    private final AliyunOssProperties aliyunOssProperties;

    /**
     * 阿里云OSS客户端
     */
    private final OSS ossClient;

    public AliyunOssService(AliyunOssProperties aliyunOssProperties) {
        this.aliyunOssProperties = aliyunOssProperties;
        DefaultCredentialProvider credentialProvider = new DefaultCredentialProvider(aliyunOssProperties.getAccessKeyId(), aliyunOssProperties.getAccessKeySecret());
        ClientBuilderConfiguration clientBuilderConfiguration = new ClientBuilderConfiguration();
        clientBuilderConfiguration.setSignatureVersion(SignVersion.V4);
        this.ossClient = OSSClientBuilder.create()
            .endpoint(aliyunOssProperties.getEndpoint())
            .region(aliyunOssProperties.getRegion())
            .credentialsProvider(credentialProvider)
            .clientConfiguration(clientBuilderConfiguration)
            .build();
        // 检查存储桶是否存在，如果不存在则创建
        if (!this.ossClient.doesBucketExist(aliyunOssProperties.getBucket())) {
            // 创建存储桶
            this.ossClient.createBucket(aliyunOssProperties.getBucket());
            // 设置存储桶公共读权限
            this.ossClient.setBucketAcl(aliyunOssProperties.getBucket(), CannedAccessControlList.PublicRead);
        }
    }

    @Override
    public boolean supports(OssServiceProvider thing) {
        return OssServiceProvider.ALIYUN.equals(thing);
    }

    @Override
    public String getBaseUrl() {
        try {
            if (StrUtil.isBlank(this.aliyunOssProperties.getDomain())) {
                throw new OssException("[AliyunOSS]获取对象文件访问基础URL失败，未配置访问域名");
            }
            URL url = URI.create(this.aliyunOssProperties.getDomain()).toURL();
            return url.toString();
        } catch (MalformedURLException e) {
            throw new OssException("[AliyunOSS]获取对象文件访问基础URL失败：{}", e, e.getMessage());
        }
    }

    @Override
    public String getBucket() {
        return this.aliyunOssProperties.getBucket();
    }

    @Override
    public boolean exists(String objectKey) {
        try {
            return this.ossClient.doesObjectExist(this.aliyunOssProperties.getBucket(), objectKey);
        } catch (Exception e) {
            throw new OssException("[AliyunOSS]判断对象文件是否存在出现异常：{}", e, e.getMessage());
        }
    }

    @Override
    public long getSize(String objectKey) {
        try {
            var object = this.ossClient.getObject(this.aliyunOssProperties.getBucket(), objectKey);
            if (object != null) {
                return object.getObjectMetadata().getContentLength();
            }
            throw new OssException("对象文件[{}]不存在", objectKey);
        } catch (Exception e) {
            throw new OssException("[AliyunOSS]获取对象文件大小出现异常：{}", e, e.getMessage());
        }
    }

    @Override
    public InputStream download(String objectKey) {
        try {
            var object = this.ossClient.getObject(new GetObjectRequest(this.aliyunOssProperties.getBucket(), objectKey));
            return object.getObjectContent();
        } catch (Exception e) {
            throw new OssException("[AliyunOSS]获取对象文件出现异常：{}", e, e.getMessage());
        }
    }

    @Override
    protected String doUpload(String objectKey, InputStream fileContent, String mimeType, InputStream objectContent) {
        try {
            // 由于MimeTypeUtils.introspectFileInfo可能会消耗流，我们重新构建原始流
            byte[] contentBytes = fileContent.readAllBytes();

            // 构建上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    this.aliyunOssProperties.getBucket(),
                    objectKey,
                    new ByteArrayInputStream(contentBytes)
            );

            // 设置MIME类型
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(mimeType);
            putObjectRequest.setMetadata(metadata);

            // 执行上传
            this.ossClient.putObject(putObjectRequest);

            return objectKey;
        } catch (Exception e) {
            throw new OssException("[AliyunOSS]上传对象文件出现异常：{}", e, e.getMessage());
        }
    }

    @Override
    public String moveToPermanentDirectory(String objectKey) {
        if (!StrUtil.startWith(objectKey, OssService.TEMPORARY_OBJECT_KEY_PREFIX)) {
            return objectKey;
        }

        String newObjectKey = StrUtil.strip(objectKey, OssService.TEMPORARY_OBJECT_KEY_PREFIX, null);
        this.move(objectKey, newObjectKey);

        return newObjectKey;
    }

    @Override
    public String moveToTemporaryDirectory(String objectKey) {
        if (StrUtil.startWith(objectKey, OssService.TEMPORARY_OBJECT_KEY_PREFIX)) {
            return objectKey;
        }

        String newObjectKey = OssService.TEMPORARY_OBJECT_KEY_PREFIX + objectKey;
        this.move(objectKey, newObjectKey);

        return newObjectKey;
    }

    @Override
    public void move(String sourceObjectKey, String destinationObjectKey) {
        try {
            // 复制对象到新位置
            CopyObjectRequest copyObjectRequest = new CopyObjectRequest(
                this.aliyunOssProperties.getBucket(),
                sourceObjectKey,
                this.aliyunOssProperties.getBucket(),
                destinationObjectKey
            );
            this.ossClient.copyObject(copyObjectRequest);

            // 删除原对象
            this.remove(sourceObjectKey);
        } catch (Exception e) {
            throw new OssException("[AliyunOSS]移动对象文件出现异常：{}", e, e.getMessage());
        }
    }

    @Override
    public void remove(String objectKey) {
        try {
            this.ossClient.deleteObject(this.aliyunOssProperties.getBucket(), objectKey);

            log.info("[AliyunOSS]删除对象文件[{}/{}]成功", this.aliyunOssProperties.getBucket(), objectKey);
        } catch (Exception e) {
            throw new OssException("[AliyunOSS]删除对象文件出现异常：{}", e, e.getMessage());
        }
    }

    @Override
    public void batchRemove(Collection<String> objectKeys) {
        try {
            var keyList = objectKeys.stream().toList();
            this.ossClient.deleteObjects(new DeleteObjectsRequest(this.aliyunOssProperties.getBucket())
                    .withKeys(keyList)
            );

            log.info("[AliyunOSS]批量删除对象文件数量: {}, [{}]", keyList.size(), String.join(", ", keyList));
        } catch (Exception e) {
            throw new OssException("[AliyunOSS]批量删除对象文件出现异常：{}", e, e.getMessage());
        }
    }
}