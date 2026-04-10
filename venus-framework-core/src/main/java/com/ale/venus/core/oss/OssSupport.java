package com.ale.venus.core.oss;

import com.ale.venus.common.utils.ImageUtils;
import com.ale.venus.core.exception.OssException;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * 对象存储支持
 *
 * @author Ale
 * @version 1.0.0
 */
@Component
public final class OssSupport {

    /**
     * oss配置
     */
    private static OssProperties ossProperties;

    /**
     * OSS对象存储服务实现
     */
    private static List<OssService> ossServices;

    /**
     * 任务执行器
     */
    private static TaskExecutor taskExecutor;

    public OssSupport(OssProperties ossProperties, List<OssService> ossServices, @Qualifier("applicationTaskExecutor") TaskExecutor taskExecutor) {
        OssSupport.ossProperties = ossProperties;
        OssSupport.ossServices = ossServices;
        OssSupport.taskExecutor = taskExecutor;
    }

    /**
     * 判断对象是否存在
     *
     * @param objectKey 对象Key
     * @return bool
     */
    public static boolean exists(String objectKey) {
        return exists(OssSupport.ossProperties.getDefaultProvider(), objectKey);
    }

    /**
     * 判断对象是否存在
     *
     * @param ossServiceProvider OSS实现
     * @param objectKey          对象Key
     * @return bool
     */
    public static boolean exists(String ossServiceProvider, String objectKey) {
        return exists(
            OssServiceProvider.valueOf(ossServiceProvider),
            objectKey
        );
    }

    /**
     * 判断对象是否存在
     *
     * @param ossServiceProvider OSS实现
     * @param objectKey          对象Key
     * @return bool
     */
    public static boolean exists(OssServiceProvider ossServiceProvider, String objectKey) {
        for (OssService ossService : ossServices) {
            if (ossService.supports(ossServiceProvider)) {
                return ossService.exists(objectKey);
            }
        }

        throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
    }

    /**
     * 获取对象大小
     *
     * @param objectKey 对象Key
     * @return 对象大小
     */
    public static long getSize(String objectKey) {
        return getSize(OssSupport.ossProperties.getDefaultProvider(), objectKey);
    }

    /**
     * 获取对象大小
     *
     * @param ossServiceProvider OSS实现
     * @param objectKey          对象Key
     * @return 对象大小
     */
    public static long getSize(String ossServiceProvider, String objectKey) {
        return getSize(
            OssServiceProvider.valueOf(ossServiceProvider),
            objectKey
        );
    }

    /**
     * 获取对象大小
     *
     * @param ossServiceProvider OSS实现
     * @param objectKey          对象Key
     * @return 对象大小
     */
    public static long getSize(OssServiceProvider ossServiceProvider, String objectKey) {
        for (OssService ossService : ossServices) {
            if (ossService.supports(ossServiceProvider)) {
                return ossService.getSize(objectKey);
            }
        }

        throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
    }

    /**
     * 批量判断对象是否存在
     *
     * @param objectKeys 对象Key集合
     * @return Map
     */
    public static Map<String, Boolean> existsAll(Set<String> objectKeys) {
        return existsAll(OssSupport.ossProperties.getDefaultProvider(), objectKeys);
    }

    /**
     * 批量判断对象是否存在
     *
     * @param ossServiceProvider OSS实现
     * @param objectKeys         对象Key集合
     * @return Map
     */
    public static Map<String, Boolean> existsAll(String ossServiceProvider, Set<String> objectKeys) {
        return existsAll(
            OssServiceProvider.valueOf(ossServiceProvider),
            objectKeys
        );
    }

    /**
     * 批量判断对象是否存在
     *
     * @param ossServiceProvider OSS实现
     * @param objectKeys         对象Key集合
     * @return Map
     */
    public static Map<String, Boolean> existsAll(OssServiceProvider ossServiceProvider, Set<String> objectKeys) {
        OssService used = findOssService(ossServiceProvider);

        var latch = new CountDownLatch(objectKeys.size());
        Map<String, Boolean> result = Maps.newConcurrentMap();
        for (String objectKey : objectKeys) {
            taskExecutor.execute(() -> {
                try {
                    result.put(objectKey, used.exists(objectKey));
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OssException("判断对象是否存在失败: {}", e.getMessage(), e);
        }

        return Maps.newHashMap(result);
    }

    /**
     * 下载对象文件
     *
     * @param objectKey 对象Key
     * @return 输入流
     */
    public static InputStream downloadObject(String objectKey) {
        return downloadObject(OssSupport.ossProperties.getDefaultProvider(), objectKey);
    }

    /**
     * 下载对象文件
     *
     * @param ossServiceProvider OSS实现
     * @param objectKey          对象Key
     * @return 输入流
     */
    public static InputStream downloadObject(String ossServiceProvider, String objectKey) {
        return downloadObject(
            OssServiceProvider.valueOf(ossServiceProvider),
            objectKey
        );
    }

    /**
     * 下载对象文件
     *
     * @param ossServiceProvider OSS实现
     * @param objectKey          对象Key
     * @return 输入流
     */
    public static InputStream downloadObject(OssServiceProvider ossServiceProvider, String objectKey) {
        for (OssService ossService : ossServices) {
            if (ossService.supports(ossServiceProvider)) {
                return ossService.download(objectKey);
            }
        }

        throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
    }

    /**
     * 批量下载对象文件
     *
     * @param objectKeys 对象Key集合
     * @return 输入流Map
     */
    public static Map<String, InputStream> downloadObjects(Set<String> objectKeys) {
        return downloadObjects(OssSupport.ossProperties.getDefaultProvider(), objectKeys);
    }

    /**
     * 批量下载对象文件
     *
     * @param ossServiceProvider OSS实现
     * @param objectKeys         对象Key集合
     * @return 输入流Map
     */
    public static Map<String, InputStream> downloadObjects(String ossServiceProvider, Set<String> objectKeys) {
        return downloadObjects(
            OssServiceProvider.valueOf(ossServiceProvider),
            objectKeys
        );
    }

    /**
     * 批量下载对象文件
     *
     * @param ossServiceProvider OSS实现
     * @param objectKeys         对象Key集合
     * @return 输入流Map
     */
    public static Map<String, InputStream> downloadObjects(OssServiceProvider ossServiceProvider, Set<String> objectKeys) {
        OssService used = findOssService(ossServiceProvider);

        var latch = new CountDownLatch(objectKeys.size());
        Map<String, InputStream> result = Maps.newConcurrentMap();
        for (String objectKey : objectKeys) {
            taskExecutor.execute(() -> {
                try {
                    result.put(objectKey, used.download(objectKey));
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OssException("下载对象文件失败: {}", e.getMessage(), e);
        }

        return Maps.newHashMap(result);
    }

    /**
     * 上传对象文件
     *
     * @param objectKeyPrefix 对象Key前缀
     * @param objectName      对象名称
     * @param objectContent   输入流对象内容
     * @return 对象Key
     */
    public static String uploadObject(String objectKeyPrefix, String objectName, InputStream objectContent) {
        return uploadObject(OssSupport.ossProperties.getDefaultProvider(), objectKeyPrefix, objectName, objectContent);
    }

    /**
     * 上传对象文件
     *
     * @param ossServiceProvider OSS实现
     * @param objectKeyPrefix    对象Key前缀
     * @param objectName         对象名称
     * @param objectContent      输入流对象内容
     * @return 对象Key
     */
    public static String uploadObject(OssServiceProvider ossServiceProvider, String objectKeyPrefix, String objectName, InputStream objectContent) {
        for (OssService ossService : ossServices) {
            if (ossService.supports(ossServiceProvider)) {
                return ossService.upload(objectKeyPrefix, objectName, objectContent);
            }
        }

        throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
    }

    /**
     * 批量上传对象文件
     *
     * @param objectKeyPrefix 对象Key前缀
     * @param objectContents  对象内容
     * @return 对象Key Map
     */
    public static Map<String, String> uploadObjects(String objectKeyPrefix, Map<String, InputStream> objectContents) {
        return uploadObjects(OssSupport.ossProperties.getDefaultProvider(), objectKeyPrefix, objectContents);
    }

    /**
     * 批量上传对象文件
     *
     * @param ossServiceProvider OSS实现
     * @param objectKeyPrefix    对象Key前缀
     * @param objectContents     对象内容
     * @return 对象Key Map
     */
    public static Map<String, String> uploadObjects(OssServiceProvider ossServiceProvider, String objectKeyPrefix, Map<String, InputStream> objectContents) {
        OssService used = findOssService(ossServiceProvider);

        var latch = new CountDownLatch(objectContents.size());
        Map<String, String> result = Maps.newConcurrentMap();
        for (Map.Entry<String, InputStream> entry : objectContents.entrySet()) {
            taskExecutor.execute(() -> {
                try {
                    result.put(entry.getKey(), used.upload(objectKeyPrefix, entry.getKey(), entry.getValue()));
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OssException("上传对象文件失败: {}", e.getMessage(), e);
        }

        return Maps.newHashMap(result);
    }

    /**
     * 上传对象文件
     *
     * @param objectKeyPrefix 对象Key前缀
     * @param objectName      对象名称
     * @param mimeType        Mime类型
     * @param objectContent   输入流对象内容
     * @return 对象Key
     */
    public static String uploadObject(String objectKeyPrefix, String objectName, String mimeType, InputStream objectContent) {
        return uploadObject(OssSupport.ossProperties.getDefaultProvider(), objectKeyPrefix, objectName, mimeType, objectContent);
    }

    /**
     * 上传对象文件
     *
     * @param ossServiceProvider OSS实现
     * @param objectKeyPrefix    对象Key前缀
     * @param objectName         对象名称
     * @param mimeType           Mime类型
     * @param objectContent      输入流对象内容
     * @return 对象Key
     */
    public static String uploadObject(OssServiceProvider ossServiceProvider, String objectKeyPrefix, String objectName, String mimeType, InputStream objectContent) {
        for (OssService ossService : ossServices) {
            if (ossService.supports(ossServiceProvider)) {
                return ossService.upload(objectKeyPrefix, objectName, mimeType, objectContent);
            }
        }

        throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
    }

    /**
     * 批量上传对象文件
     *
     * @param objectKeyPrefix 对象Key前缀
     * @param mimeType        Mime类型
     * @param objectContents  对象内容
     * @return 对象Key Map
     */
    public static Map<String, String> uploadObjects(String objectKeyPrefix, String mimeType, Map<String, InputStream> objectContents) {
        return uploadObjects(OssSupport.ossProperties.getDefaultProvider(), objectKeyPrefix, mimeType, objectContents);
    }

    /**
     * 批量上传对象文件
     *
     * @param ossServiceProvider OSS实现
     * @param objectKeyPrefix    对象Key前缀
     * @param mimeType           Mime类型
     * @param objectContents     对象内容
     * @return 对象Key Map
     */
    public static Map<String, String> uploadObjects(OssServiceProvider ossServiceProvider, String objectKeyPrefix, String mimeType, Map<String, InputStream> objectContents) {
        OssService used = ossServices.stream().filter(ossService -> ossService.supports(ossServiceProvider)).findFirst().orElse(null);
        if (used == null) {
            throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
        }

        var latch = new CountDownLatch(objectContents.size());
        Map<String, String> result = Maps.newConcurrentMap();
        for (Map.Entry<String, InputStream> entry : objectContents.entrySet()) {
            taskExecutor.execute(() -> {
                try {
                    result.put(entry.getKey(), used.upload(objectKeyPrefix, entry.getKey(), mimeType, entry.getValue()));
                } finally {
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OssException("上传对象文件失败: {}", e.getMessage(), e);
        }

        return Maps.newHashMap(result);
    }

    /**
     * 上传图片
     *
     * @param objectName    对象名称
     * @param objectContent 输入流对象内容
     * @return 对象Key
     */
    public static String uploadImage(String objectName, InputStream objectContent) {
        return uploadImage(OssSupport.ossProperties.getDefaultProvider(), objectName, objectContent);
    }

    /**
     * 上传图片
     *
     * @param ossServiceProvider OSS实现
     * @param objectName         对象名称
     * @param objectContent      输入流对象内容
     * @return 对象Key
     */
    public static String uploadImage(OssServiceProvider ossServiceProvider, String objectName, InputStream objectContent) {
        for (OssService ossService : ossServices) {
            if (ossService.supports(ossServiceProvider)) {
                return ossService.upload("image/", objectName, objectContent);
            }
        }

        throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
    }

    /**
     * 压缩并上传图片
     *
     * @param objectName    对象名称
     * @param objectContent 输入流对象内容
     * @return 对象Key
     */
    public static String compressAndUploadImage(String objectName, InputStream objectContent) {
        return compressAndUploadImage(OssSupport.ossProperties.getDefaultProvider(), objectName, objectContent);
    }

    /**
     * 压缩并上传图片
     *
     * @param ossServiceProvider OSS实现
     * @param objectName         对象名称
     * @param objectContent      输入流对象内容
     * @return 对象Key
     */
    public static String compressAndUploadImage(OssServiceProvider ossServiceProvider, String objectName, InputStream objectContent) {
        for (OssService ossService : ossServices) {
            if (ossService.supports(ossServiceProvider)) {
                return ossService.upload("image/", objectName, MediaType.IMAGE_JPEG_VALUE, new ByteArrayInputStream(ImageUtils.compressImage(objectContent).toByteArray()));
            }
        }

        throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
    }

    /**
     * 移动OSS对象
     *
     * @param objectKey 对象Key
     * @return 移动后的对象Key
     */
    public static String moveObject(String objectKey) {
        return moveObject(OssSupport.ossProperties.getDefaultProvider(), objectKey);
    }

    /**
     * 移动OSS对象
     *
     * @param ossServiceProvider OSS实现
     * @param objectKey          对象Key
     * @return 移动后的对象Key
     */
    public static String moveObject(String ossServiceProvider, String objectKey) {
        return moveObject(
            OssServiceProvider.valueOf(ossServiceProvider),
            objectKey
        );
    }

    /**
     * 移动OSS对象
     *
     * @param ossServiceProvider OSS实现
     * @param objectKey          对象Key
     * @return 移动后的对象Key
     */
    public static String moveObject(OssServiceProvider ossServiceProvider, String objectKey) {
        for (OssService ossService : ossServices) {
            if (ossService.supports(ossServiceProvider)) {
                return ossService.moveToPermanentDirectory(objectKey);
            }
        }

        throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
    }

    /**
     * 放回移动后的OSS对象
     *
     * @param objectKey 对象Key
     * @return 移动后的对象Key
     */
    public static String backObject(String objectKey) {
        return backObject(OssSupport.ossProperties.getDefaultProvider(), objectKey);
    }

    /**
     * 放回移动后的OSS对象
     *
     * @param ossServiceProvider OSS实现
     * @param objectKey          对象Key
     * @return 移动后的对象Key
     */
    public static String backObject(String ossServiceProvider, String objectKey) {
        return backObject(
            OssServiceProvider.valueOf(ossServiceProvider),
            objectKey
        );
    }

    /**
     * 放回移动后的OSS对象
     *
     * @param ossServiceProvider OSS实现
     * @param objectKey          对象Key
     * @return 移动后的对象Key
     */
    public static String backObject(OssServiceProvider ossServiceProvider, String objectKey) {
        for (OssService ossService : ossServices) {
            if (ossService.supports(ossServiceProvider)) {
                return ossService.moveToTemporaryDirectory(objectKey);
            }
        }

        throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
    }

    /**
     * 删除OSS对象
     *
     * @param objectKey 对象Key
     */
    public static void removeObject(String objectKey) {
        removeObject(OssSupport.ossProperties.getDefaultProvider(), objectKey);
    }

    /**
     * 删除OSS对象
     *
     * @param ossServiceProvider OSS实现
     * @param objectKey          对象Key
     */
    public static void removeObject(String ossServiceProvider, String objectKey) {
        removeObject(
            OssServiceProvider.valueOf(ossServiceProvider),
            objectKey
        );
    }

    /**
     * 删除OSS对象
     *
     * @param ossServiceProvider OSS实现
     * @param objectKey          对象Key
     */
    public static void removeObject(OssServiceProvider ossServiceProvider, String objectKey) {
        for (OssService ossService : ossServices) {
            if (ossService.supports(ossServiceProvider)) {
                ossService.remove(objectKey);
                return;
            }
        }

        throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
    }

    /**
     * 批量删除OSS对象
     *
     * @param objectKeys 对象Key集合
     */
    public static void removeObjects(Set<String> objectKeys) {
        removeObjects(OssSupport.ossProperties.getDefaultProvider(), objectKeys);
    }

    /**
     * 批量删除OSS对象
     *
     * @param ossServiceProvider OSS实现
     * @param objectKeys         对象Key集合
     */
    public static void removeObjects(String ossServiceProvider, Set<String> objectKeys) {
        removeObjects(
            OssServiceProvider.valueOf(ossServiceProvider),
            objectKeys
        );
    }

    /**
     * 批量删除OSS对象
     *
     * @param ossServiceProvider OSS实现
     * @param objectKeys         对象Key集合
     */
    public static void removeObjects(OssServiceProvider ossServiceProvider, Set<String> objectKeys) {
        for (OssService ossService : ossServices) {
            if (ossService.supports(ossServiceProvider)) {
                ossService.batchRemove(objectKeys);
                return;
            }
        }

        throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
    }

    /**
     * 获取bucket
     *
     * @param ossServiceProvider OSS实现
     * @return bucket
     */
    public static String getBucket(OssServiceProvider ossServiceProvider) {
        OssService used = findOssService(ossServiceProvider);
        return used.getBucket();
    }

    /**
     * 查找OSS服务
     *
     * @param ossServiceProvider OSS实现
     * @return OSS服务
     */
    private static OssService findOssService(OssServiceProvider ossServiceProvider) {
        OssService used = ossServices.stream()
            .filter(ossService -> ossService.supports(ossServiceProvider))
            .findFirst()
            .orElse(null);
        if (used == null) {
            throw new OssException("OSS对象存储[{}]未实现或未启用", ossServiceProvider.name());
        }
        return used;
    }
}
