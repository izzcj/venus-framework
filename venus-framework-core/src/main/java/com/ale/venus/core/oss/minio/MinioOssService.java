package com.ale.venus.core.oss.minio;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import com.ale.venus.common.constants.StringConstants;
import com.ale.venus.core.exception.OssException;
import com.ale.venus.core.oss.AbstractOssService;
import com.ale.venus.core.oss.OssService;
import com.ale.venus.core.oss.OssServiceProvider;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;

/**
 * minio对象存储服务实现
 *
 * @author Ale
 * @version 1.0.0
 */
@Slf4j
public class MinioOssService extends AbstractOssService {

    /**
     * 对象不存在异常Code
     */
    private static final String OBJECT_NOT_EXISTS_CODE = "NoSuchKey";

    /**
     * Minio配置属性
     */
    private final MinioProperties minioProperties;

    /**
     * Minio客户端
     */
    private final MinioClient minioClient;

    public MinioOssService(MinioProperties minioProperties) throws Exception {
        this.minioProperties = minioProperties;
        this.minioClient = MinioClient.builder()
            .endpoint(minioProperties.getEndpoint(), minioProperties.getPort(), false)
            .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
            .build();

        boolean exists = this.minioClient.bucketExists(
            BucketExistsArgs.builder()
                .bucket(minioProperties.getBucket())
                .build()
        );

        if (exists) {
            return;
        }

        // 创建桶
        this.minioClient.makeBucket(
            MakeBucketArgs.builder()
                .bucket(minioProperties.getBucket())
                .build()
        );
        // 设置桶访问权限
        this.minioClient.setBucketPolicy(
            SetBucketPolicyArgs.builder()
                .bucket(minioProperties.getBucket())
                .config(
                    StrFormatter.format(
                        """
                            {
                              "Version": "2012-10-17",
                              "Statement": [
                                {
                                  "Effect": "Allow",
                                  "Principal": {
                                    "AWS": [
                                      "*"
                                    ]
                                  },
                                  "Action": [
                                    "s3:GetObject"
                                  ],
                                  "Resource": [
                                    "arn:aws:s3:::{}/*"
                                  ]
                                }
                              ]
                            }
                            """,
                        minioProperties.getBucket()
                    )
                )
                .build()
        );
    }

    @Override
    public boolean supports(OssServiceProvider thing) {
        return OssServiceProvider.MINIO.equals(thing);
    }

    @Override
    public String getBaseUrl() {
        try {
            StringBuilder uri = new StringBuilder();
            if (StrUtil.isNotBlank(this.minioProperties.getDomain())) {
                uri.append("https://")
                    .append(this.minioProperties.getDomain())
                    .append(StringConstants.SLASH)
                    .append("minio");
            } else {
                uri.append("http://")
                    .append(this.minioProperties.getEndpoint())
                    .append(StringConstants.COLON)
                    .append(this.minioProperties.getPort());
            }
            uri.append(StringConstants.SLASH)
                .append(this.minioProperties.getBucket())
                .append(StringConstants.SLASH);
            URL url = URI.create(uri.toString()).toURL();
            return url.toString();
        } catch (MalformedURLException e) {
            throw new OssException("[Minio]获取对象文件访问基础URL失败：{}", e, e.getMessage());
        }
    }

    @Override
    public String getBucket() {
        return this.minioProperties.getBucket();
    }

    @Override
    public boolean exists(String objectKey) {
        try {
            this.minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(this.minioProperties.getBucket())
                    .object(objectKey)
                    .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            if (StrUtil.equals(e.errorResponse().code(), OBJECT_NOT_EXISTS_CODE)) {
                return false;
            }

            throw new OssException("[Minio]判断对象文件是否存在出现异常：{}", e, e.errorResponse().message());
        } catch (Exception e) {
            throw new OssException("[Minio]判断对象文件是否存在出现异常：{}", e, e.getMessage());
        }
    }

    @Override
    public long getSize(String objectKey) {
        try {
            return this.minioClient.statObject(
                    StatObjectArgs.builder()
                        .bucket(this.minioProperties.getBucket())
                        .object(objectKey)
                        .build()
                )
                .size();
        } catch (ErrorResponseException e) {
            if (StrUtil.equals(e.errorResponse().code(), OBJECT_NOT_EXISTS_CODE)) {
                throw new OssException("对象文件[{}]不存在", objectKey);
            }

            throw new OssException("[Minio]获取对象文件大小出现异常：{}", e, e.errorResponse().message());
        } catch (Exception e) {
            throw new OssException("[Minio]获取对象文件大小出现异常：{}", e, e.getMessage());
        }
    }

    @Override
    public InputStream download(String objectKey) {
        try {
            return this.minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(this.minioProperties.getBucket())
                    .object(objectKey)
                    .build()
            );
        } catch (ErrorResponseException e) {
            if (StrUtil.equals(e.errorResponse().code(), OBJECT_NOT_EXISTS_CODE)) {
                throw new OssException("[Minio]获取对象文件出现异常， 对象文件[{}]不存在", objectKey);
            }

            throw new OssException("[Minio]获取对象文件出现异常：{}", e, e.errorResponse().message());
        } catch (Exception e) {
            throw new OssException("[Minio]获取对象文件出现异常：{}", e, e.getMessage());
        }
    }

    @Override
    protected String doUpload(String objectKey, InputStream fileContent, String mimeType, InputStream objectContent) {
        try {
            this.minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(this.minioProperties.getBucket())
                            .object(objectKey)
                            .contentType(mimeType)
                            .stream(fileContent, -1, 10485760)
                            .build()
            );

            return objectKey;
        } catch (ErrorResponseException e) {
            throw new OssException("[Minio]上传对象文件出现异常：{}", e, e.errorResponse().message());
        } catch (Exception e) {
            throw new OssException("[Minio]上传对象文件出现异常：{}", e, e.getMessage());
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
            this.minioClient.copyObject(
                CopyObjectArgs.builder()
                    .bucket(this.minioProperties.getBucket())
                    .object(destinationObjectKey)
                    .source(
                        CopySource.builder()
                            .bucket(this.minioProperties.getBucket())
                            .object(sourceObjectKey)
                            .build()
                    )
                    .build()
            );

            this.remove(sourceObjectKey);
        } catch (ErrorResponseException e) {
            if (StrUtil.equals(e.errorResponse().code(), OBJECT_NOT_EXISTS_CODE)) {
                throw new OssException("[Minio]移动对象文件出现异常，对象文件[{}]不存在", sourceObjectKey);
            }

            throw new OssException("[Minio]移动对象文件出现异常：{}", e, e.errorResponse().message());
        } catch (Exception e) {
            throw new OssException("[Minio]移动对象文件出现异常：{}", e, e.getMessage());
        }
    }

    @Override
    public void remove(String objectKey) {
        try {
            this.minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(this.minioProperties.getBucket())
                    .object(objectKey)
                    .build()
            );

            log.info("[Minio]删除对象文件[{}-{}]成功", this.minioProperties.getBucket(), objectKey);
        } catch (ErrorResponseException e) {
            if (StrUtil.equals(e.errorResponse().code(), OBJECT_NOT_EXISTS_CODE)) {
                log.warn("[Minio]需删除的对象文件[{}]不存在", objectKey);
                return;
            }

            throw new OssException("[Minio]删除对象文件出现异常：{}", e, e.errorResponse().message());
        } catch (Exception e) {
            throw new OssException("[Minio]删除对象文件出现异常：{}", e, e.getMessage());
        }
    }

    @Override
    public void batchRemove(Collection<String> objectKeys) {
        Iterable<Result<DeleteError>> results = this.minioClient.removeObjects(
            RemoveObjectsArgs.builder()
                .bucket(this.minioProperties.getBucket())
                .objects(
                    objectKeys.stream()
                        .map(DeleteObject::new)
                        .toList()
                )
                .build()
        );

        for (Result<DeleteError> result : results) {
            try {
                DeleteError deleteError = result.get();
                log.info("[Minio]批量删除对象文件[{}.{}]成功：{}", deleteError.bucketName(), deleteError.objectName(), deleteError.message());
            } catch (ErrorResponseException e) {
                if (StrUtil.equals(e.errorResponse().code(), OBJECT_NOT_EXISTS_CODE)) {
                    log.warn("[Minio]需批量删除的对象文件[{}]不存在", e.errorResponse().objectName());
                    return;
                }

                throw new OssException("[Minio]批量删除对象文件出现异常：{}", e, e.errorResponse().message());
            } catch (Exception e) {
                throw new OssException("[Minio]批量删除对象文件出现异常：{}", e, e.getMessage());
            }
        }
    }
}
